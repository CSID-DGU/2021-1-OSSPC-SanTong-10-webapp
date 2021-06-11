package com.dguossp.santong.interceptor;

import com.dguossp.santong.exception.AuthException;
import com.dguossp.santong.exception.code.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Slf4j
@Component
public class AuthInterceptor implements HandlerInterceptor {

    // 아래 요청 경로 : 로그인 또는 비로그인 두 경우 모두 요청할 수 있는 경우 (쿠키 값이 있는 경우 = 로그인 상태, 없는 경우 = 비로그인 상태)
    // 비로그인 상태인 경우, Interceptor 통과 (컨트롤러에서도 로그인 / 비로그인에 따른 응답 값 반환처리하기 때문)

    // 유저 프로필 정보 조회 (이 요청을 통해 로그인 여부도 담당)
    private static final String API_USER_PROFILE = "/api/user/profile";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        // 클라이언트 요청(Request)에 포함된 쿠키 값 조회 (-> JSESSIONID 값 찾는 목적)
        Cookie[] cookies = request.getCookies();

        if (cookies != null) {
            for (Cookie cookie : cookies) {

                if (cookie.getName().equals("JSESSIONID")) {
                    // 로그인 상태로 요청한 경우 -> (SecurityContextHolder <- Authentication 설정)
                    // 인터셉터 통과하는 요청 (= 인증 요청)에 대해서 쿠키가 있는 경우
                    HttpSession session = request.getSession();
                    // NPE (요청한 쿠키 값에 매핑되는 세션이 없는 경우)
                    Authentication authentication = (Authentication) session.getAttribute(session.getId());

                    if (authentication == null) {

                        // 비로그인 상태로 회원정보 조회 요청한 경우 -> 이 경우에는 Interceptor 통과
                        if (request.getRequestURI().equals(API_USER_PROFILE)) return true;

                        // 인증이 요구되는 API 요청하는 경우 -> 세션 만료 에러 반환
                        throw new AuthException("세션 만료", ErrorCode.UNAUTHORIZED_EXCEPTION);
                    }

                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    return true;
                }
            } // for loop
        }

        // 세션 쿠키가 없어도, 인증 필요 없는 경우 통과
        if (request.getRequestURI().equals(API_USER_PROFILE)) return true;

        throw new AuthException("세션 만료", ErrorCode.UNAUTHORIZED_EXCEPTION);

        // CustomException (extending RuntimeException)에 대한 핸들러를 정의하지 않은 경우
        // org.springframework.web.util.NestedServletException: Request processing failed
        // 준비된 500 에러 페이지 반환
        // <!doctype html>
        //<html lang="en">
        //
        //<head>
        //	<title>HTTP Status 500 – Internal Server Error</title>
        //	<style type="text/css">
        //		body {
        //			font-family: Tahoma, Arial, sans-serif;
        //		}
        //
        //		h1,
        //		h2,
        //		h3,
        //		b {
        //			color: white;
        //			background-color: #525D76;
        //		}
        //
        //		h1 {
        //			font-size: 22px;
        //		}
        //
        //		h2 {
        //			font-size: 16px;
        //		}
        //
        //		h3 {
        //			font-size: 14px;
        //		}
        //
        //		p {
        //			font-size: 12px;
        //		}
        //
        //		a {
        //			color: black;
        //		}
        //
        //		.line {
        //			height: 1px;
        //			background-color: #525D76;
        //			border: none;
        //		}
        //	</style>
        //</head>
        //
        //<body>
        //	<h1>HTTP Status 500 – Internal Server Error</h1>
        //</body>
        //
        //</html>

    }
}
