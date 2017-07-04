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
            List<GameCategory> gameCategoryList = gameCategoryService.getAllGameCategory();

            // Traversal the game
            for (Game game : gameList) {
                // Traversal the gameCategory
                for (GameCategory gameCategory : gameCategoryList) {
                    // Traversal the serverArea of game
                    for (ServerArea serverArea : serverAreaList) {
                        // Traversal the childServer area of game
                        for (ServerArea childServer : serverArea.getChildServerAreas()) {
                            try {
                                this.processData(game, gameCategory, serverArea, childServer);
                            } catch (Exception e) {
                                String msg = "area:%s,server:%s,category:%s";
                                logger.error(Thread.currentThread().getStackTrace()[0].getMethodName() + ", " + String.format(msg, serverArea.getId(), childServer.getId(),
                                        gameCategory.getId()), e);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.error(Thread.currentThread().getStackTrace()[0].getMethodName(), e);
        }
    }

                            // get the first page info
                            Document document = Jsoup.connect(urlStr).timeout(10 * 1000).get();
                            Elements contentElement = document.select("div[id=divCommodityLst] ul");
                            if (contentElement != null) {
                                for (Element element : contentElement) {
                                    logger.info("---------------------------");
                                    logger.info(element.html());
                                    logger.info("---------------------------");
    /**
     * process the data of html, save it to DB
     * @param game
     * @param gameCategory
     * @param serverArea
     * @param childServer
     * @throws Exception
     */
    private void processData(Game game, GameCategory gameCategory, ServerArea serverArea, ServerArea childServer) throws Exception {
        String urlPrefix = "http://www.uu898.com/newTrade.aspx?";
        ITradeFlowService tradeFlowService = (ITradeFlowService) SpringContextUtil.getBean("tradeFlowService");

                                    TradeFlow tradeFlow = null;
        List<TradeFlow> tradeFlowList = new ArrayList<>();
        // process the url
        StringBuilder stringBuilder = new StringBuilder(urlPrefix);
        stringBuilder.append("gm=" + game.getCode());
        stringBuilder.append("&c=" + gameCategory.getCode());
        stringBuilder.append("&area=" + serverArea.getCode());
        stringBuilder.append("&srv=" + childServer.getCode());
        String urlStr = stringBuilder.toString();

                                    // parse html
                                    if (GameCategoryType.gameCoin.getTypeName().equals(gameCategory.getName())) {
                                        tradeFlow = this.parseHtmlOfGameCoin(element);
                                    } else if (GameCategoryType.equipment.getTypeName().equals(gameCategory.getName())) {
        // get the first page info
        Document document = Jsoup.connect(urlStr).timeout(10 * 1000).get();
        Elements contentElement = document.select("div[id=divCommodityLst] ul");
        if (contentElement != null) {
            for (Element element : contentElement) {
                logger.info("--------------------------- html content start ---------------------------");
                logger.info(element.html());
                logger.info("--------------------------- html content end ---------------------------");
                TradeFlow tradeFlow = new TradeFlow();
                // name
                String name = element.select("li[class=sp_li0 pos] h2 a").text();
                tradeFlow.setName(name);

                                    }
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

                // unitPrice
                String unitPrice = element.select("li[class=sp_li1] h6 span").first().text();
                tradeFlow.setUnitPrice(unitPrice);

                // tradeStatus
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

                tradeFlow.setGame(game);
                tradeFlow.setServerArea(childServer);
                tradeFlow.setGameCategory(gameCategory);

                tradeFlowList.add(tradeFlow);
            }

            if (!CollectionUtils.isEmpty(tradeFlowList)) {
                tradeFlowService.postTradeFlowBatch(tradeFlowList);

                String msg = "--------------------------- area:%s,server:%s,category:%s,size:%s ---------------------------";
                logger.info(String.format(msg, serverArea.getId(), childServer.getId(), gameCategory.getId(), tradeFlowList.size()));
                Random random = new Random();
                int sleepTime = (random.nextInt(30) + 30) * 1000;// 30s ~ 60s
                Thread.sleep(sleepTime);
            } else {
                logger.info("--------------------------- content size:0," + urlStr);
            }
        } else {
            logger.info("--------------------------- content size:0," + urlStr);
        }
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

        // unitPrice
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
    private TradeFlow parseHtmlOfEquipment(Element element) {
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

        // unitPrice: calculate with the title and price


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
}
