package com.dguossp.santong.exception.handler;

import com.dguossp.santong.dto.response.ErrorResponse;
import com.dguossp.santong.exception.AuthException;
import com.dguossp.santong.exception.code.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


/** [Scope]
 *  1. Interceptor
 *
 *  2. Controller & Downstream Services
 *
 * */

@Slf4j
@RestControllerAdvice
public class ControllerExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> authExceptionHandler(Exception ex) {

        ErrorResponse errorResponse = null;

        if (ex instanceof BadCredentialsException) {
            errorResponse = ErrorResponse.of(ErrorCode.BADCREDENTIALS_EXCEPTION);
        }

        if (ex instanceof UsernameNotFoundException) {
            errorResponse = ErrorResponse.of(ErrorCode.USERNAMENOTFOUND_EXCEPTION);
        }

        if (ex instanceof AuthException) {
            errorResponse = ErrorResponse.of(ErrorCode.UNAUTHORIZED_EXCEPTION);
        }

        return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
    }

}

