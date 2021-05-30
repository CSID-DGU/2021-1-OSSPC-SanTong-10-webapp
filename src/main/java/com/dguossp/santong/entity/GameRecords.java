package com.dguossp.santong.entity;

import lombok.Builder;
import lombok.NoArgsConstructor;
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

@Entity
@Table(name = "game_records")
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

    // 유저가 착수한 일시
    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdDatetime;

    @Builder
    public GameRecords(Games game, Users loginUser, int x, int y, int isFinish, int stoneStatus) {
        this.game = game;
        this.loginUser = loginUser;
        this.x = x;
        this.y = y;
        this.isFinish = isFinish;
        this.stoneStatus = stoneStatus;
    }


}
