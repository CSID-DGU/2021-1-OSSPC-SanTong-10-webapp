package com.dguossp.santong.controller.user;


import com.dguossp.santong.dto.response.ApiResponseEntity;
import com.dguossp.santong.dto.response.UserInfoDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/user")
public class UserController {


    @GetMapping(value = "/profile")
    public ResponseEntity<?> doFetchProfile(Authentication authentication) {
        log.info("doFetchProfile() is called");

        ApiResponseEntity apiResponseEntity;

        if (authentication == null) {
            apiResponseEntity = ApiResponseEntity.builder()
                    .message("비로그인 상태")
                    .statusCode(1)
                    .object(null)
                    .build();

        } else {
            UserInfoDto userInfoDto = UserInfoDto.builder()
                    .nickname(authentication.getName())
                    .build();

            apiResponseEntity = ApiResponseEntity.builder()
                    .message("회원정보 조회 성공 (로그인 상태)")
                    .statusCode(2)
                    .object(userInfoDto)
                    .build();
        }
        return new ResponseEntity<>(apiResponseEntity, HttpStatus.OK);
    }



}
