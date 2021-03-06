package com.game.controller;

import com.game.entity.JsonEntity;
import com.game.model.Game;
import com.game.model.GameCategory;
import com.game.model.ServerArea;
import com.game.plugins.phantomjs.GhostWebDriver;
import com.game.service.IGameCategoryService;
import com.game.service.IGameService;
import com.game.service.ISeleniumProcessHTMLService;
import com.game.service.IServerAreaService;
import com.game.thread.SeleniumProcessDataThread;
import com.game.util.ResponseHelper;
import com.game.util.SeleniumCommonLibs;
import com.game.util.redis.RedisCache;
import com.game.util.redis.RedisKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by jennifert on 7/19/2017.
 */
@RestController
@RequestMapping(value = "/processData")
public class DataController {
    @Autowired
    private IGameCategoryService gameCategoryService;
    @Autowired
    private IGameService gameService;
    @Autowired
    private IServerAreaService serverAreaService;
    @Autowired
    private ISeleniumProcessHTMLService seleniumProcessHTMLService;
    @Autowired
    private ThreadPoolTaskExecutor taskExecutor;
    @Autowired
    private RedisCache redisCache;

    @RequestMapping(value = "/tradeFlowByUrl.do", method = RequestMethod.POST)
    public JsonEntity addTradeFlowByUrl(String url) {
        GhostWebDriver ghostWebDriver = new GhostWebDriver();
        try {
            // process url
            String[] params = url.split("\\?")[1].split("\\&");
            Map<String, String> paramMap = new HashMap<>();
            for (String paramKeyValue : params) {
                paramMap.put(paramKeyValue.split("=")[0], paramKeyValue.split("=")[1]);
            }
            Game game = gameService.getGameByCode(paramMap.get("gm"));
            ServerArea serverArea = serverAreaService.getServerAreaByParentIdAndCode(null, paramMap.get("area"));
            ServerArea childServer = serverAreaService.getServerAreaByParentIdAndCode(serverArea.getId(), paramMap.get("srv"));
            GameCategory itemCategory = gameCategoryService.getItemCategoryByValue(paramMap.get("c"));

            SeleniumCommonLibs.goToPage(ghostWebDriver.getWebDriver(), url);

            if (url.contains("c=-2")) {
                // equipment
                GameCategory keyCategory = gameCategoryService.getGameCategoryByParentIdAndName(itemCategory.getId(), paramMap.get("key"));

                seleniumProcessHTMLService.processHtmlAndPost(ghostWebDriver, game, serverArea, childServer, itemCategory, keyCategory);
            } else if (url.contains("c=-3")) {
                // game coin
                seleniumProcessHTMLService.processHtmlAndPost(ghostWebDriver, game, serverArea, childServer, itemCategory, null);
            }
            return ResponseHelper.createJsonEntity("addTradeFlowByUrl time:" + new Date());
        } catch (Exception e) {
            return ResponseHelper.createJsonEntity(e.getMessage());
        } finally {
            ghostWebDriver.quit();
        }
    }

    @RequestMapping(value = "/tradeFlowWithException.do", method = RequestMethod.GET)
    public JsonEntity<String> test() {
        redisCache.delKey(RedisKey.CRON_EXCEPTION_FLAG);
        // process the data
        SeleniumProcessDataThread thread = new SeleniumProcessDataThread();
        taskExecutor.execute(thread);
        return ResponseHelper.createJsonEntity("crawlingDataToSaveCron time:" + new Date());
    }

    @RequestMapping("/test.do")
    public JsonEntity test(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        System.out.println(ip);

        return ResponseHelper.createJsonEntity(ip);
    }

}
