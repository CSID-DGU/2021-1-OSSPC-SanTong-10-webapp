package com.dguossp.santong.dto.response;

import com.dguossp.santong.entity.GameRecords;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Builder
@Getter
public class GameReviewResDto {

    // 사이즈 (-> 이전, 다음 상태 이동의 기준)
    private int size;

    // 좌표 값 리스트 (-> 좌표 값에 해당 돌 상태로 렌더링)
    private List<GameRecordsDto> gameRecordList;

    // 복기 좌표 값 리스트 (-> 복기 페이지 오른쪽(=복기) 추천 좌표 렌더링, 유저가 놓은 수와 겹치면 뒤집어 씌운다)
    private List<GameReviewDto> gameReviewRecordsList;
}
