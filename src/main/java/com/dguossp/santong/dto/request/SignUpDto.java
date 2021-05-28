package com.dguossp.santong.dto.request;

import lombok.Getter;

@Getter
public class SignUpDto {
    private String nickname;
    private String password;
    // 오목 게임 수준 (0 : 초보, 1 : 중수, 2 : 고수)
    private int level;
}
