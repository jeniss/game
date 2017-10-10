package com.game.exception;

/**
 * Created by jeniss on 17/6/18.
 * the exception of requesting quan wang
 */
public class GetProxyIPException extends RuntimeException {
    private String message;

    public GetProxyIPException() {
    }

    public GetProxyIPException(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return this.message;
    }
}
