package com.dguossp.santong.controller.game;

import com.dguossp.santong.dto.request.Greeting;
import com.dguossp.santong.dto.request.HelloMessage;
import com.dguossp.santong.dto.request.LoginDto;
import com.dguossp.santong.dto.response.ApiResponseEntity;
import com.dguossp.santong.dto.response.GameMatchingResponse;
import com.dguossp.santong.repository.redis.RedisGameSearchRepository;
import com.dguossp.santong.security.UserInformation;
import com.dguossp.santong.service.game.GameService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.util.HtmlUtils;

import javax.servlet.http.HttpSession;


@Slf4j
@RestController
@RequestMapping("/api/game")
public class GameController {

    @Autowired
    private GameService gameService;

    @Autowired
    private RedisGameSearchRepository gameSearchRepository;

    @MessageMapping("/hello")
    @SendTo("/topic/greetings")
    public Greeting greeting(HelloMessage message) throws Exception {
        Thread.sleep(1000); // simulated delay

        // https://docs.spring.io/spring-framework/docs/current/reference/html/web.html#websocket-stomp-handle-annotations
        // is serialized to a payload through a matching MessageConverter
        // and sent as a Message to the brokerChannel, from where it is broadcast to subscribers.
        return new Greeting("Hello (From GameController), " + HtmlUtils.htmlEscape(message.getName()) + "!");
    }


    @PostMapping("/async-search")
    public DeferredResult<GameMatchingResponse> defResultForGameSearching(Authentication authentication) {

        // Interceptor를 통해 Authentication -> SecurityContextHolder에 주입
        UserInformation userDetails = (UserInformation) authentication.getPrincipal();
        // Authentication 객체에서 유저네임 조회
        String username = userDetails.getUsername();

        // TIMEOUT : 10초 (= 10초 간 매칭 성공하지 못하는 경우, TIMEOUT CALLBACK)
        DeferredResult<GameMatchingResponse> deferredResult = new DeferredResult<>(10000l);
        gameService.onAsyncTask(username, deferredResult);

        return deferredResult;
    }

    @PostMapping("/cancel-search")
    public ResponseEntity<?> doLogin(Authentication authentication) {

        // Interceptor를 통해 Authentication -> SecurityContextHolder에 주입
        UserInformation userDetails = (UserInformation) authentication.getPrincipal();
        // Authentication 객체에서 유저네임 조회
        String username = userDetails.getUsername();

        // return void (예외 발생하는 경우, 해당 서비스 로직에서 Throw 후 예외 응답 반환)
        gameService.cancelGameSearch(username);

        ApiResponseEntity apiResponseEntity = ApiResponseEntity.builder()
                .statusCode(200)
                .message("게임 매칭 요청 취소 완료")
                .object(null)
                .build();

        return new ResponseEntity<>(apiResponseEntity, HttpStatus.OK);
    }
}
