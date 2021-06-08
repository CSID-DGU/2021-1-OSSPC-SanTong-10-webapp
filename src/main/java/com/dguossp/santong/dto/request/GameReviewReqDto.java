package com.dguossp.santong.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Builder
@Getter
public class GameReviewReqDto {

    // 게임방 고유 Id
    private long gameId;

    // 복기 요청 (로그인) 유저 닉네임
    private String nickname;

    // 1 : 흑돌, 2 : 백돌
    private int turn;

    // 현재 시점에 보여줘야 하는 좌표 수 (in 복기 페이지)
    private int size;

}
