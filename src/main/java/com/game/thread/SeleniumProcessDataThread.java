package com.game.thread;

import com.game.enums.GameCategoryType;
import com.game.jms.bo.MailBo;
import com.game.model.Game;
import com.game.model.GameCategory;
import com.game.model.ServerArea;
import com.game.plugins.phantomjs.GhostWebDriver;
import com.game.service.IGameCategoryService;
import com.game.service.IGameService;
import com.game.service.ISeleniumProcessHTMLService;
import com.game.service.IServerAreaService;
import com.game.template.TemplateName;
import com.game.template.TemplateService;
import com.game.util.ConfigHelper;
import com.game.util.SeleniumCommonLibs;
import com.game.util.SpringContextUtil;
import com.game.util.redis.RedisCache;
import com.game.util.redis.RedisKey;
import org.apache.log4j.Logger;
import org.springframework.jms.core.JmsTemplate;

import javax.jms.Destination;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by jeniss on 17/12/10.
 */
public class SeleniumProcessDataThread extends Thread {
    private static final Logger logger = Logger.getLogger(SeleniumProcessDataThread.class);

    @Override
    public void run() {
        GhostWebDriver ghostWebDriver = new GhostWebDriver();
        IServerAreaService serverAreaService = (IServerAreaService) SpringContextUtil.getBean("serverAreaService");
        IGameService gameService = (IGameService) SpringContextUtil.getBean("gameService");
        IGameCategoryService gameCategoryService = (IGameCategoryService) SpringContextUtil.getBean("gameCategoryService");
        ISeleniumProcessHTMLService seleniumProcessHTMLService = (ISeleniumProcessHTMLService) SpringContextUtil.getBean("seleniumProcessHTMLService");
        TemplateService templateService = (TemplateService) SpringContextUtil.getBean("templateService");
        JmsTemplate jmsTemplate = (JmsTemplate) SpringContextUtil.getBean("jmsTemplate");
        Destination destination = (Destination) SpringContextUtil.getBean("mailDestination");
        RedisCache redisCache = (RedisCache) SpringContextUtil.getBean("redisCache");

        try {
            // process data
            List<Game> gameList = gameService.getActiveGameList();
            for (Game game : gameList) {
                String url = ConfigHelper.getInstance().getGameUrl() + "gm=" + game.getCode();

                SeleniumCommonLibs.goToPage(ghostWebDriver.getWebDriver(), url);

                List<ServerArea> serverAreaList = serverAreaService.getAllByGameId(game.getId());
                List<GameCategory> gameCategoryList = gameCategoryService.getAllItemCategoriesByGameId(game.getId());
                // Traversal the serverArea of game
                for (ServerArea serverArea : serverAreaList) {
                    // Traversal the childServer area of game
                    for (ServerArea childServer : serverArea.getChildServerAreas()) {
                        SeleniumCommonLibs.goToPage(ghostWebDriver.getWebDriver(), url);

                        // Traversal the itemCategory
                        for (GameCategory itemCategory : gameCategoryList) {
                            // if the category type is equipment, then traversal the keys
                            if (GameCategoryType.equipment.name().equals(itemCategory.getCode())) {
                                boolean isExist = true;
                                List<GameCategory> keyCategoryList = gameCategoryService.getAllKeysByItemCode(GameCategoryType.equipment.name());
                                for (GameCategory keyCategory : keyCategoryList) {
                                    // check this subServer and category whether is done
                                    if (!this.isSubServerAndCategoryDone(redisCache, childServer.getId(), keyCategory.getId())) {
                                        boolean result = seleniumProcessHTMLService.processHtmlAndPost(ghostWebDriver, game, serverArea, childServer, itemCategory, keyCategory);
                                        if (result == false) {
                                            isExist = false;
                                            break;
                                        }
                                        this.saveProcessedRecordToRedis(redisCache, childServer.getId(), keyCategory.getId());
                                    }
                                }
                                if (!isExist) {
                                    break;
                                }
                            } else if (GameCategoryType.gameCoin.name().equals(itemCategory.getCode())) {
                                // check this subServer and category whether is done
                                if (!this.isSubServerAndCategoryDone(redisCache, childServer.getId(), itemCategory.getId())) {
                                    boolean result = seleniumProcessHTMLService.processHtmlAndPost(ghostWebDriver, game, serverArea, childServer, itemCategory, null);
                                    if (!result) {
                                        break;
                                    }
                                    // save the processed subServer and category to redis
                                    this.saveProcessedRecordToRedis(redisCache, childServer.getId(), itemCategory.getId());
                                }
                            }
                        }
                        ghostWebDriver.quit();
                    }

                }
            }
            logger.info("---------------------process data thread end---------------------");
        } catch (Exception e) {
            logger.info("---------------------- send error message to email -----------");
            try {
                String msg = "The Cron of Game is Failed";
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
                jmsTemplate.convertAndSend(destination, mailBo);
            } catch (Exception e1) {
                logger.error(e1);
            }
            logger.error(e);
        } finally {
            ghostWebDriver.quit();
        }
    }

    private void saveProcessedRecordToRedis(RedisCache redisCache, Integer subServerId, Integer categoryId) {
        String keyValue = String.format("%s-%s", String.valueOf(subServerId), String.valueOf(categoryId));
        redisCache.sadd(RedisKey.PROCESSED_SERVER_CATEGORIES, keyValue);
    }

    private boolean isSubServerAndCategoryDone(RedisCache redisCache, Integer subServerId, Integer categoryId) {
        String keyValue = String.format("%s-%s", String.valueOf(subServerId), String.valueOf(categoryId));
        return redisCache.sisMember(RedisKey.PROCESSED_SERVER_CATEGORIES, keyValue);
    }

}
