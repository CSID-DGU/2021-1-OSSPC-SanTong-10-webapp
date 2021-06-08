package com.dguossp.santong.controller.game_review;

import com.dguossp.santong.dto.request.GameReviewReqDto;
import com.dguossp.santong.dto.response.ApiResponseEntity;
import com.dguossp.santong.dto.response.GameReviewResDto;
import com.dguossp.santong.service.game_review.GameReviewService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/")
public class GameReviewController {

    @Autowired
    private GameReviewService gameReviewService;


    @PostMapping("/game-review")
    public ResponseEntity<?> getReivewData(@RequestBody GameReviewReqDto gameReivewDto) {

        // GameReviewResDto
        GameReviewResDto gameReviewResDto = gameReviewService.reviewBlack(gameReivewDto);

        ApiResponseEntity apiResponseEntity = ApiResponseEntity.builder()
                .statusCode(200)
                .message("복기 페이지 데이터 응답")
                .object(gameReviewResDto)
                .build();

        return new ResponseEntity<>(apiResponseEntity, HttpStatus.OK);
    }



}
