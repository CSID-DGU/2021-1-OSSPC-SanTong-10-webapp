package com.dguossp.santong.controller.auth;

import com.dguossp.santong.dto.request.LoginDto;
import com.dguossp.santong.dto.request.SignUpDto;
import com.dguossp.santong.dto.response.ApiResponseEntity;
import com.dguossp.santong.service.auth.AuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

/**
 * Created by taekwon_dev
 * Description :
 *
 * 회원가입 API (인증 X)
 * 로그인 API (인증 X)
 *
 *
 */


@Slf4j
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    // SignupDto 중, 'nickname' 필드만 사용
    @GetMapping("/nickname-check")
    public ResponseEntity<?> doChecktNickname(@RequestBody SignUpDto signUpDto) {
        log.info("회원가입 닉네임 중복체크 API 요청");
        boolean result = authService.doCheckNickname(signUpDto);
        ApiResponseEntity apiResponseEntity;

        if (result) {
            // 요청 닉네임으로 가입을 진행할 수 있는 경우
            apiResponseEntity = ApiResponseEntity.builder()
                    .statusCode(1)
                    .message("요청 닉네임으로 회원가입이 가능한 경우")
                    .object(null)
                    .build();
        } else {
            // 요청 닉네임으로 가입을 진행할 수 없는 경우
            apiResponseEntity = ApiResponseEntity.builder()
                    .statusCode(2)
                    .message("요청 닉네임으로 회원가입이 불가능한 경우")
                    .object(null)
                    .build();
        }

        return new ResponseEntity<>(apiResponseEntity, HttpStatus.OK);
    }

    @PostMapping("/signup")
    public ResponseEntity<?> doSignUp(@RequestBody SignUpDto signUpDto) {

        // 회원가입 서비스 로직 호출
        // 예외 발생 시 해당 로직에서 처리
        authService.doSignUp(signUpDto);

        ApiResponseEntity apiResponseEntity = ApiResponseEntity.builder()
                .statusCode(200)
                .message("회원가입 성공")
                .object(null)
                .build();

        return new ResponseEntity<>(apiResponseEntity, HttpStatus.OK);
    }


    @PostMapping("/login")
    public ResponseEntity<?> doLogin(@RequestBody LoginDto loginDto, HttpSession httpSession) {
        log.info("로그인 API 요청");
        // 로그인 서비스 로직 호출
        // 예외 발생 시 해당 로직에서 처리
        authService.doLogin(loginDto, httpSession);

        ApiResponseEntity apiResponseEntity = ApiResponseEntity.builder()
                .statusCode(200)
                .message("로그인 성공")
                .object(null)
                .build();


        return new ResponseEntity<>(apiResponseEntity, HttpStatus.OK);
    }

}
