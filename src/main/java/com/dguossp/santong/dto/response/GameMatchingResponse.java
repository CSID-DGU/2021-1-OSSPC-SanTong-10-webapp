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

    // 생성된 게임방 고유 Id
    private long gameId;

    // WS 게임방 구독 경로
    private String gameSubDestination;

    // 로그인 유저 정보
    private UserGameInfoDto loginUser;

    // 게임 상대방 정보
    private UserGameInfoDto opponentUser;

    public enum GameMatchingResult {
        SUCCESS, CANCEL, TIMEOUT
    }

}
