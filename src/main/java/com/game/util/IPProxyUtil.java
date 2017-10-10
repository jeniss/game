package com.game.util;

import com.game.exception.GetProxyIPException;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;

/**
 * Created by jennifert on 09/25/17.
 * single model
 */
public class IPProxyUtil {
    private static final Logger LOG = Logger.getLogger(IPProxyUtil.class);

    private static class Inner {
        private static final IPProxyUtil INSTANCE = new IPProxyUtil();
    }

    private IPProxyUtil() {
    }

    public static IPProxyUtil getInstance() {
        return Inner.INSTANCE;
    }

    /**
     * check the ip whether is valid
     * @param ip
     * @param port
     * @param processTimes test times
     * @return
     */
    public boolean checkIpValid(String ip, Integer port, int processTimes) {
        if (processTimes > 10) {
            throw new GetProxyIPException("The proxy ip has something wrong.");
        }
        boolean isValid = false;
        String testUrl = "http://www.baidu.com";
        try {
            Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(ip, port));
            HttpURLConnection connection = (HttpURLConnection) new URL(testUrl).openConnection(proxy);
            connection.setConnectTimeout(6000);// 6s
            connection.setReadTimeout(6000);
            connection.setUseCaches(false);

            if (connection.getResponseCode() == 200) {
                isValid = true;
            }
        } catch (MalformedURLException e) {
            LOG.warn(Thread.currentThread().getStackTrace()[1].getMethodName(), e);
        } catch (IOException e) {
            LOG.warn(Thread.currentThread().getStackTrace()[1].getMethodName(), e);
        }
        return isValid;
    }
}
