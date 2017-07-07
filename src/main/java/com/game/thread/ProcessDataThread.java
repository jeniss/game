package com.game.thread;

import com.game.enums.GameCategoryType;
import com.game.enums.TradeStatusType;
import com.game.model.Game;
import com.game.model.GameCategory;
import com.game.model.ServerArea;
import com.game.model.TradeFlow;
import com.game.service.IGameCategoryService;
import com.game.service.IGameService;
import com.game.service.IServerAreaService;
import com.game.service.ITradeFlowService;
import com.game.util.NumberRegExUtil;
import com.game.util.SpringContextUtil;
import com.game.util.StringUtils;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.helper.StringUtil;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.util.CollectionUtils;

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

        try {
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
                            String urlStr = this.getUrl(game, itemCategory, serverArea, childServer);

                            // if the category type is equipment, then traversal the keys
                            if (GameCategoryType.equipment.name().equals(itemCategory.getCode())) {
                                List<GameCategory> keyCategoryList = gameCategoryService.getAllKeysByItemCode(GameCategoryType.equipment.name());
                                for (GameCategory key : keyCategoryList) {
                                    // TODO: 7/7/2017  processHtmlAndPost with key
                                }
                            } else if (GameCategoryType.gameCoin.name().equals(itemCategory.getCode())) {
                                this.processHtmlAndPost(game, serverArea, childServer, itemCategory, urlStr);
                            }

                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.error(Thread.currentThread().getStackTrace()[0].getMethodName(), e);
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
            Document document = Jsoup.connect(urlStr).timeout(10 * 1000).get();
            Elements contentElement = document.select("div[id=divCommodityLst] ul");
            if (contentElement != null) {
                List<TradeFlow> tradeFlowList = new ArrayList<>();
                for (Element element : contentElement) {
                    logger.info("--------------------------- html content start ---------------------------");
                    logger.info(element.html());
                    logger.info("--------------------------- html content end ---------------------------");

                    TradeFlow tradeFlow = null;

                    // parse html
                    if (GameCategoryType.gameCoin.getTypeName().equals(gameCategory.getName())) {
                        tradeFlow = this.parseHtmlOfGameCoin(element);
                    } else if (GameCategoryType.equipment.equals(gameCategory.getName())) {
                        tradeFlow = this.parseHtmlOfEquipment(element, gameCategory);
                    }

                    tradeFlow.setGame(game);
                    tradeFlow.setServerArea(childServer);
                    tradeFlow.setGameCategory(gameCategory);

                    tradeFlowList.add(tradeFlow);
                }

                if (!CollectionUtils.isEmpty(tradeFlowList)) {
                    ITradeFlowService tradeFlowService = (ITradeFlowService) SpringContextUtil.getBean("tradeFlowService");
                    tradeFlowService.postTradeFlowBatch(tradeFlowList);

                    String msg = "--------------------------- area:%s,server:%s,category:%s,size:%s ---------------------------";
                    logger.info(String.format(msg, serverArea.getName(), childServer.getName(), gameCategory.getGame(), tradeFlowList.size()));
                    Random random = new Random();
                    int sleepTime = (random.nextInt(30) + 30) * 1000;// 30s ~ 60s
                    Thread.sleep(sleepTime);
                } else {
                    logger.info("--------------------------- content size:0," + urlStr);
                }
            } else {
                logger.info("--------------------------- content size:0," + urlStr);
            }
        } catch (Exception e) {
            String msg = "area:%s,server:%s,category:%s";
            logger.error(Thread.currentThread().getStackTrace()[0].getMethodName() + ", " +
                    String.format(msg, serverArea.getName(), childServer.getName(), gameCategory.getGame()), e);
        }
    }

    /**
     * get the url
     * @return
     */
    private String getUrl(Game game, GameCategory gameCategory, ServerArea serverArea, ServerArea childServer) {
        String urlPrefix = "http://www.uu898.com/newTrade.aspx?";

        StringBuilder stringBuilder = new StringBuilder(urlPrefix);
        stringBuilder.append("gm=" + game.getCode());
        stringBuilder.append("&c=" + gameCategory.getValue());
        stringBuilder.append("&area=" + serverArea.getCode());
        stringBuilder.append("&srv=" + childServer.getCode());

        return stringBuilder.toString();
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
        if (StringUtils.isEmpty(tradeStatus)) {
            tradeStatus = element.select("li[class=sp_li1]>span[class=btn_jyz]").text();
        }
        // finished
        if (StringUtils.isEmpty(tradeStatus)) {
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
            double unitPrice = price / count;
            tradeFlow.setUnitPrice(unitPrice);
            tradeFlow.setUnitPriceDesc(String.format("unit count:%s, price:%s", unitCount, (price / count) * unitCount));
        }

        // tradeStatus:finished,trading,selling
        // selling
        String tradeStatus = element.select("li[class=sp_li1] a").text();
        // trading
        if (StringUtils.isEmpty(tradeStatus)) {
            tradeStatus = element.select("li[class=sp_li1]>span[class=btn_jyz]").text();
        }
        // finished
        if (StringUtils.isEmpty(tradeStatus)) {
            tradeStatus = element.select("li[class=sp_li1]>span[class=btn_jywc]").text();
        }
        tradeFlow.setTradeStatus(TradeStatusType.getTradeStatusTypeByDesc(tradeStatus).name());
        return tradeFlow;
    }

    /**
     * process the equipment count
     * @param content
     * @return
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
