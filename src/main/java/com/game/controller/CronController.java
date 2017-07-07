package com.game.controller;

import com.game.entity.JsonEntity;
import com.game.service.IGameCategoryService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

/**
 * Created by jeniss on 17/5/23.
 */
@RestController
@RequestMapping("/cron")
public class CronController {
    private static final Logger logger = Logger.getLogger(CronController.class);

    @Autowired
    private IGameCategoryService gameCategoryService;

    @RequestMapping(value = "/crawlingDataToSave.do", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public JsonEntity<String> crawlingDataToSaveCron(HttpServletRequest request, HttpServletResponse response) {
//        ProcessDataThread thread = new ProcessDataThread();
//        thread.start();
        gameCategoryService.getAllItemCategories();
        JsonEntity jsonEntity = new JsonEntity();
        jsonEntity.setData("crawlingDataToSaveCron time:" + new Date());
        return jsonEntity;
    }
}
