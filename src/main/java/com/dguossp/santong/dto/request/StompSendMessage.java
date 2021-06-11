package com.dguossp.santong.dto.request;

import lombok.Getter;

import java.util.List;

@Getter
public class StompSendMessage {

    // 로그인 유저 고유 닉네임
    private String loginUserNickname;
    // 게임 상대 유저 고유 닉네임
    private String opponentUserNickname;

    // 게임 고유 ID
    private long gameId;

    // 1 : 흑돌, 2 : 백돌
    // 로그인 유저 (= 착수한 유저) 돌 상태
    private int loginUserTurn;
    // 게임 상대 유저 돌 상태
    private int opponentUserTurn;

    // 착수한 돌 X 좌표
    private int x;
    // 착수한 돌 Y 좌표
    private int y;

    // 금수 위치 좌표 리스트 [(a, b), (c,d) ... ]
    private List<String> unallowedList;

    // 유저의 착수 시점, 직전 판의 상태
    // 예를 들어, 전체 오목판 기준, 3번째 돌 착수하는 경우(흑입장) --> 흑(1), 백(2) 좌표 값 리스트
    private List<String> prevStateList;

    // 승부 결정 수 (= 결정 수)
    // 1 : ~결정수 , 2 : 결정수
    private int isFinish;





}
