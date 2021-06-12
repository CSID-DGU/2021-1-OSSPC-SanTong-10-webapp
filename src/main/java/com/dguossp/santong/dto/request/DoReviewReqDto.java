package com.dguossp.santong.dto.request;

import lombok.Getter;

// 복기 하기 버튼 클릭 시, Spring서버로  데이터 식별 값 전송
// 이 값을 통해, 복기 모델 서버 측으로 데이터 생성 후 전송
@Getter
public class DoReviewReqDto {
    private int gameID;
    private int userStoneStatus;
}
