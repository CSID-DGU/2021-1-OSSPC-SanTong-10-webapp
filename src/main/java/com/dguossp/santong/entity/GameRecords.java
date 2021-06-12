package com.dguossp.santong.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 *  [] GameRecords 객체 -> 게임 정보를 가지고와서 상대 유저 정보 탐색
 *
 *  [] GameRecords 객체 ->
 *
 *
 * */

@ToString
@Entity
@Table(name = "game_records")
@Getter
@NoArgsConstructor
public class GameRecords {

    // 식별자 (PK)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_id", referencedColumnName = "id")
    private Games game;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private Users loginUser;

    // X 좌표 값
    private int x;

    // Y 좌표 값
    private int y;

    // 1: ~결정수 2: 결정수
    private int isFinish;

    // 1 : 흑돌, 2 : 백돌
    private int stoneStatus;

    // 금수 위치 좌표 리스트 [(a, b), (c,d) ... ]
    private String unallowedList;

    // AI 분석 결과 좌표 & 승률 리스트 [X&Y&Rate ... ]
    private String reviewList;

    // 유저의 착수 시점, 직전 판의 상태
    // 예를 들어, 전체 오목판 기준, 3번째 돌 착수하는 경우(흑입장) --> 흑(1), 백(2) 좌표 값 리스트
    private String prevStateList;


    // 유저가 착수한 일시
    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdDatetime;

    @Builder
    public GameRecords(Games game, Users loginUser, int x, int y, int isFinish, String unallowedList, String prevStateList, int stoneStatus) {
        this.game = game;
        this.loginUser = loginUser;
        this.x = x;
        this.y = y;
        this.isFinish = isFinish;
        this.unallowedList = unallowedList;
        this.prevStateList = prevStateList;
        this.stoneStatus = stoneStatus;
    }


}
