package com.dguossp.santong.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

public class CustomAuthenticationProvider implements AuthenticationProvider {

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        String username = authentication.getName();
        String password = (String) authentication.getCredentials();

        try {
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            // 로그인 시점 -> 패스워드 일치 여부 체크
            if(!passwordEncoder.matches(password,userDetails.getPassword())){
                throw new BadCredentialsException("가입하지 않은 아이디이거나, 잘못된 비밀번호입니다.");
            }

            UsernamePasswordAuthenticationToken authenticationToken
                    = new UsernamePasswordAuthenticationToken(
                    userDetails,
                    null,
                    userDetails.getAuthorities());

            return authenticationToken;
        } catch (UsernameNotFoundException e) {
            // 로그인 요청 시점
            // 아이디 또는 패스워드 정보가 일치하지 않은 경우
            throw new UsernameNotFoundException("가입하지 않은 아이디이거나, 잘못된 비밀번호 입니다.");
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
