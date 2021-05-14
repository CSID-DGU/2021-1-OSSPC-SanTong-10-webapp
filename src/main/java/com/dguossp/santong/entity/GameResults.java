package com.dguossp.santong.entity;


import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "game_results")
public class GameResults {

    // 식별자 (PK)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    // 게임
    @OneToOne(fetch = FetchType.LAZY)
    private Games games;

    // 게임 승리자
    @ManyToOne(fetch = FetchType.LAZY)
    private Users winner;

    // 게임 패배자
    @ManyToOne(fetch = FetchType.LAZY)
    private Users loser;

    // 0 : 승패결정, 1 : 무승부
    private int isDraw;

    // 게임 결과 생성 일시
    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdDatetime;



}
