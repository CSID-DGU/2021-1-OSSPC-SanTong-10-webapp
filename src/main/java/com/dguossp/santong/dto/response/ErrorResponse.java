package com.dguossp.santong.dto.response;

import com.dguossp.santong.exception.code.ErrorCode;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ErrorResponse {

    private String message;
    private int statusCode;
    private String code;

    public ErrorResponse(ErrorCode code) {
        this.message = code.getMessage();
        this.statusCode = code.getStatus();
        this.code = code.getCode();
    }

    public static ErrorResponse of(ErrorCode code) {
        return new ErrorResponse(code);
    }

}
