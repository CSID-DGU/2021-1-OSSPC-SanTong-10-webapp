package com.dguossp.santong.controller.game_review;

import com.dguossp.santong.dto.request.DoReviewReqDto;
import com.dguossp.santong.dto.request.GameReviewReqDto;
import com.dguossp.santong.dto.response.ApiResponseEntity;
import com.dguossp.santong.dto.response.GameReviewResDto;
import com.dguossp.santong.security.UserInformation;
import com.dguossp.santong.service.game_review.GameReviewService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/")
public class GameReviewController {

    @Autowired
    private GameReviewService gameReviewService;

    @PostMapping("/game-review")
    public ResponseEntity<?> getReivewData(@RequestBody GameReviewReqDto gameReivewDto, Authentication authentication) {
        log.info("getReviewData() is called");

        // Interceptor를 통해 Authentication -> SecurityContextHolder에 주입
        UserInformation userDetails = (UserInformation) authentication.getPrincipal();
        // Authentication 객체에서 유저네임 조회
        String username = userDetails.getUsername();

        // GameReviewResDto
        GameReviewResDto gameReviewResDto = gameReviewService.reviewBlack(username, gameReivewDto);

        ApiResponseEntity apiResponseEntity = ApiResponseEntity.builder()
                .statusCode(200)
                .message("복기 페이지 데이터 응답")
                .object(gameReviewResDto)
                .build();

        return new ResponseEntity<>(apiResponseEntity, HttpStatus.OK);
    }

    @PostMapping("/do-review")
    public ResponseEntity<?> doReqReview(@RequestBody DoReviewReqDto doReviewReqDto) {


        String responseBody = gameReviewService.doRequestReviewData(doReviewReqDto);


        ApiResponseEntity apiResponseEntity = ApiResponseEntity.builder()
                .statusCode(200)
                .message("복기 데이터 요청 응답")
                .object(responseBody)
                .build();
        return new ResponseEntity<>(apiResponseEntity, HttpStatus.OK);
    }




}
