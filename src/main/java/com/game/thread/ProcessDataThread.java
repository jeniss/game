package com.game.thread;

import com.game.enums.GameCategoryType;
import com.game.enums.TradeStatusType;
import com.game.jms.bo.MailBo;
import com.game.model.Game;
import com.game.model.GameCategory;
import com.game.model.ServerArea;
import com.game.model.TradeFlow;
import com.game.service.IGameCategoryService;
import com.game.service.IGameService;
import com.game.service.IServerAreaService;
import com.game.service.ITradeFlowService;
import com.game.util.ConfigHelper;
import com.game.util.NumberRegExUtil;
import com.game.util.SpringContextUtil;
import com.game.util.StringUtil;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.util.CollectionUtils;

import javax.print.attribute.standard.Destination;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by jeniss on 17/6/21.
 */
public class ProcessDataThread extends Thread {
    private static final Logger logger = Logger.getLogger(ProcessDataThread.class);

    @Override
    public void run() {
        IServerAreaService serverAreaService = (IServerAreaService) SpringContextUtil.getBean("serverAreaService");
        IGameService gameService = (IGameService) SpringContextUtil.getBean("gameService");
        IGameCategoryService gameCategoryService = (IGameCategoryService) SpringContextUtil.getBean("gameCategoryService");

        List<ServerArea> serverAreaList = serverAreaService.getAll();

        List<Game> gameList = gameService.getAllGameList();
        List<GameCategory> gameCategoryList = gameCategoryService.getAllItemCategories();

        // Traversal the game
        for (Game game : gameList) {
            // Traversal the serverArea of game
            for (ServerArea serverArea : serverAreaList) {
                // Traversal the childServer area of game
                for (ServerArea childServer : serverArea.getChildServerAreas()) {
                    // Traversal the itemCategory
                    for (GameCategory itemCategory : gameCategoryList) {
                        // process the url
                        StringBuilder urlStringBuilder = this.getUrl(game, itemCategory, serverArea, childServer);

                        // if the category type is equipment, then traversal the keys
                        if (GameCategoryType.equipment.name().equals(itemCategory.getCode())) {
                            List<GameCategory> keyCategoryList = gameCategoryService.getAllKeysByItemCode(GameCategoryType.equipment.name());
                            for (GameCategory keyCategory : keyCategoryList) {
                                this.processHtmlAndPost(game, serverArea, childServer, keyCategory, urlStringBuilder.toString());
                            }
                        } else if (GameCategoryType.gameCoin.name().equals(itemCategory.getCode())) {
                            this.processHtmlAndPost(game, serverArea, childServer, itemCategory, urlStringBuilder.toString());
                        }
                    }
                }
            }
        }
    }

    /**
     * process html and post the trade flow
     * @param game
     * @param serverArea
     * @param childServer
     * @param gameCategory: itemCategory / gameCategory
     * @param urlStr
     */
    private void processHtmlAndPost(Game game, ServerArea serverArea, ServerArea childServer, GameCategory gameCategory, String urlStr) {
        try {
            // get the page info
            List<TradeFlow> tradeFlowList = null;
            if (GameCategoryType.gameCoin.name().equals(gameCategory.getCode())) {
                tradeFlowList = this.getGameCoinTradeFlow(urlStr, game, gameCategory, childServer);
            } else if (GameCategoryType.equipment.name().equals(gameCategory.getCode())) {
                tradeFlowList = this.getEquipmentTradeFlow(null, urlStr, game, gameCategory, childServer);
            }

            // save to DB
            if (!CollectionUtils.isEmpty(tradeFlowList)) {
                ITradeFlowService tradeFlowService = (ITradeFlowService) SpringContextUtil.getBean("tradeFlowService");
                tradeFlowService.postTradeFlowBatch(tradeFlowList);
            }

            // end log, sleep
            String msg = "--------------------------- area:%s,server:%s,category:%s,size:%s ---------------------------";
            logger.info(String.format(msg, serverArea.getName(), childServer.getName(), gameCategory.getName(), tradeFlowList.size()));
            Random random = new Random();
            int sleepTime = (random.nextInt(30) + 30) * 1000;// 30s ~ 60s
            Thread.sleep(sleepTime);
        } catch (Exception e) {
            String msg = String.format("area:%s,server:%s,category:%s", serverArea.getName(), childServer.getName(), gameCategory.getName());

            JmsTemplate jmsTemplate = (JmsTemplate) SpringContextUtil.getBean("jmsTemplate");
            Destination destination = (Destination) SpringContextUtil.getBean("mailDestination");
            MailBo mailBo = new MailBo();
            mailBo.setFrom(ConfigHelper.getInstance().getMailUsername());
            mailBo.setMailTo("jeniss1234@163.com");
            mailBo.setSubject(msg);
            mailBo.setMsgContent(Thread.currentThread().getStackTrace()[0].getMethodName() + ":" + e.getMessage());
            jmsTemplate.convertAndSend(destination);

            logger.error(Thread.currentThread().getStackTrace()[0].getMethodName() + ", " + msg, e);
        }
    }

    /**
     * get gameCoin tradeFlow
     * @param urlStr
     * @param game
     * @param gameCategory
     * @param childServer
     */
    private List<TradeFlow> getGameCoinTradeFlow(String urlStr, Game game, GameCategory gameCategory, ServerArea childServer) throws IOException {
        logger.info("---------------------------url: " + urlStr);
        Document document = Jsoup.connect(urlStr).timeout(10 * 1000).get();
        Elements contentElement = document.select("div[id=divCommodityLst] ul");
        List<TradeFlow> tradeFlowList = new ArrayList<>();
        if (contentElement != null) {
            for (Element element : contentElement) {
                logger.info("--------------------------- html content start ---------------------------");
                logger.info(element.html());
                logger.info("--------------------------- html content end ---------------------------");

                TradeFlow tradeFlow = null;

                // parse html
                tradeFlow = this.parseHtmlOfGameCoin(element);

                tradeFlow.setGame(game);
                tradeFlow.setServerArea(childServer);
                tradeFlow.setGameCategory(gameCategory);

                tradeFlowList.add(tradeFlow);
            }
        }
        return tradeFlowList;
    }

    /**
     * get equipment tradeFlow
     * @param tradeFlowList
     * @param urlStr
     * @param game
     * @param gameCategory
     * @param childServer
     */
    private List<TradeFlow> getEquipmentTradeFlow(List<TradeFlow> tradeFlowList, String urlStr, Game game, GameCategory gameCategory, ServerArea childServer) throws IOException, InterruptedException {
        logger.info("---------------------------url: " + urlStr + "&key=" + gameCategory.getName());
        urlStr = urlStr + "&key=" + URLEncoder.encode(gameCategory.getName(), "UTF-8");

        Document document = Jsoup.connect(urlStr).timeout(10 * 1000).get();

        Elements contentElement = document.select("div[id=divCommodityLst] ul");
        if (tradeFlowList == null) {
            tradeFlowList = new ArrayList<>();
        }
        if (contentElement != null) {
            for (Element element : contentElement) {
                //                logger.info("--------------------------- html content start ---------------------------");
                //                logger.info(element.html());
                //                logger.info("--------------------------- html content end ---------------------------");

                TradeFlow tradeFlow = null;

                // parse html
                tradeFlow = this.parseHtmlOfEquipment(element, gameCategory);

                tradeFlow.setGame(game);
                tradeFlow.setServerArea(childServer);
                tradeFlow.setGameCategory(gameCategory);

                int nullCount = 0;
                if (tradeFlow.getUnitPrice() != null) {
                    int listSeq = 0;
                    boolean allNull = true;
                    for (TradeFlow tradeFlowInList : tradeFlowList) {
                        if (tradeFlowInList.getUnitPrice() != null) {
                            allNull = false;
                            if (tradeFlow.getUnitPrice().doubleValue() < tradeFlowInList.getUnitPrice().doubleValue()) {
                                tradeFlowList.add(listSeq, tradeFlow);
                                break;
                            }
                        } else {
                            nullCount++;
                        }
                        listSeq++;
                    }

                    if (allNull) {
                        tradeFlowList.add(tradeFlow);
                    }
                } else {
                    tradeFlowList.add(0, tradeFlow);
                }

                if (tradeFlowList.size() > 10) {
                    tradeFlowList = tradeFlowList.subList(0, 10 + nullCount);
                }
            }
        }

        // get next page url
        Elements pageElement = document.select("ul[id=ulTurnPage] img");
        if (pageElement.size() > 0) {
            // sleep
            Random random = new Random();
            int sleepTime = (random.nextInt(30) + 30) * 1000;// 30s ~ 60s
            Thread.sleep(sleepTime);

            String nextUrl = pageElement.get(0).parent().attr("href");
            tradeFlowList = this.getEquipmentTradeFlow(null, nextUrl, game, gameCategory, childServer);
        }
        return tradeFlowList;
    }

    /**
     * get the url
     */
    private StringBuilder getUrl(Game game, GameCategory gameCategory, ServerArea serverArea, ServerArea childServer) {
        String urlPrefix = "http://www.uu898.com/newTrade.aspx?";

        StringBuilder stringBuilder = new StringBuilder(urlPrefix);
        stringBuilder.append("gm=" + game.getCode());
        stringBuilder.append("&c=" + gameCategory.getValue());
        stringBuilder.append("&area=" + serverArea.getCode());
        stringBuilder.append("&srv=" + childServer.getCode());

        return stringBuilder;
    }

    /**
     * parse html of GameCoin
     * @param element
     */
    private TradeFlow parseHtmlOfGameCoin(Element element) {
        TradeFlow tradeFlow = new TradeFlow();
        // name
        String name = element.select("li[class=sp_li0 pos] h2 a").text();
        tradeFlow.setName(name);

        // price
        double price = Double.valueOf(element.select("li[class=Red zuan_dh] span").text());
        tradeFlow.setPrice(price);

        // stock
        String stock = element.select("li[class=sp_li3] h5").text();
        if (StringUtil.isNumeric(stock)) {
            tradeFlow.setStock(Integer.valueOf(stock));
        } else {
            tradeFlow.setStock(0);
        }

        // totalPrice
        Double totalPrice = tradeFlow.getPrice() * tradeFlow.getStock();
        tradeFlow.setTotalPrice(totalPrice);

        // unitPriceDesc
        String unitPrice = element.select("li[class=sp_li1] h6 span").first().text();
        tradeFlow.setUnitPriceDesc(unitPrice);

        // tradeStatus:finished,trading,selling
        String tradeStatus = element.select("li[class=sp_li1] a").text();
        // trading
        if (StringUtil.isEmpty(tradeStatus)) {
            tradeStatus = element.select("li[class=sp_li1]>span[class=btn_jyz]").text();
        }
        // finished
        if (StringUtil.isEmpty(tradeStatus)) {
            tradeStatus = element.select("li[class=sp_li1]>span[class=btn_jywc]").text();
        }
        tradeFlow.setTradeStatus(TradeStatusType.getTradeStatusTypeByDesc(tradeStatus).name());
        return tradeFlow;
    }

    /**
     * parse html of equipment
     * @param element
     */
    private TradeFlow parseHtmlOfEquipment(Element element, GameCategory keyCategory) {
        TradeFlow tradeFlow = new TradeFlow();
        // name
        String name = element.select("li[class=sp_li0 pos] h2 a").text();
        tradeFlow.setName(name);

        // price
        double price = Double.valueOf(element.select("li[class=Red zuan_dh] span").text());
        tradeFlow.setPrice(price);

        // stock
        String stock = element.select("li[class=sp_li3] h5").text();
        if (StringUtil.isNumeric(stock)) {
            tradeFlow.setStock(Integer.valueOf(stock));
        } else {
            tradeFlow.setStock(0);
        }

        // unitPrice, unitPriceDesc
        Integer count = this.processCount(name);
        if (count != 0) {
            Integer unitCount = Integer.valueOf(keyCategory.getValue());
            tradeFlow.setUnitPrice((price / count) * unitCount);
            tradeFlow.setUnitPriceDesc(String.format("unit count:%s", unitCount));
        }

        // tradeStatus:finished,trading,selling
        // selling
        String tradeStatus = element.select("li[class=sp_li1] a").text();
        // trading
        if (StringUtil.isEmpty(tradeStatus)) {
            tradeStatus = element.select("li[class=sp_li1]>span[class=btn_jyz]").text();
        }
        // finished
        if (StringUtil.isEmpty(tradeStatus)) {
            tradeStatus = element.select("li[class=sp_li1]>span[class=btn_jywc]").text();
        }
        tradeFlow.setTradeStatus(TradeStatusType.getTradeStatusTypeByDesc(tradeStatus).name());
        return tradeFlow;
    }

    /**
     * process the equipment count
     * @param content
     */
    private Integer processCount(String content) {
        Integer result = 0;

        List<Integer> numberByRomanNum = NumberRegExUtil.getNumberByRomanNum(content);
        List<Integer> numberByZhNum = NumberRegExUtil.getNumberByZhNum(content);

        if (numberByRomanNum.size() == 1 && numberByZhNum.size() == 0) {
            result = numberByRomanNum.get(0);
        } else if (numberByRomanNum.size() == 0 && numberByZhNum.size() == 1) {
            result = numberByZhNum.get(0);
        }

        return result;
    }
}
