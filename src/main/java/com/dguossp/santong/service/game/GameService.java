package com.dguossp.santong.service.game;


import com.dguossp.santong.dto.response.GameMatchingResponse;
import com.dguossp.santong.dto.response.UserGameInfoDto;
import com.dguossp.santong.dto.response.UserInfoDto;
import com.dguossp.santong.entity.Games;
import com.dguossp.santong.entity.Users;
import com.dguossp.santong.repository.GamesRepository;
import com.dguossp.santong.repository.UsersRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.async.DeferredResult;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Atomic Type
 * */

@Slf4j
@Service
public class GameService {

    // 게임 모드 (0 : PVC, 1 : PVP)
    private static final int MODE_PVC = 0;
    private static final int MODE_PVP = 1;

    // 게임 상태 (0 : 게임 중, 1 : 게임 종료)
    private static final int GAME_ING = 0;
    private static final int GAME_END = 1;


    // 오목 게임 수준 (0 : 초보, 1 : 중수, 2 : 고수)
    private static final int NOVICE = 0;
    private static final int IM = 1;
    private static final int ADVANCED = 2;

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private GamesRepository gamesRepository;


    // 게임 찾기 요청을 보낸 유저 (상)
    private Map<String, DeferredResult<GameMatchingResponse>> searchingAdvancedUsers;
    // 게임 찾기 요청을 보낸 유저 (IM = Intermediate)
    private Map<String, DeferredResult<GameMatchingResponse>> searchingIMUsers;
    // 게임 찾기 요청을 보낸 유저 (하)
    private Map<String, DeferredResult<GameMatchingResponse>> searchingNoviceUsers;
    // 웹소켓 연결된 유저
    private Map<String, String> wsConnectedUsers;

    // 게임 상대를 랜덤으로 배정할 때 사용
    private Random random;
    // 각 오목 게임 수준별로 게임 찾기 대기열에 있는 유저네임을 담고 있는 리스트 (리스트 내 값들은 각 대기열 Map의 keySet() 메소드를 통해 값 지정)
    private ArrayList<String> keySetsList;

    @PostConstruct
    private void init() {
        this.searchingAdvancedUsers = new ConcurrentHashMap<>();
        this.searchingIMUsers = new ConcurrentHashMap<>();
        this.searchingNoviceUsers = new ConcurrentHashMap<>();
        this.wsConnectedUsers = new ConcurrentHashMap<>();

        this.random = new Random();
        this.keySetsList = new ArrayList<>();
    }


    @Async("threadPoolTaskExecutor")
    public void onAsyncTask(String username, DeferredResult<GameMatchingResponse> deferredResult) {
        log.info("[1] onAsyncTask() Start");


        // [1] 유저의 오목 게임 수준에 따라서 게임 상대를 지정한다. (닉네임 정보를 통해 '유저' 객체 탐색 -> 유저의 오목 게임 수준 조회)
        Users loginUser = usersRepository.findByNickname(username);

        // [2] 유저 수준에 맞는 게임 대기열에 PUT
        if (loginUser.getLevel() == NOVICE) {
            log.info("로그인 유저 오목 게임 수준 : [초보]");
            // 게임 매칭 유저의 오목게임 수준이 "초보"
            searchingNoviceUsers.put(username, deferredResult);
            // [3] 게임 매칭 메소드 호출 -> 각 유저가 속한 레벨 대기열에서 상대 서칭.
            doMatchingGame(username, searchingNoviceUsers);

        } else if (loginUser.getLevel() == IM) {
            log.info("로그인 유저 오목 게임 수준 : [중수]");
            // 게임 매칭 유저의 오목게임 수준이 "중수"
            searchingIMUsers.put(username, deferredResult);
            // [3] 게임 매칭 메소드 호출 -> 각 유저가 속한 레벨 대기열에서 상대 서칭.
            doMatchingGame(username, searchingIMUsers);
        } else {
            log.info("로그인 유저 오목 게임 수준 : [고수]");
            // 게임 매칭 유저의 오목게임 수준이 "고수"
            searchingAdvancedUsers.put(username, deferredResult);
//            searchingAdvancedUsers.put("username", deferredResult);
            // [3] 게임 매칭 메소드 호출 -> 각 유저가 속한 레벨 대기열에서 상대 서칭.
            doMatchingGame(username, searchingAdvancedUsers);
        }

        // 지정한 게임 매칭 요청 시간이 초과한 경우 (예외 처리) -> 클라이언트 측에 (유저에게) "게임 매칭에 실패 했습니다. 다시 시도해주세요" 안내.
        deferredResult.onTimeout(() -> {

            // onTimeout() 콜백 발생 시,
            // 로그인 유저가 속한 레벨 대기열에서 해당 유저가 없는 경우는 이미 매칭 취소 상태
            // 따라서, TIMEOUT이 아닌 CANCEL 응답을 보낸다.
            boolean isCancelStatus = isCancelStauts(loginUser);

            // 게임 매칭결과 응답 초기화
            GameMatchingResponse gameMatchingResponse;

            if (!isCancelStatus) {
                gameMatchingResponse = GameMatchingResponse.builder()
                        .gameMatchingResult(GameMatchingResponse.GameMatchingResult.TIMEOUT)
                        .build();
            } else {
                gameMatchingResponse = GameMatchingResponse.builder()
                        .gameMatchingResult(GameMatchingResponse.GameMatchingResult.CANCEL)
                        .build();
            }

            deferredResult.setErrorResult(gameMatchingResponse);
        });

        // 게임 매칭 과정에서 예외 발생하는 경우 (예외 처리) -> 클라이언트 측에 예외에 따라서 안내

    }

    public void cancelGameSearch(String username) {

        // [1] 유저네임(고유식별자)를 통해 유저의 오목 수준 조회 -> 해당 대기열에서 해당 유저 삭제
        Users loginUser = usersRepository.findByNickname(username);

        // [2] 유저의 오목게임 수준 기준에 맞춰, 해당 대기열에서 유저 정보 삭제
        if (loginUser.getLevel() == NOVICE) {
            log.info("로그인 유저 오목 게임 수준 : [초보]");
            // 게임 매칭 유저의 오목게임 수준이 "초보"
            searchingNoviceUsers.remove(username);

        } else if (loginUser.getLevel() == IM) {
            log.info("로그인 유저 오목 게임 수준 : [중수]");
            // 게임 매칭 유저의 오목게임 수준이 "중수"
            searchingIMUsers.remove(username);
        } else {
            log.info("로그인 유저 오목 게임 수준 : [고수]");
            // 게임 매칭 유저의 오목게임 수준이 "고수"
            searchingAdvancedUsers.remove(username);
        }
    }

    private void doMatchingGame(String username, Map<String, DeferredResult<GameMatchingResponse>> deferredResultMap) {


        // 클라이언트 매칭 요청 시점, 유저의 게임 수준에 맞는 상대 유저 대기열에 있는 유저 수가 2 미만인 경우 (= 요청한 유저만 대기열에 있는 경우)
        // return, 매칭 요청한 유저가 지정한 요청 처리 시간 내 대기 상태가 된다.
        // 대기 상태에서 다른 상대유저가 매칭 요청을 보내는 경우 -> 게임 매칭 성공
        // 대기 상태에서 다른 상태유저가 매칭 요청을 보내지 않는 경우 -> 지정한 처리 시간 초과로 예외처리 대상 ("게임 매칭 실패")
        if (deferredResultMap.size() < 2) {
            log.info("매칭할 상대 유저가 없습니다. (게임 찾는 유저 수 : " + deferredResultMap.size() + ")");
            return;
        }

        // 로그인 유저 & 게임 상대 유저의 DeferredResult<GameMatchingResponse> 값을 가지고, 매칭 성공 응답 반환
        // 닉네임 값으로 DeferredResult 값을 조회함과 동시에 게임 찾기 대기열에서 삭제해야 함으로, remove() 메소드 활용해서 값 조회

        // 로그인 유저의 DeferredResult 오브젝트
        DeferredResult<GameMatchingResponse> loginUserDeferredResult = deferredResultMap.remove(username);

        // 게임 상대 유저 닉네임 (고유값) 값 (게임 상대를 랜덤하게 배정하기 위해 수준이 맞는 유저 중 랜덤으로 대상 지정)
        String opponentUsername = randomKey(deferredResultMap);

        // 게임 상대 유저의 DeferredResult 오브젝트
        DeferredResult<GameMatchingResponse> opponentUserDeferredResult = deferredResultMap.remove(opponentUsername);

        // 고유한 게임 방 ID 생성 -> 게임 매칭에 소속된 유저에게 WS STOMP SUB 경로로 활용 (해당 경로에 구독을 하고, 이를 통해 서로 게임 내 착수 데이터를 송/수신)

        // 각 참여자 유저 객체 생성 (로그인 유저는 매칭 성공 시점에 로그인한 유저를 기준으로 한다)
        Users loginUser = usersRepository.findByNickname(username);
        Users opponentUser = usersRepository.findByNickname(opponentUsername);

        // Games : Users 관계 -> Many to One 관계를 갖고 있다.
        // Games 객체를 생성 할 때 Primary 키로 고유한 ID가 생성된다.
        Games games = Games.builder()
                .title(username +  " VS " + opponentUsername)
                .gameMode(MODE_PVP)
                // Participant A,B는 특별한 의미를 갖지 않는다. 단, 여기서 값을 설정할 땐 A를 매칭 성공 시점의 로그인 유저로 설정한다.
                .participantA(loginUser)
                .participantB(opponentUser)
                .build();

        // Hibernate: insert into games
        gamesRepository.save(games);

        // Hibernate: select  from  where games0_.participanta=? and games0_.participantb=? and games0_.game_status=?
        Games gameForId = gamesRepository.findByParticipantAAndParticipantBAndGameStatus(loginUser, opponentUser, GAME_ING);
        // loginUser, opponentUser 참가자 생성된 게임 ID
        long gameId = gameForId.getId();

        // 게임에 참여한 유저 정보 오브젝트 생성 (로그인 유저 기준 -> 흑돌)
        // 매칭 성공 시점에서, 로그인 유저가 흑돌로 지정
        UserGameInfoDto loginUserGameInfoDto = UserGameInfoDto.builder()
                .nickname(username)
                .turn(true) // true : 흑돌, false : 백돌
                .gameLevel(loginUser.getLevel())
                .build();

        // 게임에 참여한 유저 정보 오브젝트 생성 (게임 상대 유저 기준 -> 백돌)
        // 매칭 성공 시점에서, 게임 상대 유저가 백돌로 지정
        UserGameInfoDto opponentUserGameInfoDto = UserGameInfoDto.builder()
                .nickname(opponentUsername)
                .turn(false) // true : 흑돌, false : 백돌
                .gameLevel(opponentUser.getLevel())
                .build();


        // 게임 매칭 응답 오브젝트 생성 (로그인 유저 기준)
        GameMatchingResponse loginUserMatchingResponse = GameMatchingResponse.builder()
                .gameMatchingResult(GameMatchingResponse.GameMatchingResult.SUCCESS)
                .loginUser(loginUserGameInfoDto)
                .opponentUser(opponentUserGameInfoDto)
                .gameSubDestination("/topic/"+ gameId)
                .build();


        // 게임 매칭 응답 오브젝트 생성 (게임 상대 유저 기준)
        GameMatchingResponse opponentUserMatchingResponse = GameMatchingResponse.builder()
                .gameMatchingResult(GameMatchingResponse.GameMatchingResult.SUCCESS)
                .loginUser(opponentUserGameInfoDto)
                .opponentUser(loginUserGameInfoDto)
                .gameSubDestination("/topic/"+ gameId)
                .build();

        // 로그인 유저에게 매칭 성공 시점에 응답
        loginUserDeferredResult.setResult(loginUserMatchingResponse);

        // 게임 상대 유저에게 매칭 성공 시점에 응답
        opponentUserDeferredResult.setResult(opponentUserMatchingResponse);

    }

    // 게임 상대유저 임의로 설정하기 (-> 상대 유저네임을 랜덤하게 픽해서 리턴)
    private String randomKey(Map<String, DeferredResult<GameMatchingResponse>> deferredResultMap) {
        keySetsList.addAll(deferredResultMap.keySet());
        log.info("랜덤으로 픽된 상대 유저네임 : " + keySetsList.get(random.nextInt(keySetsList.size())));
        return keySetsList.get(random.nextInt(keySetsList.size()));
    }

    // 온 타임 에러 발생 시, 로그인 유저가 게임 매칭 취소 상태인 지 체크
    private boolean isCancelStauts(Users loginUser) {

        if (loginUser.getLevel() == NOVICE) {
            // 게임 매칭 유저의 오목게임 수준이 "초보"
            if (searchingNoviceUsers.containsKey(loginUser.getNickname())) {
                // onTimeError
                return false;
            } else {
                // Cancel
                return true;
            }


        } else if (loginUser.getLevel() == IM) {
            // 게임 매칭 유저의 오목게임 수준이 "중수"

            if (searchingIMUsers.containsKey(loginUser.getNickname())) {
                // onTimeError
                return false;
            } else {
                // Cancel
                return true;
            }

        } else {
            // 게임 매칭 유저의 오목게임 수준이 "고수"

            if (searchingAdvancedUsers.containsKey(loginUser.getNickname())) {
                // onTimeError
                return false;
            } else {
                // Cancel
                return true;
            }
        }
    }



}
