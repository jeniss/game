package com.game.thread;

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
        ITradeFlowService tradeFlowService = (ITradeFlowService) SpringContextUtil.getBean("tradeFlowService");

        try {
            List<ServerArea> serverAreaList = serverAreaService.getAll();

            List<Game> gameList = gameService.getAllGameList();
            List<GameCategory> gameCategoryList = gameCategoryService.getAllGameCategory();

            String urlPrefix = "http://www.uu898.com/newTrade.aspx?";


            // Traversal the game
            for (Game game : gameList) {
                // Traversal the gameCategory
                for (GameCategory gameCategory : gameCategoryList) {
                    // Traversal the serverArea of game
                    for (ServerArea serverArea : serverAreaList) {
                        // Traversal the childServer area of game
                        for (ServerArea childServer : serverArea.getChildServerAreas()) {
                            List<TradeFlow> tradeFlowList = new ArrayList<>();
                            // process the url
                            StringBuilder stringBuilder = new StringBuilder(urlPrefix);
                            stringBuilder.append("gm=" + game.getCode());
                            stringBuilder.append("&c=" + gameCategory.getCode());
                            stringBuilder.append("&area=" + serverArea.getCode());
                            stringBuilder.append("&srv=" + childServer.getCode());
                            String urlStr = stringBuilder.toString();

                            // get the first page info
                            Document document = Jsoup.connect(urlStr).timeout(10 * 1000).get();
                            Elements contentElement = document.select("div[id=divCommodityLst] ul");
                            if (contentElement != null) {
                                for (Element element : contentElement) {
                                    logger.info("---------------------------");
                                    logger.info(element.html());
                                    logger.info("---------------------------");
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
                                    logger.info(tradeStatus);
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
                                    logger.info("--------------------------- size:0," + urlStr);
                                }
                            } else {
                                logger.info("--------------------------- size:0," + urlStr);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.error(Thread.currentThread().getStackTrace()[0].getMethodName(), e);
        }
    }
}
