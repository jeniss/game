package com.game.util;

import com.game.exception.BizException;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.NoSuchElementException;

/**
 * Created by jeniss on 18/2/2.
 */
public class SeleniumCommonLibs {
    static final int DEFAULT_TIMEOUT = 60; // second

    public static void goToPage(WebDriver webDriver, String url) throws InterruptedException {
        webDriver.get(url);
        SeleniumCommonLibs.waitPageLoad(webDriver);
        String curUrl = webDriver.getCurrentUrl();
        boolean isCorrectPage = false;
        for (int i = 0; i < 3; i++) {
            String[] urlArray = url.split("//");
            String[] curUrlArray = curUrl.split("//");
            if (urlArray[1].equals(curUrlArray[1])) {
                isCorrectPage = true;
                break;
            } else {
                Thread.sleep(1000 * 3);
                webDriver.get(url);
                SeleniumCommonLibs.waitPageLoad(webDriver);
                curUrl = webDriver.getCurrentUrl();
            }
        }
        if (!isCorrectPage) {
            throw new BizException(String.format("The loading page is failed, request url:%s; page url:%s", url, curUrl));
        }
    }

    public static boolean isElementExist(WebDriver webDriver, By elementBy) {
        try {
            webDriver.findElement(elementBy);
        } catch (NoSuchElementException e) {
            return false;
        }
        return true;
    }

    public static boolean isElementExist(WebElement parentElement, By elementBy) {
        try {
            List<WebElement> children = parentElement.findElements(elementBy);
            if (CollectionUtils.isEmpty(children)) {
                return false;
            }
        } catch (NoSuchElementException e) {
            return false;
        }
        return true;
    }

    public static void waitPageLoad(WebDriver webDriver) {
        WebDriverWait wait = new WebDriverWait(webDriver, DEFAULT_TIMEOUT);
        wait.until(webDriver1 -> ((JavascriptExecutor) webDriver).executeScript("return document.readyState").equals("complete"));
    }

    public static void waitPageContainElement(WebDriver webDriver, By elementBy) {
        WebDriverWait wait = new WebDriverWait(webDriver, DEFAULT_TIMEOUT);
        wait.until(ExpectedConditions.presenceOfElementLocated(elementBy));
    }
}
