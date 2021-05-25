package com.dguossp.santong.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Builder
@Getter
public class GameMatchingResponse {

    // enum (매칭 성공, 취소, 시간 초과)
    private GameMatchingResult gameMatchingResult;

    // WS 게임방 구독 경로
    private String gameSubDestination;

    // 로그인 유저 정보
    private UserInfoDto loginUser;

    // 게임 상대방 정보
    private UserInfoDto opponentUser;

    public enum GameMatchingResult {
        SUCCESS, CANCEL, TIMEOUT
    }

}
