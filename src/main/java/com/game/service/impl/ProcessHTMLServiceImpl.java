package com.game.service.impl;

import com.game.enums.GameCategoryType;
import com.game.enums.TradeStatusType;
import com.game.jms.bo.MailBo;
import com.game.model.Game;
import com.game.model.GameCategory;
import com.game.model.ServerArea;
import com.game.model.TradeFlow;
import com.game.service.IProcessHTMLService;
import com.game.service.ITradeFlowService;
import com.game.template.TemplateName;
import com.game.template.TemplateService;
import com.game.util.ConfigHelper;
import com.game.util.NumberRegExUtil;
import com.game.util.StringUtil;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.jms.Destination;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Created by jennifert on 7/19/2017.
 */
@Service(value = "processHTMLService")
public class ProcessHTMLServiceImpl implements IProcessHTMLService {
    private static final Logger logger = Logger.getLogger(ProcessHTMLServiceImpl.class);

    @Autowired
    private ITradeFlowService tradeFlowService;
    @Autowired
    private TemplateService templateService;
    @Autowired
    private JmsTemplate jmsTemplate;
    @Autowired
    private Destination destination;

    /**
     * process html and post the trade flow
     * @param game
     * @param serverArea
     * @param childServer
     * @param gameCategory: itemCategory / gameCategory
     * @param urlStr
     */
    @Override
    public void processHtmlAndPost(Game game, ServerArea serverArea, ServerArea childServer, GameCategory gameCategory, String urlStr) {
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
                tradeFlowService.postTradeFlowBatch(tradeFlowList);
            }

            // end log, sleep
            String msg = "----------- area:%s,server:%s,category:%s,size:%s -----------";
            logger.info(String.format(msg, serverArea.getName(), childServer.getName(), gameCategory.getName(), tradeFlowList.size()));
            Random random = new Random();
            int sleepTime = (random.nextInt(30) + 30) * 1000;// 30s ~ 60s
            Thread.sleep(sleepTime);
        } catch (Exception e) {
            String msg = String.format("area:%s,server:%s,category:%s", serverArea.getName(), childServer.getName(), gameCategory.getName());

            try {
                logger.info("---------------------- send error message to email -----------");
                Map<String, Object> templateParams = new HashMap<>();
                templateParams.put("msg", msg);
                templateParams.put("url", urlStr);
                templateParams.put("exceptionMsg", e.toString());
                templateParams.put("stacks", e.getStackTrace());
                templateParams.put("htmlContent", e.getMessage());
                String templateHtml = templateService.generate(TemplateName.MAIL_ERROR_MSG, templateParams);
                MailBo mailBo = new MailBo();
                mailBo.setFrom(ConfigHelper.getInstance().getMailUsername());
                mailBo.setMailTo("jenisstest@163.com");
                mailBo.setSubject(msg);
                mailBo.setMsgContent(templateHtml);
                jmsTemplate.convertAndSend(destination, mailBo);
            } catch (Exception e1) {
                logger.error(e1);
            }

            logger.error(Thread.currentThread().getStackTrace()[1].getMethodName() + ", " + msg, e);
        }
    }

    /**
     * get gameCoin tradeFlow
     * @param urlStr
     * @param game
     * @param gameCategory
     * @param childServer
     */
    @Override
    public List<TradeFlow> getGameCoinTradeFlow(String urlStr, Game game, GameCategory gameCategory, ServerArea childServer) throws Exception {
        logger.info("-----------url: " + urlStr);
        Document document = this.getDocument(urlStr);
        Elements contentElement = document.select("div[id=divCommodityLst] ul");
        List<TradeFlow> tradeFlowList = new ArrayList<>();
        if (contentElement != null) {
            for (Element element : contentElement) {
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
     * @param keyCategory
     * @param childServer
     */
    @Override
    public List<TradeFlow> getEquipmentTradeFlow(List<TradeFlow> tradeFlowList, String urlStr, Game game, GameCategory keyCategory, ServerArea childServer) throws Exception {
        urlStr = urlStr.replace(keyCategory.getName(), URLEncoder.encode(keyCategory.getName(), "UTF-8"));

        logger.info("-----------url: " + urlStr);

        Document document = this.getDocument(urlStr);
        Elements contentElement = document.select("div[id=divCommodityLst] ul");
        if (tradeFlowList == null) {
            tradeFlowList = new ArrayList<>();
        }
        if (contentElement != null) {
            for (Element element : contentElement) {
                TradeFlow tradeFlow = null;

                // parse html
                tradeFlow = this.parseHtmlOfEquipment(element, keyCategory);

                tradeFlow.setGame(game);
                tradeFlow.setServerArea(childServer);
                tradeFlow.setGameCategory(keyCategory);

                boolean isAdd = false;
                boolean allNull = true;
                int nullCount = 0;
                if (tradeFlow.getUnitPrice() != null) {
                    int listSeq = 0;
                    for (TradeFlow tradeFlowInList : tradeFlowList) {
                        if (tradeFlowInList.getUnitPrice() != null) {
                            allNull = false;
                            if (tradeFlow.getUnitPrice().doubleValue() < tradeFlowInList.getUnitPrice().doubleValue()) {
                                tradeFlowList.add(listSeq, tradeFlow);
                                isAdd = true;
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

                if (isAdd && (tradeFlowList.size() - nullCount) > 10) {
                    tradeFlowList = tradeFlowList.subList(0, tradeFlowList.size() - 1);
                }
            }
        }

        // get next page url
        Elements pageElement = document.select("ul[id=ulTurnPage] img[alt=下一页]");
        if (pageElement.size() > 0) {
            // sleep
            Random random = new Random();
            int sleepTime = (random.nextInt(30) + 30) * 1000;// 30s ~ 60s
            Thread.sleep(sleepTime);

            String nextUrl = pageElement.get(0).parent().attr("href");
            tradeFlowList = this.getEquipmentTradeFlow(tradeFlowList, nextUrl, game, keyCategory, childServer);
        }
        return tradeFlowList;
    }

    /**
     * parse html by Jsoup
     * @param url
     * @return
     * @throws IOException
     */
    private Document getDocument(String url) throws IOException {
        Document document = Jsoup.connect(url).timeout(10 * 1000).get();
        return document;
    }

    /**
     * parse html of GameCoin
     * @param element
     */
    private TradeFlow parseHtmlOfGameCoin(Element element) throws Exception {
        try {
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
            Elements elements = element.select("li[class=sp_li1] h6 span");
            if (elements.size() > 0) {
                String unitPrice = elements.first().text();
                tradeFlow.setUnitPriceDesc(unitPrice);
            }

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
        } catch (Exception e) {
            throw new Exception(element.html(), e);
        }
    }

    /**
     * parse html of equipment
     * @param element
     */
    private TradeFlow parseHtmlOfEquipment(Element element, GameCategory keyCategory) throws Exception {
        try {
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
            Double count = this.processCount(name);
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
        } catch (Exception e) {
            throw new Exception(element.html(), e);
        }
    }

    /**
     * process the equipment count
     * 1. get the count by romanNum or zhNum list which has the only one data.
     * @param content
     */
    private Double processCount(String content) {
        Double result = 0.0;

        // get result by unit
        List<Double> numberByRomanNum = NumberRegExUtil.getNumberByRomanNum(content, ConfigHelper.getInstance().getRegexWithUnit(), null);
        List<Double> numberByZhNum = NumberRegExUtil.getNumberByZhNum(content, ConfigHelper.getInstance().getRegexWithUnit());
        if (numberByRomanNum.size() == 1 && numberByZhNum.size() == 0) {
            result = numberByRomanNum.get(0);
        } else if (numberByRomanNum.size() == 0 && numberByZhNum.size() == 1) {
            result = numberByZhNum.get(0);
        }

        // get result without unit by roman number
        if (result == 0) {
            numberByRomanNum = NumberRegExUtil.getNumberByRomanNum(content, null, ConfigHelper.getInstance().getRegexWithoutUnit());
            if (numberByRomanNum.size() == 1) {
                result = numberByRomanNum.get(0);
            }
        }

        // get result without roman number
        if (result == 0) {
            if (NumberRegExUtil.checkContentWithoutRomanNumber(content)) {
                result = 1.0;
            }
        }

        return result;
    }
}
