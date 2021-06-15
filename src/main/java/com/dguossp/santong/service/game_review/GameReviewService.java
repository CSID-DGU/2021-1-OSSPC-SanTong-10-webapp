package com.dguossp.santong.service.game_review;

import com.dguossp.santong.dto.request.DoReviewReqDto;
import com.dguossp.santong.dto.request.GameReviewReqDto;
import com.dguossp.santong.dto.response.GameRecordsDto;
import com.dguossp.santong.dto.response.GameReviewDto;
import com.dguossp.santong.dto.response.GameReviewResDto;
import com.dguossp.santong.entity.GameRecords;
import com.dguossp.santong.entity.Games;
import com.dguossp.santong.entity.Users;
import com.dguossp.santong.repository.GameRecordsRepository;
import com.dguossp.santong.repository.GamesRepository;
import com.dguossp.santong.repository.UsersRepository;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class GameReviewService {

    private static final String BASE_URL = "http://*/osspapp/init/";

    private static int IS_INIT_DEFAULT = -1;
    private static int IS_INIT_YES = 0;
    private static int IS_INIT_NO = 1;

    // 각 돌 상태 값 (흑 : 1, 백 : 2)
    private static int BLACK_TURN = 1;
    private static int WHITE_TURN = 2;

    // 각 돌 상태에 따라, 복기 페이지 진입 시 초기화면에 표시할 돌 수 초기화
    private static int BLACK_INIT_SIZE = 1;
    private static int WHITE_INIT_SIZE = 2;

    // 게임 종료 상태 플래그
    private static int GAME_OVER = 2;

    // AI 모델이 추천해주는 주변 좌표 수
    private static int AI_SERVICE_SIZE = 4;

    // 복기 데이터 각 좌표 값이 갖는 의미
    // 금수 위치 제외하고, TOP 4를 보여준다.
    // 각 Element 별로 x, y 값이 의미하는 것은 AI 모델이 착수할 위치
    // 그 좌표 값(가장 높은 확률)이 실제 유저가 놓은 좌표 값과 동일한 경우 -> Flag 0 ()
    // 그 좌표 값(가장 높은 확률)이 실제 유저가 놓은 좌표 값과 동일하지 않은 경우 -> Flag 1 ()
    // 그 좌표 값(Top 2~4)이 실제 유저가 놓은 좌표 값과 동일한 경우 -> Flag 2 ()
    // 그 좌표 값(Top 2~4)이 실제 유저가 놓은 좌표 값과 동일하지 않은 경우 -> Flag 3 ()
    private static int REVIEW_TOP_SAME = 0;
    private static int REVIEW_TOP_NOT_SAME = 1;
    private static int REVIEW_NOT_TOP_SAME = 2;
    private static int REVIEW_NOT_TOP_NOT_SAME = 3;

    // 위 4가지 경우에 모두 해당 사항 없는 경우(개념상) + 초기화
    private static int REVIEW_FLAG_INIT = -1;


    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private GamesRepository gamesRepository;

    @Autowired
    private GameRecordsRepository gameRecordsRepository;

    private ModelMapper modelMapper;

    public GameReviewResDto reviewBlack(String username, GameReviewReqDto gameReivewDto) {

        Users loginUser = usersRepository.findByNickname(username);

        Games game = gamesRepository.findFirstByParticipantAOrParticipantBAndGameStatusOrderByCreatedDatetimeDesc(loginUser, loginUser, GAME_OVER);
        GameRecords gameRecord = gameRecordsRepository.findFirstByGameAndLoginUserOrderByCreatedDatetimeDesc(game, loginUser);

        // 매퍼 객체 생성
        modelMapper = new ModelMapper();

        PropertyMap<GameRecords, GameRecordsDto> mapGameRecords = new PropertyMap<GameRecords, GameRecordsDto>() {
            // source (= 매핑 대상) : GameRecords
            @Override
            protected void configure() {
                map().mapX(source.getX());
                map().mapY(source.getY());
                map().mapStoneStatus(source.getStoneStatus());
            }
        };

        // 매퍼 객체에 위 관계를 설정
        modelMapper.addMappings(mapGameRecords);



        // 로그인 유저 돌 상태 (= 복기 요청한 유저가 흑돌 또는 백돌인 지 파악)
        // 1 : 흑돌, 2 : 백돌
        int loginUserStoneStatus = gameRecord.getStoneStatus();

        // 현재 복기페이지에서 보여줘야 하는 좌표 수
        // 예를 들어, 1인 경우 흑돌 1개
        // 예를 들어, 3인 경우 흑 / 백 / 흑 3개 ...
        int size = gameReivewDto.getSize();

        if (size == 0) {
            if (loginUserStoneStatus == BLACK_TURN) {
                size = BLACK_INIT_SIZE;
            } else {
                size = WHITE_INIT_SIZE;
            }
        } else if (size < 0) {
            if (loginUserStoneStatus == BLACK_TURN) {
                size = BLACK_INIT_SIZE;
            } else {
                size = WHITE_INIT_SIZE;
            }
        } else {
            size = gameReivewDto.getSize();
        }

        // 사이즈 예외 처리
        List<GameRecords> gameRecordListForCount = gameRecordsRepository.findAllByGame(game);
        int stone_totalSize = gameRecordListForCount.size();
        log.info("stone_totalSize : " + stone_totalSize);

        // 흑 -> 흑 시점에 종결 (흑 승리 또는 흑 패배)
        // 백 -> 백 시점에 종결 (백 승리 또는 백 패배)

        // 로그인 유저의 돌 상태
        // 마지막 돌의 상태

        // 로그인 유저의 돌 == 마지막 돌의 상태 -> 흑 또는 백 승리 (size = total_size)
        // 로그인 유저의 돌 != 마지막 돌의 상태 -> 흑 또는 백 패배 (size = total_size-1)

        int lastStoneStatus = gameRecordListForCount.get(stone_totalSize-1).getStoneStatus();
        log.info("로그인 유저 돌 상태 값 : " + loginUserStoneStatus);
        log.info("이긴 사람 돌 상태 값 : " + lastStoneStatus);

        if (loginUserStoneStatus == lastStoneStatus) {
            log.info("1");
            // 전체 게임에서 놓은 수 보다 큰 수를 요구하는 경우
            if (size > stone_totalSize) {
                // 사이즈 값을 게임에서 착수된 돌 개수 값으로 설정
                size = stone_totalSize;
            }

        } else {
            log.info("2");
            // 전체 게임에서 놓은 수 보다 큰 수를 요구하는 경우
            if (size > stone_totalSize) {
                // 사이즈 값을 게임에서 착수된 돌 개수 값으로 설정
                size = stone_totalSize-1;
            }
        }

        log.info("적용 사이즈 : " + size);
        // Pageable 오브젝트 내에서 size 값을 변수로 활용
        Pageable pageable = PageRequest.of(0, size);

        // 복기 페이지 중, 왼쪽 (= 단순 게임 기록 조회 목적)
        List<GameRecordsDto> gameRecordsList = gameRecordsRepository.findAllByGame(game, pageable).toList()
                .stream()
                .map(gameRecords -> modelMapper.map(gameRecords, GameRecordsDto.class))
                .collect(Collectors.toList());



        /** size가 해당 게임에서 진행된 전체 수보다 큰 경우, 예외처리 (-> 해당 게임에서 착수된 전체 수로 설정) */
        // 복기 페이지 중, 오른쪽 (=복기 AI 모델 통과 후 복기 목적)
        List<GameReviewDto> gameReviewList = new ArrayList<>();

        // X, Y, Rate 값 파싱 후 값 지정 -> [T&Rate]
        // 리스트 형식의 String 파싱 후, 각 요소 값에 연결

        // X, Y  값을 기준으로 -> AI 분석 결과를 가져올 로우를 탐색
        GameRecords gameRecordForReivew = gameRecordsRepository.findByXAndYAndGame(gameRecordsList.get(size-1).getX(), gameRecordsList.get(size-1).getY(), game);

        // 복기 요청을 보낸 시점 오목판에서 실제 유저가 놓은 수의 좌표 값
        int rx = gameRecordsList.get(size-1).getX();
        int ry = gameRecordsList.get(size-1).getY();

        // ex) ['7&7&99', '8&8&92'] --> X&Y&Rate (복기 모델 결과 데이터)
        String xAndyAndrate = gameRecordForReivew.getReviewList();



        // 금수 리스트 (복기 리뷰 데이터 리스트와 비교할 리스트 형태로 변환)
        // 리뷰 데이터 중, 금수 위치를 제외하기 위함 (리스트 간 요소 비교 진행)
        List<String> compareUnallowedList = new ArrayList<>();
        List<String> compareReviewDataList = new ArrayList<>();

        // 금수 리스트
        // [7&6, 7&11, 8&6, 10&6, 10&11]
        String unallowedStr = gameRecordForReivew.getUnallowedList();
        unallowedStr = unallowedStr.replace("[", "");
        unallowedStr = unallowedStr.replace("]", "");
        unallowedStr = unallowedStr.replace(" ", "");

        // ',' 스플릿을 통해 Array 형태로 변환
        String[] unallowedStrArray = unallowedStr.split(",");

        for (int i = 0; i < unallowedStrArray.length; i++) {
            compareUnallowedList.add(unallowedStrArray[i]);
        }


        /** null check 현재는 "" 로 비교  */
        if (xAndyAndrate != null) {
            // Java Replace 1. [, ], ', " " -> 공란
            xAndyAndrate = xAndyAndrate.replace("[", "");
            xAndyAndrate = xAndyAndrate.replace("]", "");
            xAndyAndrate = xAndyAndrate.replace("'", "");
            xAndyAndrate = xAndyAndrate.replace(" ", "");

            // 복기 리뷰 데이터 [, ], ' 슬라이싱 처리 후
            // 7&7&99, 6&6&77 ...
            String reviewDataStr = xAndyAndrate;

            // ',' 스플릿을 통해 Array 형태로 변환
            String[] reviewDataStrArray = reviewDataStr.split(",");

            for (int i = 0; i < reviewDataStrArray.length; i++) {
                // subString 을 통해 복기 데이터 중, 확률 값을 제외한 y&x 좌표 값만 추출
                // 두 번째 & 인덱스를 기준으로 0 ~ 두번째 & 위치 (substring 인덱스 값 미포함)
                int targetIndex = reviewDataStrArray[i].lastIndexOf("&");
                reviewDataStrArray[i] = reviewDataStrArray[i].substring(0, targetIndex);
                compareReviewDataList.add(reviewDataStrArray[i]);
            }



            // 리뷰 데이터에서 삭제 처리할 데이터 인덱스 리스트
            ArrayList<Integer> removeIndexList = new ArrayList<>();

            // 복기 데이터 중, 금수 위치에 있는 것을 제외 (리스트 간 요소 비교)
            // 비교 후, 금수 위치를 제외한 복기 데이터 리스트 활용
            for (int i = 0; i < compareReviewDataList.size(); i++) {
                // 리뷰 데이터에, 금수 위치 데이터가 포함된 경우 -> 리뷰 데이터에서 해당 좌표 값을 삭제
                for (int j = 0; j < compareUnallowedList.size(); j++) {
                    if (compareReviewDataList.get(i).equals(compareUnallowedList.get(j))) {
                        log.info("금수 = 리뷰 : " + compareReviewDataList.get(i) + ", i : " + i);
                        // 금수 위치와 중복되는 복기 결과를 제외 (위 복기vs리뷰 비교 중, 리뷰 데이터 인덱스 값 기준)
                        // 이 곳에서 리스트 값을 삭제하면, 에러 (Loop 내에서 실시간으로 삭제하지 못하도록 되어 있음)
                        removeIndexList.add(i);
                    }
                }
            }

            // ',' 를 기준으로 배열형태로 변환 (via split) (review_list)
            String[] arr_xAndyAndrate = xAndyAndrate.split(",");

            // 금수 = 리뷰 인 인덱스 값을 통해 해당 요소 리뷰 데이터 리스트에서 삭제 (인덱스 값이 삭제 시점 별로 계속, 바뀌므로 실제 대상 요소가 삭제되지 않음, compareReviewDataList -> 사이즈만 확인하는 격)
            for (int i = 0; i < removeIndexList.size(); i++) {
                changeToNullOnArr(removeIndexList.get(i), arr_xAndyAndrate);
            }

            // 복기 데이터 (좌표값&확률) 값 어레이에서 null 값 제거
            List<String> resultReviewArr = new ArrayList<>();
            for (String element : arr_xAndyAndrate) {
                if (element != null) {
                    resultReviewArr.add(element);
                }
            }
            arr_xAndyAndrate = resultReviewArr.toArray(new String[resultReviewArr.size()]);

            for (int i = 0; i < arr_xAndyAndrate.length; i++) {
                log.info("삭제 후 각 요소 값 : " + arr_xAndyAndrate[i]);
            }


            // Top 4를 제공 (금수 위치 제외하고 Top 4가 안 되는 경우, 복기 데이터 사이즈만큼만 제공)
            int serviceSize = AI_SERVICE_SIZE;
            if (compareReviewDataList.size() - removeIndexList.size() >= 4) {
                serviceSize = AI_SERVICE_SIZE;
            } else {
                // EXCEPTION 사이즈가 0 또는 음수인 경우
                serviceSize = compareReviewDataList.size() - removeIndexList.size();
            }

            // 금수 위치 제외하고, TOP 4를 보여준다.
            // 각 Element 별로 x, y 값이 의미하는 것은 AI 모델이 착수할 위치
            // 그 좌표 값(가장 높은 확률)이 실제 유저가 놓은 좌표 값과 동일한 경우 -> Flag 0 ()
            // 그 좌표 값(가장 높은 확률)이 실제 유저가 놓은 좌표 값과 동일하지 않은 경우 -> Flag 1 ()
            // 그 좌표 값(Top 2~4)이 실제 유저가 놓은 좌표 값과 동일한 경우 -> Flag 2 ()
            // 그 좌표 값(Top 2~4)이 실제 유저가 놓은 좌표 값과 동일하지 않은 경우 -> Flag 3 ()
            for (int i = 0; i < serviceSize; i++) {

                // NPE 체크
                if (arr_xAndyAndrate[i] != null) {
                    // 각 요소 별로 --> x, y, rate 파싱
                    String[] arr_xyrate = arr_xAndyAndrate[i].split("&");

                    // 유저가 놓은 수와 비교를 통해 flag 값 지정
                    int flag = REVIEW_FLAG_INIT;

                    // 1) (가장 높은 확률)이 실제 유저가 놓은 좌표 값과 동일한 경우
                    if (i == 0 && (rx == Integer.valueOf(arr_xyrate[1]) && ry == Integer.valueOf(arr_xyrate[0]))) {
                        flag = REVIEW_TOP_SAME;
                    }

                    // (가장 높은 확률)이 실제 유저가 놓은 좌표 값과 동일하지 않은 경우
                    if (i == 0 && (rx != Integer.valueOf(arr_xyrate[1]) || ry != Integer.valueOf(arr_xyrate[0]))) {
                        flag = REVIEW_TOP_NOT_SAME;
                    }

                    if (i != 0 && (rx == Integer.valueOf(arr_xyrate[1]) && ry == Integer.valueOf(arr_xyrate[0]))) {
                        flag = REVIEW_NOT_TOP_SAME;
                    }

                    if (i != 0 && (rx != Integer.valueOf(arr_xyrate[1]) || ry != Integer.valueOf(arr_xyrate[0]))) {
                        flag = REVIEW_NOT_TOP_NOT_SAME;
                    }


                    // 가장 마지막 수 (= size)의 돌 상태 값 -> 해당 상태 값을 기준으로 복기 분석 제공
                    GameReviewDto gameReviewDto = GameReviewDto.builder()
                            .x(Integer.valueOf(arr_xyrate[1]))
                            .y(Integer.valueOf(arr_xyrate[0]))
                            .winningRate(Integer.valueOf(arr_xyrate[2]))
                            .flag(flag)
                            .build();

                    gameReviewList.add(gameReviewDto);
                }

            }

        }
        // 복기 페이지 초기화면 여부 체크 (버튼 클릭 시, 맨 처음 또는 맨 뒤의 경우 Alert 띄우기 위함)
        int is_init_flag = IS_INIT_DEFAULT;
        if (size == stone_totalSize || size == stone_totalSize-1) {
            is_init_flag = IS_INIT_NO;
        } else if (size == 1 || size == 2) {
            is_init_flag = IS_INIT_YES;
        } else {
            is_init_flag = IS_INIT_DEFAULT;
        }



        // 복기 페이지 요청에 대한 최종 응답
        GameReviewResDto gameReviewResDto = GameReviewResDto.builder()
                .username(username)
                .size(size)
                .is_init(is_init_flag)
                .gameRecordList(gameRecordsList)
                .gameReviewRecordsList(gameReviewList)
                .build();

        return gameReviewResDto;
    }


    public String doRequestReviewData(DoReviewReqDto doReviewReqDto) {
        /**
         JSONObject
         {
         "object":
             {
                 "gameID" : 367,
                 "userStoneStatus": 2,
                 "placeandStatus":
                     [
                         [
                             {
                                 "rx" : 2,
                                 "ry" : 3
                             },
                             {
                                 "x": 1,
                                 "y": 2,
                                 "stoneStatus": 1
                             },
                             {
                                 "x": 1,
                                 "y": 4,
                                 "stoneStatus": 2
                             }
                         ]
                     ]
             }
         }
         */
        log.info("game id : " + doReviewReqDto.getGameID());
        log.info("login User turn : " + doReviewReqDto.getUserStoneStatus());


        // 매퍼 객체 생성
        modelMapper = new ModelMapper();

        PropertyMap<GameRecords, GameRecordsDto> mapGameRecords = new PropertyMap<GameRecords, GameRecordsDto>() {
            // source (= 매핑 대상) : GameRecords
            @Override
            protected void configure() {
                map().mapX(source.getX()); // rx
                map().mapY(source.getY()); // ry
                map().mapStoneStatus(source.getStoneStatus()); // stoneStatus
                map().mapPrevStatsList(source.getPrevStateList()); // x, y list
            }
        };

        // 매퍼 객체에 위 관계를 설정
        modelMapper.addMappings(mapGameRecords);

        // 게임 고유 ID로 게임 객체 탐색
        Games game = gamesRepository.findById(doReviewReqDto.getGameID());

        // 복기 요청 대상 게임 객체, 해당 게임 내에서 복기 요청한 유저에 해당 하는 데이터만 추출해서 요청 (예 : 흑 -> 흑 상황에서 복기 하는 경우만 추출)
        List<GameRecordsDto> gameRecordsList = gameRecordsRepository.findAllByGameAndStoneStatus(game, doReviewReqDto.getUserStoneStatus())
                .stream()
                .map(gameRecords -> modelMapper.map(gameRecords, GameRecordsDto.class))
                .collect(Collectors.toList());

        // 요청 보낼 데이터 JSON 형식으로 생성
        JSONObject outer_jsonObject = new JSONObject();

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("gameID", doReviewReqDto.getGameID());
        jsonObject.put("userStoneStatus", doReviewReqDto.getUserStoneStatus());


        JSONArray jsonArray = new JSONArray();
        // loop-1
        // 각 돌의 상태에서 초기 상태(=첫 번째 상태)는 복기 요청 데이터에서 제외한다. (흑 or 흑-백)
        // 인덱스를 0이 아닌 1부터 시작한다
        for (int i = 1; i < gameRecordsList.size(); i++) {
            JSONArray jsonArray_inner = new JSONArray();
            JSONObject jsonObject_rx_ry = new JSONObject();
            jsonObject_rx_ry.put("rx", Integer.valueOf(gameRecordsList.get(i).getX()));
            jsonObject_rx_ry.put("ry", Integer.valueOf(gameRecordsList.get(i).getY()));
            jsonArray_inner.add(jsonObject_rx_ry);

            // loop-2
            // String -> Array (prevStateList from db was String)
            // ex) prevStateList : [1_2, 2_3, 1_4, 2_5]
            String prevStateStr = gameRecordsList.get(i).getPrevStateList();
            prevStateStr = prevStateStr.replace("[", "");
            prevStateStr = prevStateStr.replace("]", "");
            prevStateStr = prevStateStr.replace(" ", "");
            // ex) prevStateList : 1_2,2_3,1_4,2_5

            // ","를 통해 스플릿 -> to Array
            String[] prevStateArr = prevStateStr.split(",");
            for (int j = 0; j < prevStateArr.length; j++) {

                JSONObject jsonObject_x_y = new JSONObject();
                // x, y 값을 각각 추출하기 위해서, "_" 를 기준으로 스플릿
                String[] arr_xy = prevStateArr[j].split("_");
                jsonObject_x_y.put("x", Integer.valueOf(arr_xy[1]));
                jsonObject_x_y.put("y", Integer.valueOf(arr_xy[0]));
                jsonObject_x_y.put("stoneStatus", Integer.valueOf(arr_xy[2]));
                jsonArray_inner.add(jsonObject_x_y);
            }
            jsonArray.add(jsonArray_inner);
        }
        jsonObject.put("placeandStatus", jsonArray);
        outer_jsonObject.put("object", jsonObject);

//        log.info(outer_jsonObject.toJSONString());

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> responseEntity = restTemplate.postForEntity(BASE_URL, outer_jsonObject, String.class);
        return responseEntity.getBody();
    }

    // 특정 인덱스에 있는 값 제거
    public void remove(int index, String[] array) {
        for (int i = index; i < array.length-1; i++) {
            array[i] = array[i+1];
            array[i+1] = null;
        }
    }

    // 특정 인덱스에 있는 값 null로 변환
    public void changeToNullOnArr(int index, String[] array) {
        array[index] = null;
    }



}
