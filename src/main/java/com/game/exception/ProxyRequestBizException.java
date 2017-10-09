package com.game.exception;

/**
 * Created by jeniss on 17/6/18.
 * the exception of requesting by proxy ip
 */
public class ProxyRequestBizException extends RuntimeException {
    private String message;

    public ProxyRequestBizException() {
    }

    public ProxyRequestBizException(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return this.message;
    }
}
