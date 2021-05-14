package com.dguossp.santong.security;

import lombok.Builder;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

/**
 *  Spring Security에서 로그인 정보를 대조할 때 UserDetails를 사용하므로, 해당 인터페이스를 implements 받아서 구현
 * */


@Getter
public class UserInformation implements UserDetails {

    private String username;
    private String password;
    private List<GrantedAuthority> authorities;

    @Builder
    public UserInformation(String username, String password, List<GrantedAuthority> authorities) {
        this.username = username;
        this.password = password;
        this.authorities = authorities;
    }



    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
