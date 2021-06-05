package com.dguossp.santong.exception;

import com.dguossp.santong.exception.code.ErrorCode;

public class GameException extends RuntimeException {

    private ErrorCode errorCode;

    public GameException(String message, ErrorCode errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

}
