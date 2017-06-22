package com.game.controller;

import com.game.entity.JsonEntity;
import com.game.service.IGameCategoryService;
import com.game.service.IGameService;
import com.game.service.IServerAreaService;
import com.game.service.ITradeFlowService;
import com.game.thread.ProcessDataThread;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by jeniss on 17/5/23.
 */
@RestController
@RequestMapping("/cron")
public class CronController {
    private static final Logger logger = Logger.getLogger(CronController.class);

    @Autowired
    private IServerAreaService serverAreaService;
    @Autowired
    private IGameService gameService;
    @Autowired
    private IGameCategoryService gameCategoryService;
    @Autowired
    private ITradeFlowService tradeFlowService;

    @RequestMapping("/crawlingDataToSave.do")
    public JsonEntity<String> crawlingDataToSave() {
        ProcessDataThread thread = new ProcessDataThread();
        thread.start();
        JsonEntity jsonEntity = new JsonEntity();
        jsonEntity.setData("ok");
        return jsonEntity;
    }
}
