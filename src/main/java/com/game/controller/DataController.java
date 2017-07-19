package com.game.controller;

import com.game.entity.JsonEntity;
import com.game.enums.GameCategoryType;
import com.game.model.Game;
import com.game.model.GameCategory;
import com.game.model.ServerArea;
import com.game.service.IGameCategoryService;
import com.game.service.IGameService;
import com.game.service.IProcessHTMLService;
import com.game.service.IServerAreaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
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
    private IProcessHTMLService processHTMLService;
    @Autowired
    private IGameService gameService;
    @Autowired
    private IServerAreaService serverAreaService;

    @RequestMapping(value = "/tradeFlowByUrl.do", method = RequestMethod.POST)
    public JsonEntity addTradeFlowByUrl(String url) {
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

        if (url.contains("c=-2")) {
            // equipment
            List<GameCategory> keyCategoryList = gameCategoryService.getAllKeysByItemCode(GameCategoryType.equipment.name());
            for (GameCategory keyCategory : keyCategoryList) {
                processHTMLService.processHtmlAndPost(game, serverArea, childServer, keyCategory, url);
            }
        } else if (url.contains("c=-3")) {
            // game coin
            processHTMLService.processHtmlAndPost(game, serverArea, childServer, itemCategory, url);
        }

        JsonEntity jsonEntity = new JsonEntity();
        jsonEntity.setData("addTradeFlowByUrl time:" + new Date());
        return jsonEntity;
    }
}
