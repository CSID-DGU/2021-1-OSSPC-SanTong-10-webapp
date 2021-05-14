package com.dguossp.santong.entity;



import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "games")
public class Games {

    // 식별자 (PK)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    // 게임 타이틀 (복기 리스트에서 타이틀로 활용)
    private String title;

    // 게임 모드 (0 : PVC, 1 : PVP)
    private int gameMode;

    // 복기 진행 여부 (0 : 복기 진행 X, 1 : 복기 진행 O)
    private int isReminded;

    // 게임 참여자 A
    @ManyToOne(fetch = FetchType.LAZY)
    private Users participantA;

    // 게임 참여자 B
    @ManyToOne(fetch = FetchType.LAZY)
    private Users participantB;

    // 복기 리스트에 활용될 썸네일 이미지 사진
    private String thumbnailImgDir;

    // 복기 사진 디렉토리 리스트
    private String imgDirList;

    // 게임 생성 일시
    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdDatetime;

    // 게임 정보 마지막 업데이트 일시
    @UpdateTimestamp
    private LocalDateTime updatedDatetime;
}
