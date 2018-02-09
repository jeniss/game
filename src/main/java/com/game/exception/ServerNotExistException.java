package com.game.exception;

/**
 * Created by jeniss on 18/2/7.
 */
public class ServerNotExistException extends RuntimeException {
    public ServerNotExistException() {
    }

    public ServerNotExistException(String message) {
        super(message);
    }

    public ServerNotExistException(String message, Throwable cause) {
        super(message, cause);
    }
}
