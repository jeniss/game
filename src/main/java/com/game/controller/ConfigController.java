package com.game.controller;

import com.game.entity.JsonEntity;
import com.game.util.ConfigHelper;
import com.game.util.ResponseHelper;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Created by jennifert on 7/20/2017.
 */
@RestController
@RequestMapping("/config")
public class ConfigController {
    @RequestMapping(value = "/updateConfig.do")
    public JsonEntity updateConfig() {
        Map<String, String> result = ConfigHelper.getInstance().refresh();
        return ResponseHelper.createJsonEntity(result);
    }
}
