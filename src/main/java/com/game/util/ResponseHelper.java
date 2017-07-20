package com.game.util;

import com.game.entity.JsonEntity;

/**
 * Created by jennifert on 7/20/2017.
 */
public class ResponseHelper {
    private ResponseHelper() {
    }

    public static <T> JsonEntity<T> createJsonEntity(T data) {
        JsonEntity jsonEntity = new JsonEntity();
        jsonEntity.setData(data);
        return jsonEntity;
    }
}
