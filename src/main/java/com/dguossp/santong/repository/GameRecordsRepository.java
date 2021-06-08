package com.dguossp.santong.repository;

import com.dguossp.santong.entity.GameRecords;
import com.dguossp.santong.entity.Games;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


public interface GameRecordsRepository extends JpaRepository<GameRecords, Long> {

    Page<GameRecords> findAllByGame(Games game, Pageable pageable);

    GameRecords findByXAndYAndGame(int x, int y, Games game);

}
