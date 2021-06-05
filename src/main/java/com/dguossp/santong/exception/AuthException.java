package com.dguossp.santong.exception;

import com.dguossp.santong.exception.code.ErrorCode;

public class AuthException extends RuntimeException {

    private ErrorCode errorCode;

    public AuthException(String message, ErrorCode errorCode) {
        super(message);
        this.errorCode = errorCode;
    }



}
