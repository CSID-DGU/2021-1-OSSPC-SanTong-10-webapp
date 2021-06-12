package com.dguossp.santong.repository;

import com.dguossp.santong.entity.Games;
import com.dguossp.santong.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GamesRepository extends JpaRepository<Games, Long> {

    Games findByParticipantAAndParticipantBAndGameStatus(Users usersA, Users usersB, int gameStatus);

    Games findById(long gameId);

    // 로그인 유저가 가장 마지막으로 치룬 게임 (게임 상태 값 2)
    Games findFirstByParticipantAOrParticipantBAndGameStatusOrderByCreatedDatetimeDesc(Users loginUserA, Users loginUserB, int gameStatus);

}
