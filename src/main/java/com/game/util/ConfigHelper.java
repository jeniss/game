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

    private String getValue(String code) {
        return keyValues.get(code);
    }

    private final static String VALID_IP = "valid.ip";
    private final static String MAIL_USERNAME = "mail.username";
    private final static String MAIL_PASSWORD = "mail.password";

    public String getValidIp() {
        return getValue(VALID_IP);
    }

    public String getMailUsername() {
        return getValue(MAIL_USERNAME);
    }

    public String getMailPassword() {
        return getValue(MAIL_PASSWORD);
    }
}
