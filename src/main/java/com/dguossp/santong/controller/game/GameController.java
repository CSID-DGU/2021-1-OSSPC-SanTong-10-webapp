package com.dguossp.santong.controller.game;

import com.dguossp.santong.dto.request.StompSendMessage;
import com.dguossp.santong.dto.response.ApiResponseEntity;
import com.dguossp.santong.dto.response.GameMatchingResponse;
import com.dguossp.santong.entity.GameRecords;
import com.dguossp.santong.entity.Games;
import com.dguossp.santong.entity.Users;
import com.dguossp.santong.repository.GameRecordsRepository;
import com.dguossp.santong.repository.GamesRepository;
import com.dguossp.santong.repository.UsersRepository;
import com.dguossp.santong.security.UserInformation;
import com.dguossp.santong.service.game.GameService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;


@Slf4j
@RestController
@RequestMapping("/api/game")
public class GameController {

    @Autowired
    private GameService gameService;

    @Autowired
    private GameRecordsRepository gameRecordsRepository;

    @Autowired
    private GamesRepository gamesRepository;

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;
    // https://docs.spring.io/spring-framework/docs/current/reference/html/web.html#websocket-stomp-handle-annotations
    // is serialized to a payload through a matching MessageConverter
    // and sent as a Message to the brokerChannel, from where it is broadcast to subscribers.

    // Path variables in Spring WebSockets @SendTo mapping
    // https://stackoverflow.com/questions/27047310/path-variables-in-spring-websockets-sendto-mapping
    @MessageMapping("/place-stone/{gameId}")
    @SendTo("/topic/game/{gameId}")
    public void sendMessage(StompSendMessage sendMessage) {

        // 1) 데이터 저장 (GameRecords Entity)
        // 게임
        // 로그인 유저
        Games game = gamesRepository.findById(sendMessage.getGameId());

        Users loginUser = usersRepository.findByNickname(sendMessage.getLoginUserNickname());

        GameRecords gameRecords = GameRecords.builder()
                .game(game)
                .loginUser(loginUser)
                .isFinish(sendMessage.getIsFinish())
                .x(sendMessage.getX())
                .y(sendMessage.getY())
                .unallowedList("금수 위치 좌표 리스트 넣기!")
                .stoneStatus(sendMessage.getLoginUserTurn())
                .build();

        // [GameRecords to game_records] Database에 저장
        gameRecordsRepository.save(gameRecords);

        // 2) 각 클라이언트로 착수한 돌 정보 전송 ( 돌 상태, 좌표 값 전송 )
        this.simpMessagingTemplate.convertAndSend("/topic/game/"+sendMessage.getGameId(), sendMessage);

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
