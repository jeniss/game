package com.game.thread;

import com.game.enums.GameCategoryType;
import com.game.model.Game;
import com.game.model.GameCategory;
import com.game.model.ServerArea;
import com.game.plugins.phantomjs.GhostWebDriver;
import com.game.service.IGameCategoryService;
import com.game.service.IGameService;
import com.game.service.ISeleniumProcessHTMLService;
import com.game.service.IServerAreaService;
import com.game.util.ConfigHelper;
import com.game.util.SpringContextUtil;
import org.apache.log4j.Logger;
import org.openqa.selenium.WebDriver;

import java.util.List;

/**
 * Created by jeniss on 17/12/10.
 */
public class SeleniumProcessDataThread extends Thread {
    private static final Logger logger = Logger.getLogger(SeleniumProcessDataThread.class);

    @Override
    public void run() {
        IServerAreaService serverAreaService = (IServerAreaService) SpringContextUtil.getBean("serverAreaService");
        IGameService gameService = (IGameService) SpringContextUtil.getBean("gameService");
        IGameCategoryService gameCategoryService = (IGameCategoryService) SpringContextUtil.getBean("gameCategoryService");
        ISeleniumProcessHTMLService seleniumProcessHTMLService = (ISeleniumProcessHTMLService) SpringContextUtil.getBean("seleniumProcessHTMLService");

        WebDriver webDriver = (new GhostWebDriver()).getWebDriver();

        List<Game> gameList = gameService.getActiveGameList();
        for (Game game : gameList) {
            String url = ConfigHelper.getInstance().getGameUrl() + "gm=" + game.getCode();

            webDriver.get(url);

            List<ServerArea> serverAreaList = serverAreaService.getAllByGameId(game.getId());
            List<GameCategory> gameCategoryList = gameCategoryService.getAllItemCategoriesByGameId(game.getId());
            // Traversal the serverArea of game
            for (ServerArea serverArea : serverAreaList) {
                // Traversal the childServer area of game
                for (ServerArea childServer : serverArea.getChildServerAreas()) {
                    // Traversal the itemCategory
                    for (GameCategory itemCategory : gameCategoryList) {
                        // if the category type is equipment, then traversal the keys
                        if (GameCategoryType.equipment.name().equals(itemCategory.getCode())) {
                            boolean isExist = true;
                            List<GameCategory> keyCategoryList = gameCategoryService.getAllKeysByItemCode(GameCategoryType.equipment.name());
                            for (GameCategory keyCategory : keyCategoryList) {
                                boolean result = seleniumProcessHTMLService.processHtmlAndPost(webDriver, game, serverArea, childServer, itemCategory, keyCategory);
                                if (result == false) {
                                    isExist = false;
                                    break;
                                }
                            }
                            if (!isExist) {
                                break;
                            }
                        } else if (GameCategoryType.gameCoin.name().equals(itemCategory.getCode())) {
                            boolean result = seleniumProcessHTMLService.processHtmlAndPost(webDriver, game, serverArea, childServer, itemCategory, null);
                            if (!result) {
                                break;
                            }
                        }
                    }
                }

            }
        }
        webDriver.close();
        logger.info("---------------------process data thread end---------------------");
    }

}
