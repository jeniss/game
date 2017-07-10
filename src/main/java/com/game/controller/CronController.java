package com.game.controller;

import com.game.entity.JsonEntity;
import com.game.thread.ProcessDataThread;
import org.apache.log4j.Logger;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

/**
 * Created by jeniss on 17/5/23.
 */
@RestController
@RequestMapping("/cron")
public class CronController {
    private static final Logger logger = Logger.getLogger(CronController.class);

    @RequestMapping(value = "/crawlingDataToSave.do", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public JsonEntity<String> crawlingDataToSaveCron() {
        ProcessDataThread thread = new ProcessDataThread();
        thread.start();
        JsonEntity jsonEntity = new JsonEntity();
        jsonEntity.setData("crawlingDataToSaveCron time:" + new Date());
        return jsonEntity;
    }
}
