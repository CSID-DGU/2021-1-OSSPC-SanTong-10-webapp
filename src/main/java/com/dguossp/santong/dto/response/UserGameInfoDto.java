package com.dguossp.santong.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

/** 게임 매칭 성공 시점에, 게임에 참여한 유저 정보에 활용
 * */
@Builder
@AllArgsConstructor
@Getter
public class UserGameInfoDto {

    private String nickname;

    // (true : 흑돌, false : 백돌)
    private boolean turn;

    // 유저 게임 수준 (0 : 하수 / 1 : 중수 / 2 : 고수) <- 회원가입 회원정보 입력 시 기재한 값 기준
    private int gameLevel;
}
