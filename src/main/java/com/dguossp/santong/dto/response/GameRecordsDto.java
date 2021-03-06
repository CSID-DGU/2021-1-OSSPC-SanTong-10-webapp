package com.dguossp.santong.dto.response;


import lombok.Getter;
@Getter
// 앞 단에서 유저가 놓은 수를 렌더링 할 때 필요한 정보 From GameRecords
public class GameRecordsDto {

    // mapped
    // X
    private int x;

    // mapped
    // Y
    private int y;

    // mapped
    // Turn (흑1 / 백2)
    private int stoneStatus;

    // 이 x, y 착수 시점 전에 놓인 좌표 값 리스트
    private String prevStateList;

    public void mapX(int x) {
        this.x = x;
    }

    public void mapY(int y) {
        this.y = y;
    }

    public void mapStoneStatus(int stoneStatus) {
        this.stoneStatus = stoneStatus;
    }

    public void mapPrevStatsList(String prevStateList) {
        this.prevStateList = prevStateList;
    }

}
