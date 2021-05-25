package com.dguossp.santong.controller.game;

import com.dguossp.santong.dto.request.Greeting;
import com.dguossp.santong.dto.request.HelloMessage;
import com.dguossp.santong.dto.request.SignUpDto;
import com.dguossp.santong.dto.response.ApiResponseEntity;
import com.dguossp.santong.dto.response.GameMatchingResponse;
import com.dguossp.santong.entity.redis.RedisGameSearch;
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

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.ForkJoinPool;


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


    @GetMapping("/async-search")
    public DeferredResult<GameMatchingResponse> defResultForGameSearching(Authentication authentication) {

        UserInformation userDetails = (UserInformation) authentication.getPrincipal();
        String username = userDetails.getUsername();



        DeferredResult<GameMatchingResponse> deferredResult = new DeferredResult<>(10000l);
        log.info("deferredResult : " + deferredResult);


        gameService.onAsyncTask(username, deferredResult);

        return deferredResult;
    }



}
