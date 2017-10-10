package com.game.exception;

import com.game.entity.JsonEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by jennifert on 10/10/17.
 */
@Controller
@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler({Exception.class})
    @ResponseBody
    public JsonEntity handleException(HttpServletRequest request, Exception ex) {
        JsonEntity jsonEntity = new JsonEntity();
        jsonEntity.setStatus(500);
        jsonEntity.setMsg(ex.getMessage());
        return jsonEntity;
    }
}
