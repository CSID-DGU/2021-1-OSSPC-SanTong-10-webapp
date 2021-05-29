package com.dguossp.santong.entity;

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
public class GameRecords {

    // 식별자 (PK)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    private Games game;

    @ManyToOne
    private Users user;

    // X 좌표 값
    private int x;

    // Y 좌표 값
    private int y;

    // 0 : 백돌, 1 : 흑돌
    private int stoneStatus;

    // 0 : 미결정 수 1 : 결정수 (승리 결정 수)
    private int isFinish;

    // 유저가 착수한 일시
    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdDatetime;
}
