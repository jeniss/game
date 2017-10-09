package com.game.exception;

/**
 * Created by jeniss on 17/6/18.
 * the exception of requesting quan wang
 */
public class QuanWangBizException extends RuntimeException {
    private String message;

    public QuanWangBizException() {
    }

    public QuanWangBizException(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return this.message;
    }
}
