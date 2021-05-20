package com.dguossp.santong.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

/** 추후 유저 정보가 추가될 수 있으므로, DTO 형태로 관리 */

@Builder
@AllArgsConstructor
@Getter
public class UserInfoDto {
    String nickname;
}
