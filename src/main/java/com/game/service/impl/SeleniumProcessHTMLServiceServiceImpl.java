package com.game.service.impl;

import com.game.enums.GameCategoryType;
import com.game.enums.TradeStatusType;
import com.game.exception.BizException;
import com.game.exception.ServerNotExistException;
import com.game.jms.bo.MailBo;
import com.game.model.Game;
import com.game.model.GameCategory;
import com.game.model.ServerArea;
import com.game.model.TradeFlow;
import com.game.plugins.phantomjs.GhostWebDriver;
import com.game.service.ISeleniumProcessHTMLService;
import com.game.service.ITradeFlowService;
import com.game.template.TemplateName;
import com.game.template.TemplateService;
import com.game.util.ConfigHelper;
import com.game.util.NumberRegExUtil;
import com.game.util.SeleniumCommonLibs;
import com.game.util.SpringContextUtil;
import com.game.util.StringUtil;
import com.game.util.redis.RedisCache;
import com.game.util.redis.RedisKey;
import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.jms.Destination;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Created by jeniss on 17/12/10.
 */
@Service(value = "seleniumProcessHTMLService")
public class SeleniumProcessHTMLServiceServiceImpl implements ISeleniumProcessHTMLService {
    private final static Logger logger = Logger.getLogger(SeleniumProcessHTMLServiceServiceImpl.class);

    @Autowired
    private TemplateService templateService;
    @Autowired
    private JmsTemplate jmsTemplate;
    @Autowired
    private Destination mailDestination;

    @Autowired
    private ITradeFlowService tradeFlowService;


    /**
     * process html and save the trade flow
     * @param ghostWebDriver
     * @param game
     * @param serverArea
     * @param childServer
     * @param gameCategory   : itemCategory / gameCategory
     * @param keyCategory
     */
    @Override
    public String processHtmlAndPost(GhostWebDriver ghostWebDriver, Game game, ServerArea serverArea, ServerArea childServer, GameCategory gameCategory, GameCategory keyCategory) {
        // sleep
        this.waitForAWhile(null);

        String result = "success";
        String msg = null;
        if (keyCategory == null) {
            msg = "----------- area:%s,server:%s,category:%s -----------";
            logger.info(String.format(msg, serverArea.getName(), childServer.getName(), gameCategory.getName()));
        } else {
            msg = "----------- area:%s,server:%s,category:%s, keyCategory:%s -----------";
            logger.info(String.format(msg, serverArea.getName(), childServer.getName(), gameCategory.getName(), (keyCategory == null ? "" : keyCategory.getName())));
        }
        try {
            List<TradeFlow> tradeFlowList = null;

            if (GameCategoryType.gameCoin.name().equals(gameCategory.getCode())) {
                this.gotoExactListPage(ghostWebDriver.getWebDriver(), serverArea, childServer, gameCategory, null);
                tradeFlowList = this.getGameCoinTradeFlow(ghostWebDriver.getWebDriver(), game, gameCategory, childServer);
            } else if (GameCategoryType.equipment.name().equals(gameCategory.getCode())) {
                this.gotoExactListPage(ghostWebDriver.getWebDriver(), serverArea, childServer, gameCategory, keyCategory.getName());
                tradeFlowList = this.getEquipmentTradeFlow(ghostWebDriver.getWebDriver(), game, keyCategory, childServer);
            }

            // save to DB
            if (!CollectionUtils.isEmpty(tradeFlowList)) {
                tradeFlowService.postTradeFlowBatch(tradeFlowList);
            }

            // end log
            msg = "----------- processed total count:%s -----------";
            logger.info(String.format(msg, tradeFlowList.size()));
        } catch (Exception e) {
            result = "error";
            if (keyCategory == null) {
                msg = "----------- area:%s,server:%s,category:%s -----------";
                msg = String.format(msg, serverArea.getName(), childServer.getName(), gameCategory.getName());
            } else {
                msg = "----------- area:%s,server:%s,category:%s, keyCategory:%s -----------";
                msg = String.format(msg, serverArea.getName(), childServer.getName(), gameCategory.getName(), (keyCategory == null ? "" : keyCategory.getName()));
            }
            logger.error(Thread.currentThread().getStackTrace()[1].getMethodName() + ", " + msg, e);
            List<String> files = new ArrayList<>();
            String exceptionMsg = e.getMessage();
            logger.info(String.format("------- exception:%s, msg:%s", e.getClass(), exceptionMsg));
            if (!(e instanceof WebDriverException && exceptionMsg.contains("org.apache.http.conn.HttpHostConnectException"))) {
                String screenshotFilePath = SeleniumCommonLibs.screenshot(ghostWebDriver.getWebDriver());
                files.add(screenshotFilePath);
            }
            try {
                logger.info("---------------------- send error message to email -----------");
                Map<String, Object> templateParams = new HashMap<>();
                templateParams.put("msg", msg);
                templateParams.put("exceptionMsg", e.toString());
                templateParams.put("stacks", e.getStackTrace());
                templateParams.put("htmlContent", e.getMessage());
                String templateHtml = templateService.generate(TemplateName.MAIL_ERROR_MSG, templateParams);
                MailBo mailBo = new MailBo();
                mailBo.setFrom(ConfigHelper.getInstance().getMailUsername());
                mailBo.setMailTo(ConfigHelper.getInstance().getReceiveEmail());
                mailBo.setSubject(msg);
                mailBo.setMsgContent(templateHtml);
                mailBo.setAttachments(files);
                jmsTemplate.convertAndSend(mailDestination, mailBo);
            } catch (Exception e1) {
                logger.error(e1);
            }

            if (e instanceof ServerNotExistException) {
                result = "ServerNotExistException";
            } else {
                logger.info("---------------------- set exception flag is Y -----------");
                RedisCache redisCache = (RedisCache) SpringContextUtil.getBean("redisCache");
                redisCache.set(RedisKey.CRON_EXCEPTION_FLAG, "Y");
                if (exceptionMsg.contains("org.apache.http.conn.HttpHostConnectException")) {
                    ghostWebDriver.quit();
                    int waitTime = 1000 * 60 * 5;
                    this.waitForAWhile(waitTime);
                }
            }
        }
        return result;
    }

    /**
     * get gameCoin tradeFlow
     * @param webDriver
     * @param game
     * @param gameCategory
     * @param childServer
     */
    private List<TradeFlow> getGameCoinTradeFlow(WebDriver webDriver, Game game, GameCategory gameCategory, ServerArea childServer) throws Exception {
        List<TradeFlow> tradeFlowList = new ArrayList<>();
        // check the commodityLst whether exists.
        if (!SeleniumCommonLibs.isElementExist(webDriver, By.id("divCommodityLst"))) {
            return tradeFlowList;
        }
        WebElement listElement = webDriver.findElement(By.id("divCommodityLst"));

        // check the items whether exists.
        List<WebElement> ulElements = listElement.findElements(By.tagName("ul"));
        if (CollectionUtils.isEmpty(ulElements)) {
            return tradeFlowList;
        }

        // parse html
        for (WebElement ulElement : ulElements) {
            TradeFlow tradeFlow = this.createTradeFlow(ulElement);
            tradeFlow.setGame(game);
            tradeFlow.setServerArea(childServer);
            tradeFlow.setGameCategory(gameCategory);

            tradeFlowList.add(tradeFlow);
        }

        return tradeFlowList;
    }


    /**
     * get equipment tradeFlow
     * @param webDriver
     * @param game
     * @param keyCategory
     * @param childServer
     */
    private List<TradeFlow> getEquipmentTradeFlow(WebDriver webDriver, Game game, GameCategory keyCategory, ServerArea childServer) throws Exception {
        List<TradeFlow> tradeFlowList = this.getEquipmentTradeFlowsInOnePage(webDriver, null, game, keyCategory, childServer);

        if (tradeFlowList.size() > 0) {
            if (!SeleniumCommonLibs.isElementExist(webDriver, By.id("lblPGCount"))) {
                return tradeFlowList;
            }
            // get next page url
            WebElement totalPageCountElement = webDriver.findElement(By.id("lblPGCount"));
            String countStr = totalPageCountElement.getText();
            Integer count = Integer.parseInt(countStr);
            for (int i = 1; i < count; i++) {
                // sleep
                this.waitForAWhile(null);

                Integer index = i + 1;
                // process url
                String currentUrl = webDriver.getCurrentUrl();
                StringBuilder newUrl = new StringBuilder(currentUrl.split("\\?")[0] + "?");
                String[] params = currentUrl.split("\\?")[1].split("\\&");
                for (String paramKeyValue : params) {
                    if (!paramKeyValue.startsWith("p=")) {
                        newUrl.append(paramKeyValue + "&");
                    }
                }
                newUrl.append("p=" + String.valueOf(index));
                logger.info("----------- next page url: " + newUrl);
                webDriver.get(newUrl.toString());
                tradeFlowList = this.getEquipmentTradeFlowsInOnePage(webDriver, tradeFlowList, game, keyCategory, childServer);
            }
        }
        return tradeFlowList;
    }

    private List getEquipmentTradeFlowsInOnePage(WebDriver webDriver, List<TradeFlow> tradeFlowList, Game game, GameCategory keyCategory, ServerArea childServer) {
        if (tradeFlowList == null) {
            tradeFlowList = new ArrayList<>();
        }
        SeleniumCommonLibs.waitPageLoad(webDriver);
        // check the commodityLst whether exists.
        By divCommondityListBy = By.id("divCommodityLst");
        if (!SeleniumCommonLibs.isElementExist(webDriver, divCommondityListBy)) {
            return tradeFlowList;
        }

        WebElement listElement = webDriver.findElement(divCommondityListBy);

        // check the items whether exists.
        List<WebElement> ulElements = listElement.findElements(By.tagName("ul"));
        if (CollectionUtils.isEmpty(ulElements)) {
            return tradeFlowList;
        }
        for (WebElement ulElement : ulElements) {
            // parse html
            TradeFlow tradeFlow = this.createTradeFlow(ulElement);

            String name = tradeFlow.getName();
            Double price = tradeFlow.getPrice();
            // unitPrice, unitPriceDesc
            Double count = this.processCount(name);
            if (count != 0) {
                Integer unitCount = Integer.valueOf(keyCategory.getValue());
                tradeFlow.setUnitPrice((price / count) * unitCount);
                tradeFlow.setUnitPriceDesc(String.format("单位数量:%s;总量:%s", unitCount, count));
            }

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
        return tradeFlowList;
    }

    private TradeFlow createTradeFlow(WebElement ulElement) {
        TradeFlow tradeFlow = new TradeFlow();
        // name
        String name = ulElement.findElement(By.xpath("li[1]/h2/a")).getText();
        tradeFlow.setName(name);

        // price
        String price = ulElement.findElement(By.xpath("li[2]/span")).getText();
        tradeFlow.setPrice(Double.valueOf(price));

        // stock
        String stock = ulElement.findElement(By.xpath("li[3]/h5")).getText();
        if (StringUtil.isNumeric(stock)) {
            tradeFlow.setStock(Integer.valueOf(stock));
        } else {
            tradeFlow.setStock(0);
        }

        // totalPrice
        Double totalPrice = tradeFlow.getPrice() * tradeFlow.getStock();
        tradeFlow.setTotalPrice(totalPrice);

        // unit price desc
        By unitPriceDescBy = By.xpath("li[4]/h6/span[1]");
        if (SeleniumCommonLibs.isElementExist(ulElement, unitPriceDescBy)) {
            String unitPriceDesc = ulElement.findElement(unitPriceDescBy).getText();
            tradeFlow.setUnitPriceDesc(unitPriceDesc);
        }

        // tradeStatus:finished,trading,selling
        // selling
        By sellingBy = By.xpath("li[4]/a");
        WebElement tradeStatusElement = null;
        if (SeleniumCommonLibs.isElementExist(ulElement, sellingBy)) {
            tradeStatusElement = ulElement.findElement(sellingBy);
        }
        // trading
        By tradingBy = By.xpath("li[4]/span[@class='btn_jyz']");
        if (tradeStatusElement == null && SeleniumCommonLibs.isElementExist(ulElement, tradingBy)) {
            tradeStatusElement = ulElement.findElement(tradingBy);
        }
        // finished
        By finishedBy = By.xpath("li[4]/span[@class='btn_jywc']");
        if (tradeStatusElement == null && SeleniumCommonLibs.isElementExist(ulElement, finishedBy)) {
            tradeStatusElement = ulElement.findElement(finishedBy);
        }
        String tradeStatus = tradeStatusElement.getText();
        tradeFlow.setTradeStatus(TradeStatusType.getTradeStatusTypeByDesc(tradeStatus).name());

        // server name
        String bigGameServer = ulElement.findElement(By.xpath("li[1]/p[2]/a[2]")).getText();
        String childServer = ulElement.findElement(By.xpath("li[1]/p[2]/a[3]")).getText();
        tradeFlow.setServerName(String.format("%s / %s", bigGameServer, childServer));

        return tradeFlow;
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
        } else if (numberByRomanNum.size() == 1 && numberByZhNum.size() == 1) {
            if (numberByRomanNum.get(0).doubleValue() == numberByZhNum.get(0).doubleValue()) {
                result = numberByRomanNum.get(0);
            }
        }

        // get result without unit by roman number
        if (result == 0 && numberByRomanNum.size() == 0 && numberByZhNum.size() == 0) {
            numberByRomanNum = NumberRegExUtil.getNumberByRomanNum(content, null, ConfigHelper.getInstance().getRegexWithoutUnit());
            if (numberByRomanNum.size() == 1) {
                result = numberByRomanNum.get(0);
            }
        }

        // get result without roman number
        if (result == 0 && numberByRomanNum.size() == 0 && numberByZhNum.size() == 0) {
            result = 1.0;
        }

        return result;
    }

    /**
     * go to the exact search page by server area and child server area and category
     */
    private void gotoExactListPage(WebDriver webDriver, ServerArea serverArea, ServerArea childServerArea, GameCategory gameCategory, String keyword) {
        SeleniumCommonLibs.waitPageLoad(webDriver);
        try {
            SeleniumCommonLibs.waitElementIsVisible(webDriver, By.id("exactselectbox"), null);
        } catch (Exception e) {
            throw new BizException("The exact search box doesn't exist.");
        }

        boolean isExist = false;
        // choose the big server of game
        webDriver.findElement(By.xpath("//*[@id='exactselectbox']/li[2]")).click();
        WebElement areaListElement = webDriver.findElement(By.id("arealist"));
        List<WebElement> liElements = areaListElement.findElements(By.tagName("li"));
        for (int i = 0; i < liElements.size(); i++) {
            WebElement aElement = liElements.get(i).findElement(By.tagName("a"));
            if (aElement.getText().equals(serverArea.getName())) {
                liElements.get(i).click();
                isExist = true;
                break;
            }
        }
        if (!isExist) {
            throw new ServerNotExistException(String.format("游戏大区:%s 不存在.", serverArea.getName()));
        }
        isExist = false;
        // choose child server of game
        areaListElement = webDriver.findElement(By.id("serverlist"));
        List<WebElement> aElements = areaListElement.findElements(By.tagName("a"));
        for (int i = 0; i < aElements.size(); i++) {
            WebElement aElement = aElements.get(i);
            if (aElement.getText().equals(childServerArea.getName())) {
                aElement.click();
                isExist = true;
                break;
            }
        }
        if (!isExist) {
            throw new ServerNotExistException(String.format("游戏服务器:%s 不存在.", childServerArea.getName()));
        }
        isExist = false;
        // choose game category
        areaListElement = webDriver.findElement(By.id("ctypelist"));
        aElements = areaListElement.findElements(By.tagName("a"));
        for (int i = 0; i < aElements.size(); i++) {
            WebElement aElement = aElements.get(i);
            if (aElement.getText().equals(gameCategory.getName())) {
                aElement.click();
                isExist = true;
                break;
            }
        }
        if (!isExist) {
            throw new ServerNotExistException(String.format("物品类型:%s 不存在.", gameCategory.getName()));
        }
        SeleniumCommonLibs.waitPageLoad(webDriver);
        // keyword
        if (keyword != null) {
            webDriver.findElement(By.id("commonKey")).sendKeys(keyword);
            webDriver.findElement(By.id("serach")).click();
        }
    }

    private void waitForAWhile(Integer waitTime) {
        Integer sleepTime = waitTime;
        if (waitTime == null) {
            Random random = new Random();
            sleepTime = (random.nextInt(20) + 10) * 1000;// 10s ~ 30s
        }
        try {
            Thread.sleep(sleepTime);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
