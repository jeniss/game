package com.game.util;

import org.apache.log4j.Logger;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

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

    public Map<String, Integer> getValidProxyIps(Map<String, Integer> proxyIps) {
        if (CollectionUtils.isEmpty(proxyIps)) {
            return null;
        }
        Map<String, Integer> result = new HashMap<String, Integer>();
        for (Map.Entry entry : proxyIps.entrySet()) {
            String ip = String.valueOf(entry.getKey());
            Integer port = (Integer) entry.getValue();
            boolean isValid = this.checkIpValid(ip, port);
            if (isValid) {
                result.put(ip, port);
            }
        }
        return result;
    }

    public boolean checkIpValid(String ip, Integer port) {
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
//            LOG.error(Thread.currentThread().getStackTrace()[1].getMethodName(), e);
        } catch (IOException e) {
//            LOG.error(Thread.currentThread().getStackTrace()[1].getMethodName(), e);
        }
        return isValid;
    }

    public static void main(String[] args) {
        String ip = "123.182.137.47";
        Integer port = 9000;
        System.out.println("result:" + IPProxyUtil.getInstance().checkIpValid(ip, port));
    }
}
