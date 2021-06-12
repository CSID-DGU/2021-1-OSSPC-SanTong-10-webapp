package com.dguossp.santong.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Builder
@Getter
public class GameReviewDto {

    // X
    private int x;
    // Y
    private int y;

    // Rate
    private int winningRate;
    // Flag (-> 1: 가장 높은 승률, 2: 유저가 놓은 수 & 가장 높은 승률, 3: 유저가 높은 수 & ~가장 높은 승률, 4: 그 외)
    private int flag;

}
