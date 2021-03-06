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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;


@Slf4j
@RestController
@RequestMapping("/api/game")
public class GameController {

    private static final int GAME_OVER = 2;

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
    @Transactional
    @MessageMapping("/place-stone/{gameId}")
    @SendTo("/topic/game/{gameId}")
    public void sendMessage(StompSendMessage sendMessage) {


        // 1) ????????? ?????? (GameRecords Entity)
        // ??????
        // ????????? ??????
        Games game = gamesRepository.findById(sendMessage.getGameId());
        // ?????? ?????? ?????? ???, Games ???????????? ?????? ??? ?????? (?????? ?????? ??????)
        if (sendMessage.getIsFinish() == GAME_OVER) {
            game.setGameStatus(GAME_OVER);
        }

        Users loginUser = usersRepository.findByNickname(sendMessage.getLoginUserNickname());

        GameRecords gameRecords = GameRecords.builder()
                .game(game)
                .loginUser(loginUser)
                .isFinish(sendMessage.getIsFinish())
                .x(sendMessage.getX())
                .y(sendMessage.getY())
                .unallowedList(sendMessage.getUnallowedList().toString())
                .prevStateList(sendMessage.getPrevStateList().toString())
                .stoneStatus(sendMessage.getLoginUserTurn())
                .build();

        // [GameRecords to game_records] Database??? ??????
        gameRecordsRepository.save(gameRecords);

        // 2) ??? ?????????????????? ????????? ??? ?????? ?????? ( ??? ??????, ?????? ??? ?????? )
        this.simpMessagingTemplate.convertAndSend("/topic/game/"+sendMessage.getGameId(), sendMessage);

    }


    @PostMapping("/async-search")
    public DeferredResult<GameMatchingResponse> defResultForGameSearching(Authentication authentication) {

        // Interceptor??? ?????? Authentication -> SecurityContextHolder??? ??????
        UserInformation userDetails = (UserInformation) authentication.getPrincipal();
        // Authentication ???????????? ???????????? ??????
        String username = userDetails.getUsername();

        // TIMEOUT : 10??? (= 10??? ??? ?????? ???????????? ????????? ??????, TIMEOUT CALLBACK)
        DeferredResult<GameMatchingResponse> deferredResult = new DeferredResult<>(10000l);
        gameService.onAsyncTask(username, deferredResult);

        return deferredResult;
    }

    @PostMapping("/cancel-search")
    public ResponseEntity<?> doLogin(Authentication authentication) {

        // Interceptor??? ?????? Authentication -> SecurityContextHolder??? ??????
        UserInformation userDetails = (UserInformation) authentication.getPrincipal();
        // Authentication ???????????? ???????????? ??????
        String username = userDetails.getUsername();

        // return void (?????? ???????????? ??????, ?????? ????????? ???????????? Throw ??? ?????? ?????? ??????)
        gameService.cancelGameSearch(username);

        ApiResponseEntity apiResponseEntity = ApiResponseEntity.builder()
                .statusCode(200)
                .message("?????? ?????? ?????? ?????? ??????")
                .object(null)
                .build();

        return new ResponseEntity<>(apiResponseEntity, HttpStatus.OK);
    }
}
