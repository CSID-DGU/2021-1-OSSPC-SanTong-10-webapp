package com.dguossp.santong.security;

import com.dguossp.santong.entity.Users;
import com.dguossp.santong.repository.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UsersRepository usersRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        Users userEntity = usersRepository.findByNickname(username);

        List<GrantedAuthority> authorityList = new ArrayList<>();

        // NPE
        if (userEntity == null) throw new UsernameNotFoundException("가입하지 않은 아이디이거나, 잘못된 비밀번호 입니다.");

        userEntity.getRolesSet().forEach(role -> {
            authorityList.add(new SimpleGrantedAuthority(role.getName()));
        });

        UserInformation user = UserInformation.builder()
                .username(userEntity.getNickname())
                .password(userEntity.getPassword())
                .authorities(authorityList)
                .build();

        return user;
    }





}
