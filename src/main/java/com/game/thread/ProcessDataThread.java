package com.game.thread;

import com.game.enums.GameCategoryType;
import com.game.model.Game;
import com.game.model.GameCategory;
import com.game.model.ServerArea;
import com.game.service.IGameCategoryService;
import com.game.service.IGameService;
import com.game.service.IProcessHTMLService;
import com.game.service.IServerAreaService;
import com.game.util.SpringContextUtil;
import org.apache.log4j.Logger;

import java.util.List;

/**
 * Created by jeniss on 17/6/21.
 */
public class ProcessDataThread extends Thread {
    private static final Logger logger = Logger.getLogger(ProcessDataThread.class);

    @Override
    public void run() {
        IServerAreaService serverAreaService = (IServerAreaService) SpringContextUtil.getBean("serverAreaService");
        IGameService gameService = (IGameService) SpringContextUtil.getBean("gameService");
        IGameCategoryService gameCategoryService = (IGameCategoryService) SpringContextUtil.getBean("gameCategoryService");

        List<ServerArea> serverAreaList = serverAreaService.getAll();

        List<Game> gameList = gameService.getAllGameList();
        List<GameCategory> gameCategoryList = gameCategoryService.getAllItemCategories();

        // Traversal the game
        for (Game game : gameList) {
            // Traversal the serverArea of game
            for (ServerArea serverArea : serverAreaList) {
                // Traversal the childServer area of game
                for (ServerArea childServer : serverArea.getChildServerAreas()) {
                    // Traversal the itemCategory
                    for (GameCategory itemCategory : gameCategoryList) {
                        // process the url
                        StringBuilder urlStringBuilder = this.getUrl(game, itemCategory, serverArea, childServer);

                        IProcessHTMLService processHTMLService = (IProcessHTMLService) SpringContextUtil.getBean("processHTMLService");

                        // if the category type is equipment, then traversal the keys
                        if (GameCategoryType.equipment.name().equals(itemCategory.getCode())) {
                            List<GameCategory> keyCategoryList = gameCategoryService.getAllKeysByItemCode(GameCategoryType.equipment.name());
                            for (GameCategory keyCategory : keyCategoryList) {
                                String url = urlStringBuilder.toString() + "&key=" + keyCategory.getName();
                                processHTMLService.processHtmlAndPost(game, serverArea, childServer, keyCategory, url);
                            }
                        } else if (GameCategoryType.gameCoin.name().equals(itemCategory.getCode())) {
                            processHTMLService.processHtmlAndPost(game, serverArea, childServer, itemCategory, urlStringBuilder.toString());
                        }
                    }
                }
            }
        }

        logger.info("---------------------process data thread end---------------------");
    }

    /**
     * get the url
     */
    private StringBuilder getUrl(Game game, GameCategory gameCategory, ServerArea serverArea, ServerArea childServer) {
        String urlPrefix = "http://www.uu898.com/newTrade.aspx?";

        StringBuilder stringBuilder = new StringBuilder(urlPrefix);
        stringBuilder.append("gm=" + game.getCode());
        stringBuilder.append("&c=" + gameCategory.getValue());
        stringBuilder.append("&area=" + serverArea.getCode());
        stringBuilder.append("&srv=" + childServer.getCode());

        return stringBuilder;
    }
}
