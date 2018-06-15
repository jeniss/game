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
import org.springframework.util.CollectionUtils;

import javax.jms.Destination;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
        Destination mailDestination = (Destination) SpringContextUtil.getBean("mailDestination");
        Destination cronExceptionDestination = (Destination) SpringContextUtil.getBean("cronExceptionDestination");
        RedisCache redisCache = (RedisCache) SpringContextUtil.getBean("redisCache");

        // get processed data from redis
        Set<String> allCacheProcessedData = redisCache.getAllMembers(RedisKey.PROCESSED_SERVER_CATEGORIES);
        logger.info(String.format("---------------- allCacheProcessedData: %s", String.valueOf(CollectionUtils.isEmpty(allCacheProcessedData) ? 0 : allCacheProcessedData.size())));

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
                        // check all category of subserver whether is processed
                        if (isAllCategoryOfSubServerProcessed(childServer, gameCategoryList, allCacheProcessedData, gameCategoryService)) {
                            continue;
                        }

                        // start to process category
                        SeleniumCommonLibs.goToPage(ghostWebDriver.getWebDriver(), url);

                        // Traversal the itemCategory
                        for (GameCategory itemCategory : gameCategoryList) {
                            // if the category type is equipment, then traversal the keys
                            if (GameCategoryType.equipment.name().equals(itemCategory.getCode())) {
                                boolean isSubServerExist = true;
                                List<GameCategory> keyCategoryList = gameCategoryService.getAllKeysByItemCode(GameCategoryType.equipment.name());
                                for (GameCategory keyCategory : keyCategoryList) {
                                    boolean result = seleniumProcessHTMLService.processHtmlAndPost(ghostWebDriver, game, serverArea, childServer, itemCategory, keyCategory);
                                    if (result == false) {
                                        isSubServerExist = false;
                                        break;
                                    }
                                    // save the processed subServer and category to redis
                                    this.saveProcessedRecordToRedis(redisCache, childServer.getId(), keyCategory.getId());
                                }
                                if (!isSubServerExist) {
                                    break;
                                }
                            } else if (GameCategoryType.gameCoin.name().equals(itemCategory.getCode())) {
                                boolean result = seleniumProcessHTMLService.processHtmlAndPost(ghostWebDriver, game, serverArea, childServer, itemCategory, null);
                                if (!result) {
                                    break;
                                }
                                // save the processed subServer and category to redis
                                this.saveProcessedRecordToRedis(redisCache, childServer.getId(), itemCategory.getId());
                            }
                        }
                        ghostWebDriver.quit();
                    }

                }
            }
        } catch (Exception e) {
            logger.info("---------------------- set exception flag is Y -----------");
            redisCache.set(RedisKey.CRON_EXCEPTION_FLAG, "Y");
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
                jmsTemplate.convertAndSend(mailDestination, mailBo);
            } catch (Exception e1) {
                logger.error(e1);
            }
            logger.error(e);
        } finally {
            ghostWebDriver.quit();
            String cronExceptionFlag = redisCache.get(RedisKey.CRON_EXCEPTION_FLAG);
            if ("Y".equals(cronExceptionFlag)) {
                jmsTemplate.convertAndSend(cronExceptionDestination, "");
                logger.info("---------------------there are some servers cannot be processed---------------------");
            } else {
                logger.info("---------------------process data thread end---------------------");
                String msg = "The Cron of Game is done";
                Map<String, Object> templateParams = new HashMap<>();
                templateParams.put("msg", msg);
                MailBo mailBo = new MailBo();
                mailBo.setFrom(ConfigHelper.getInstance().getMailUsername());
                mailBo.setMailTo(ConfigHelper.getInstance().getReceiveEmail());
                mailBo.setSubject(msg);
                mailBo.setMsgContent(msg);
                jmsTemplate.convertAndSend(mailDestination, mailBo);
            }
        }
    }

    private void saveProcessedRecordToRedis(RedisCache redisCache, Integer subServerId, Integer categoryId) {
        String keyValue = String.format("%s-%s", String.valueOf(subServerId), String.valueOf(categoryId));
        redisCache.sadd(RedisKey.PROCESSED_SERVER_CATEGORIES, keyValue);
    }


    /**
     * check all category of sub server whether is processed
     */
    private boolean isAllCategoryOfSubServerProcessed(ServerArea subServer, List<GameCategory> categories, Set<String> processedData, IGameCategoryService gameCategoryService) {
        boolean isProcessed = true;
        if (CollectionUtils.isEmpty(processedData)) {
            return false;
        }
        for (GameCategory gameCategory : categories) {
            if (GameCategoryType.equipment.name().equals(gameCategory.getCode())) {
                List<GameCategory> keyCategoryList = gameCategoryService.getAllKeysByItemCode(GameCategoryType.equipment.name());
                for (GameCategory keyCategory : keyCategoryList) {
                    String keyValue = String.format("%s-%s", String.valueOf(subServer.getId()), String.valueOf(keyCategory.getId()));
                    if (!processedData.contains(keyValue)) {
                        isProcessed = false;
                        break;
                    }
                }
                if (!isProcessed) {
                    break;
                }
            } else if (GameCategoryType.gameCoin.name().equals(gameCategory.getCode())) {
                String keyValue = String.format("%s-%s", String.valueOf(subServer.getId()), String.valueOf(gameCategory.getId()));
                if (!processedData.contains(keyValue)) {
                    isProcessed = false;
                    break;
                }
            }
        }
        return isProcessed;
    }

}
