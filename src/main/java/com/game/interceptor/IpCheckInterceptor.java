package com.game.interceptor;

import com.game.util.ConfigHelper;
import org.apache.log4j.Logger;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by jennifert on 7/7/2017.
 */
public class IpCheckInterceptor implements HandlerInterceptor {
    private static final Logger logger = Logger.getLogger(IpCheckInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String validIps = ConfigHelper.getInstance().getValidIp();
        if (validIps.contains(request.getRemoteAddr())) {
            return true;
        }
        logger.info(String.format("ip(%s) is invalid.", request.getRemoteAddr()));
        return false;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }
}
