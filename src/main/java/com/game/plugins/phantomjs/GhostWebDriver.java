package com.game.plugins.phantomjs;

import com.game.util.ConfigHelper;
import com.game.util.StringUtil;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.remote.DesiredCapabilities;

/**
 * Created by jeniss on 18/2/9.
 */
public class GhostWebDriver {
    private WebDriver webDriver;

    public GhostWebDriver() {
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
        //截屏支持
        dcaps.setCapability("takesScreenshot", true);
        //css搜索支持
        dcaps.setCapability("cssSelectorsEnabled", true);
        //js支持
        dcaps.setJavascriptEnabled(true);
        dcaps.setCapability(PhantomJSDriverService.PHANTOMJS_PAGE_SETTINGS_PREFIX + "userAgent", userAgent);
        webDriver = new PhantomJSDriver(dcaps);
    }

    public WebDriver getWebDriver() {
        return webDriver;
    }
}
