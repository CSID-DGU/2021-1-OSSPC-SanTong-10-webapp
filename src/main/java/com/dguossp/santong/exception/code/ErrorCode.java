package com.dguossp.santong.exception.code;

import lombok.Getter;

@Getter
public enum ErrorCode {

    // Auth
    BADCREDENTIALS_EXCEPTION(401, "AUTH001", "가입하지 않은 아이디이거나, 잘못된 비밀번호입니다."),
    USERNAMENOTFOUND_EXCEPTION(401, "AUTH002", "가입하지 않은 아이디이거나, 잘못된 비밀번호입니다."),
    UNAUTHORIZED_EXCEPTION(401, "AUTH003", "인증이 요구되는 요청 대상, 세션 만료"),

    // User

    // Game
    NOTFOUNDUSER_EXCEPTION(404, "GAME001", "요청한 유저를 매칭 대기열에서 찾을 수 없습니다.");

    // AWS

    private String code;
    private String message;
    private int status;

    ErrorCode(int status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }
}
