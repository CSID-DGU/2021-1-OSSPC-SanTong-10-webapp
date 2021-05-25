package com.dguossp.santong.service.game;


import com.dguossp.santong.dto.response.GameMatchingResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.async.DeferredResult;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Atomic Type
 * Stop Flag로 멈춘 스레드는 어떻게 처리?
 * */

@Slf4j
@Service
public class GameService {

    // 게임 찾기 요청을 보낸 유저
    private Map<String, DeferredResult<GameMatchingResponse>> gameSearchingUsers;

    // 웹소켓 연결된 유저
    private Map<String, String> wsConnectedUsers;

    @PostConstruct
    private void init() {
        this.gameSearchingUsers = new ConcurrentHashMap<>();
        this.wsConnectedUsers = new ConcurrentHashMap<>();
    }


    @Async("threadPoolTaskExecutor")
    public void onAsyncTask(String username, DeferredResult<GameMatchingResponse> deferredResult) {
        log.info("[1] onAsyncTask() Start");

        gameSearchingUsers.put(username, deferredResult);
        log.info("gameSearchingUsers size : " + gameSearchingUsers.size());

        doMatchingGame(deferredResult);


        deferredResult.onTimeout(() -> {
            deferredResult.setErrorResult("게임 매칭 요청 시간 초과");
        });


        deferredResult.onCompletion(() ->
                log.info("Processing complete")
        );


    }


    //
    public void setConnectUser(String sessionId) {
        wsConnectedUsers.put(sessionId, "test");
        log.info("size : " + wsConnectedUsers.size());
    }

    private void doMatchingGame(DeferredResult<GameMatchingResponse> deferredResult) {

        if (gameSearchingUsers.size() < 2) {
            log.info("게임 매칭 할 수 없음! 게임 찾는 유저 수 : " + gameSearchingUsers.size());
            return;
        }

        GameMatchingResponse gameMatchingResponse = GameMatchingResponse.builder()
                .gameSubDestination("/sub/destination")
                .loginUser(null)
                .opponentUser(null)
                .build();

        deferredResult.setResult(gameMatchingResponse);

    }
}
