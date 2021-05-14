package com.dguossp.santong.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Builder
@Getter
public class ApiResponseEntity {
    int statusCode;
    String message;
    Object object;
}
