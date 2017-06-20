package com.game.controller;

import com.game.entity.JsonEntity;
import com.game.enums.TradeStatusType;
import com.game.model.Game;
import com.game.model.GameCategory;
import com.game.model.ServerArea;
import com.game.model.TradeFlow;
import com.game.service.IConfigService;
import com.game.service.IGameCategoryService;
import com.game.service.IGameService;
import com.game.service.IServerAreaService;
import com.game.service.ITradeFlowService;
import com.game.util.StringUtils;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.helper.StringUtil;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jeniss on 17/5/23.
 */
@RestController
@RequestMapping("/cron")
public class CronController {
    private static final Logger logger = Logger.getLogger(CronController.class);

    @Autowired
    private IServerAreaService serverAreaService;
    @Autowired
    private IGameService gameService;
    @Autowired
    private IGameCategoryService gameCategoryService;
    @Autowired
    private IConfigService configService;
    @Autowired
    private ITradeFlowService tradeFlowService;

    @RequestMapping("/crawlingDataToSave.do")
    public JsonEntity crawlingDataToSave() {
        try {


            List<ServerArea> serverAreaList = serverAreaService.getAll();

            List<Game> gameList = gameService.getAllGameList();
            List<GameCategory> gameCategoryList = gameCategoryService.getAllGameCategory();

            String urlPrefix = "http://www.uu898.com/newTrade.aspx?";

            List<TradeFlow> tradeFlowList = new ArrayList<>();

            // Traversal the game
            for (Game game : gameList) {
                // Traversal the gameCategory
                for (GameCategory gameCategory : gameCategoryList) {
                    // Traversal the serverArea of game
                    for (ServerArea serverArea : serverAreaList) {
                        // Traversal the childServer area of game
                        for (ServerArea childServer : serverArea.getChildServerAreas()) {
                            // process the url
                            StringBuilder stringBuilder = new StringBuilder(urlPrefix);
                            stringBuilder.append("gm=" + game.getCode());
                            stringBuilder.append("&c=" + gameCategory.getCode());
                            stringBuilder.append("&area=" + serverArea.getCode());
                            stringBuilder.append("&srv=" + childServer.getCode());
                            String urlStr = stringBuilder.toString();

                            // get the first page info
                            Document document = Jsoup.connect(urlStr).timeout(10000).get();
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
                                    logger.info(tradeStatus);
                                    tradeFlow.setTradeStatus(TradeStatusType.getTradeStatusTypeByDesc(tradeStatus).name());

                                    tradeFlow.setGame(game);
                                    tradeFlow.setServerArea(childServer);
                                    tradeFlow.setGameCategory(gameCategory);

                                    tradeFlowList.add(tradeFlow);
                                }
                            }
                        }
                    }
                }
            }

            tradeFlowService.postTradeFlowBatch(tradeFlowList);

            return new JsonEntity();
        } catch (Exception e) {
            logger.error(Thread.currentThread().getStackTrace()[0].getMethodName(), e);
        }
        return new JsonEntity();
    }
}
