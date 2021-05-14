package com.dguossp.santong.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Slf4j
@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor
public class Users {

    // 식별자 (PK)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    // 닉네임 (고유 값 -> 중복체크 대상)
    private String nickname;

    // 패스워드
    private String password;

    // 회원 프로필 사진 경로 (on AWS S3)
    private String profileImgDir;

    // (로그인) 유저의 권한
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
              )
    private Set<Roles> rolesSet = new HashSet<>();

    // (로그인) 유저의 게임 리스트 (유저 = Participant A)
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "participantA")
    private Set<Games> gamesASet = new HashSet<>();

    // (로그인) 유저의 게임 리스트 (유저 = Participant B)
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "participantB")
    private Set<Games> gamesBSet = new HashSet<>();

    // (로그인) 유저가 승리한 게임 리스트
    // 단, 무승부인 게임 결과에서는 ___ 유저를 의미
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "winner")
    private Set<GameResults> winnerSet = new HashSet<>();

    // (로그인) 유저가 패배한 게임 리스트
    // 단, 무승부인 게임 결과에서는 ___ 유저를 의미
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "loser")
    private Set<GameResults> loserSet = new HashSet<>();

    // (로그인) 유저가 보유하고 있는 아이템 리스트
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "user")
    private Set<UserItems> userItemsSet = new HashSet<>();


    // 회원가입 일시
    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdDatetime;

    // 회원정보 마지막 업데이트 일시
    @UpdateTimestamp
    private LocalDateTime updatedDatetime;

    // 회원가입 API 요청 요소
    @Builder
    public Users (String nickname, String password, Set<Roles> rolesSet) {
        this.nickname = nickname;
        this.password = password;
        this.rolesSet = rolesSet;
    }



}
