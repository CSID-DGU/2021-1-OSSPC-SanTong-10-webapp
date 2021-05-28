package com.dguossp.santong.service.auth;

import com.dguossp.santong.dto.request.LoginDto;
import com.dguossp.santong.dto.request.SignUpDto;
import com.dguossp.santong.entity.Roles;
import com.dguossp.santong.entity.Users;
import com.dguossp.santong.repository.RoleRepository;
import com.dguossp.santong.repository.UsersRepository;
import com.dguossp.santong.security.CustomAuthenticationProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.util.HashSet;
import java.util.Set;

@Slf4j
@Service
public class AuthServiceImpl implements AuthService {


    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private CustomAuthenticationProvider authenticationProvider;

    @Override
    public boolean doCheckNickname(SignUpDto signUpDto) {

        // 요청한 닉네임 값이 중복되는 지 체크
        Users user = usersRepository.findByNickname(signUpDto.getNickname());

        // 요청 닉네임으로 가입을 진행할 수 있는 경우
        if (user == null) return true;

        // 요청 닉네임으로 가입을 진행할 수 없는 경우
        return false;
    }

    // 회원가입 서비스 로직
    @Override
    public void doSignUp(SignUpDto signUpDto) {

        // user_roles 테이블에 저장하기 위해 Set<Roles> 객체를 생성
        Set<Roles> roleSet = new HashSet<>();

        // ID 2 : ROLE_USER
        Roles role = roleRepository.findById(2);

        // 최초 회원가입 대상 (일반 유저)으로 ROLE_USER 권한 부여
        roleSet.add(role);

        // 오목 게임 수준 (0 : 초보, 1 : 중수, 2 : 고수)
        // 유저의 오목 실력

        Users users = Users.builder()
                .nickname(signUpDto.getNickname())
                .password(encoder.encode(signUpDto.getPassword()))
                .rolesSet(roleSet)
                .level(signUpDto.getLevel())
                .build();

        // 회원 저장 (INSERT)
        usersRepository.save(users);
    }

    // 로그인 서비스 로직
    @Override
    public void doLogin(LoginDto loginDto, HttpSession httpSession) {

        try {
            // 인증 (로그인 아이디 / 패스워드)
            Authentication authentication = authenticationProvider.authenticate(
                    new UsernamePasswordAuthenticationToken(loginDto.getNickname(), loginDto.getPassword())
            );

            // 로그인 성공 시,
            // 로그인 세션ID를 통해 해당 유저의 로그인 상태 저장
            String JESSIONID = httpSession.getId();
            httpSession.setAttribute(JESSIONID, authentication);

        } catch (BadCredentialsException e) {
            throw new BadCredentialsException("가입하지 않은 아이디이거나, 잘못된 비밀번호입니다.");
        } catch (UsernameNotFoundException e) {
            throw new UsernameNotFoundException("가입하지 않은 아이디이거나, 잘못된 비밀번호입니다.");
        }

    }
}
