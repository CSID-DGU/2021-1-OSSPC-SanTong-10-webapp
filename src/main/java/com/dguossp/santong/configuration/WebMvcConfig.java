package com.dguossp.santong.configuration;

import com.dguossp.santong.interceptor.AuthInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private AuthInterceptor authInterceptor;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry
                // '/' 로 시작하는 모든 요청을 다룬다.
                .addResourceHandler("/**")
                .addResourceLocations("classpath:/templates/", "classpath:/static/");
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/index").setViewName("index.html");
        registry.addViewController("/login").setViewName("account/login.html");
    }


    // 인증이 기반이 되는 요청에 대해서 로그인 여부 검사
    // 로그인 여부에 따라 서비스 로직 전개
//    @Override
//    public void addInterceptors(InterceptorRegistry registry) {
//
//        registry
//                // 로그인 여부 체크하는 인터셉터 (= 인증이 요구되는 요청 대상)
//                .addInterceptor(authInterceptor)
//                // 인증이 요구되지 않는 요청에 대해서는 인터셉터 적용 없이 통과 처리
//                .excludePathPatterns("/api/auth/**");
//    }
}
