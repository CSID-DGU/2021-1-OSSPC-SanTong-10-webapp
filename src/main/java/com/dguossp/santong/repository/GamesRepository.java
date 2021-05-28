package com.dguossp.santong.repository;

import com.dguossp.santong.entity.Games;
import com.dguossp.santong.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GamesRepository extends JpaRepository<Games, Long> {

    Games findByParticipantAAndParticipantBAndGameStatus(Users usersA, Users usersB, int gameStatus);

}
