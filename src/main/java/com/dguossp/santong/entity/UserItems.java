package com.dguossp.santong.entity;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_items")
public class UserItems {

    // 식별자 (PK)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    // 어떤 아이템인 지 식별하기 위한 고유 ID 값
    @ManyToOne(fetch = FetchType.LAZY)
    private Items item;

    // 아이템 보유 유저
    @ManyToOne(fetch = FetchType.LAZY)
    private Users user;

    // 아이템 사용처 (=게임)
    @OneToOne(fetch = FetchType.LAZY)
    private Games game;


    // 게임 아이템 사용 여부 (0 : 사용 전, 1 : 사용 완료)
    private int isUsed;

    // 아이템 사용 일시
    private LocalDateTime itemUsedDatetime;

    // 아이템 생성 일시
    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdDatetime;

}
