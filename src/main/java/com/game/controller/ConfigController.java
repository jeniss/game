package com.game.controller;

import com.game.entity.JsonEntity;
import com.game.util.ConfigHelper;
import com.game.util.ResponseHelper;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
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

    @RequestMapping(value = "/getRemoteIp.do")
    public JsonEntity ge(HttpServletRequest request) {
        Map<String, String> result = new HashMap<>();
        result.put("ip", request.getRemoteAddr());
        return ResponseHelper.createJsonEntity(result);
    }
}
