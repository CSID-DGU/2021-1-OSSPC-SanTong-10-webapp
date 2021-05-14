package com.dguossp.santong.interceptor;

import com.dguossp.santong.exception.AuthException;
import com.dguossp.santong.exception.code.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Slf4j
@Component
public class AuthInterceptor implements HandlerInterceptor {

    private static final String HS_AUTH_SESSION = "HS_AUTH_SESSION";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        // 로그인 여부를 확인할 때 사용한 세션 정보를 조회 (via JSESSIONID)
        HttpSession session = request.getSession();
        Authentication authentication = (Authentication) session.getAttribute(HS_AUTH_SESSION);

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
        if (authentication == null) throw new AuthException("세션 만료", ErrorCode.UNAUTHORIZED_EXCEPTION);

        // DownStream 서비스 로직 전개
        return true;
    }
}
