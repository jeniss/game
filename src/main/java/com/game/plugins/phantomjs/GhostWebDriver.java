package com.game.plugins.phantomjs;

import com.game.util.ConfigHelper;
import com.game.util.StringUtil;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.util.concurrent.TimeUnit;

/**
 * Created by jeniss on 18/2/9.
 */
public class GhostWebDriver {
    private PhantomJSDriver webDriver;

    public PhantomJSDriver createWebDriver() {
        //        String web_driver = "webdriver.chrome.driver";
        //        String web_driver_name = "chromedriver"
        String webDriverPropertyName = "phantomjs.binary.path";
        String webDriverName = "phantomjs";
        if (StringUtil.isEmpty(System.getProperty(webDriverPropertyName))) {
            System.setProperty(webDriverPropertyName, ConfigHelper.getInstance().getWebDriverPath() + webDriverName);
        }
        //        webDriver = new ChromeDriver();

        String userAgent = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.132 Safari/537.36";

        DesiredCapabilities dcaps = new DesiredCapabilities();
        //ssl证书支持
        dcaps.setCapability("acceptSslCerts", true);
        //css搜索支持
        dcaps.setCapability("cssSelectorsEnabled", true);
        //js支持
        dcaps.setJavascriptEnabled(true);
        // load image
        dcaps.setCapability("--load-images", false);
        // user agent
        dcaps.setCapability(PhantomJSDriverService.PHANTOMJS_PAGE_SETTINGS_PREFIX + "userAgent", userAgent);
        // screenshot
        dcaps.setCapability("takesScreenshot", true);

        PhantomJSDriver driver = new PhantomJSDriver(dcaps);
        //设置隐性等待（作用于全局）
        driver.manage().timeouts().implicitlyWait(1, TimeUnit.SECONDS);
        return driver;
    }

    public PhantomJSDriver getWebDriver() {
        if (webDriver == null) {
            webDriver = this.createWebDriver();
        } else {
            if (webDriver.getSessionId() == null) {
                webDriver = this.createWebDriver();
            }
        }
        return webDriver;
    }

    public void quit() {
        if (webDriver != null) {
            webDriver.quit();
        }
    }
}
