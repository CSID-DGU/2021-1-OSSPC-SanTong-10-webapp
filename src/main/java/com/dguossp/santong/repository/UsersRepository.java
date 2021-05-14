package com.dguossp.santong.repository;

import com.dguossp.santong.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsersRepository extends JpaRepository<Users, Long> {

    // 닉네임 (고유 값, 로그인 아이디로 활용)
    Users findByNickname(String nickname);

}
