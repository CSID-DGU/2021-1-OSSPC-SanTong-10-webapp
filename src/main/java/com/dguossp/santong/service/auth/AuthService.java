package com.dguossp.santong.service.auth;

import com.dguossp.santong.dto.request.LoginDto;
import com.dguossp.santong.dto.request.SignUpDto;

import javax.servlet.http.HttpSession;

public interface AuthService {

    boolean doCheckNickname(SignUpDto signUpDto);

    void doSignUp(SignUpDto signUpDto);

    void doLogin(LoginDto loginDto, HttpSession httpSession);

}
