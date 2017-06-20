package com.game.exception;

/**
 * Created by jeniss on 17/6/18.
 */
public class BizException extends RuntimeException {
    private String message;

    public BizException() {
    }

    public BizException(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return this.message;
    }
}
