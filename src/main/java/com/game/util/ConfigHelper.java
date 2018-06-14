package com.game.util;

import com.game.model.Config;
import com.game.service.IConfigService;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Created by jennifert on 7/17/2017.
 */
public class ConfigHelper {
    private static AtomicReference<ConfigHelper> instance = new AtomicReference<>();
    private static Map<String, String> keyValues = new ConcurrentHashMap<>();

    private ConfigHelper() {
    }

    public static ConfigHelper getInstance() {
        if (instance.get() == null) {
            init();
        }
        return instance.get();
    }

    private static void init() {
        if (instance.get() == null) {
            IConfigService configService = (IConfigService) SpringContextUtil.getBean("configService");
            List<Config> configList = configService.getAllConfig();
            ConfigHelper configHelper = from(configList);
            instance.compareAndSet(null, configHelper);
        }
    }

    private static ConfigHelper from(List<Config> configList) {
        ConfigHelper configHelper = new ConfigHelper();
        for (Config config : configList) {
            configHelper.keyValues.put(config.getCode(), config.getValue());
        }
        return configHelper;
    }

    public Map<String, String> refresh() {
        if (instance.get() != null) {
            IConfigService configService = (IConfigService) SpringContextUtil.getBean("configService");
            List<Config> configList = configService.getAllConfig();
            ConfigHelper configHelper = from(configList);
            instance.get().keyValues.putAll(configHelper.keyValues);
        } else {
            init();
        }
        return instance.get().keyValues;
    }

    private String getValue(String code) {
        return keyValues.get(code);
    }

    private final static String VALID_IP = "valid.ip";
    private final static String MAIL_USERNAME = "mail.username";
    private final static String MAIL_PASSWORD = "mail.password";
    private final static String REGEX_WITH_UNIT = "regex.with.unit";
    private final static String REGEX_WITHOUT_UNIT = "regex.without.unit";
    private final static String GAME_URL = "game.url";
    private final static String WEB_DRIVER_PATH = "web.driver.path";
    private final static String RECEIVE_EMAIL = "receive.email";
    private final static String SELENIUM_LOG_PATH = "selenium.log.path";


    public String getValidIps() {
        return getValue(VALID_IP);
    }

    public String getMailUsername() {
        return getValue(MAIL_USERNAME);
    }

    public String getMailPassword() {
        return getValue(MAIL_PASSWORD);
    }

    public String getRegexWithUnit() {
        return getValue(REGEX_WITH_UNIT);
    }

    public String getRegexWithoutUnit() {
        return getValue(REGEX_WITHOUT_UNIT);
    }

    public String getGameUrl() {
        return getValue(GAME_URL);
    }

    public String getWebDriverPath() {
        return getValue(WEB_DRIVER_PATH);
    }

    public String getReceiveEmail() {
        return getValue(RECEIVE_EMAIL);
    }

    public String getSeleniumLogPath() {
        return getValue(SELENIUM_LOG_PATH);
    }

}
