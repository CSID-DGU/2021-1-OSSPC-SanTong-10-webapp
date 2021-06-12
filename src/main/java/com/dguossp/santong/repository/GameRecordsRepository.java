package com.dguossp.santong.repository;

import com.dguossp.santong.entity.GameRecords;
import com.dguossp.santong.entity.Games;
import com.dguossp.santong.entity.Users;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;


public interface GameRecordsRepository extends JpaRepository<GameRecords, Long> {

    List<GameRecords> findAllByGame(Games game);

    Page<GameRecords> findAllByGame(Games game, Pageable pageable);

    List<GameRecords> findAllByGameAndStoneStatus(Games game, int stoneStatus);

    GameRecords findByXAndYAndGame(int x, int y, Games game);

    GameRecords findFirstByGameAndLoginUserOrderByCreatedDatetimeDesc(Games game, Users user);

}
