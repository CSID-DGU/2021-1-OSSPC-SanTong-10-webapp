package com.dguossp.santong.service.game_review;

import com.dguossp.santong.dto.request.GameReviewReqDto;
import com.dguossp.santong.dto.response.GameRecordsDto;
import com.dguossp.santong.dto.response.GameReviewDto;
import com.dguossp.santong.dto.response.GameReviewResDto;
import com.dguossp.santong.entity.GameRecords;
import com.dguossp.santong.entity.Games;
import com.dguossp.santong.repository.GameRecordsRepository;
import com.dguossp.santong.repository.GamesRepository;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class GameReviewService {

    @Autowired
    private GamesRepository gamesRepository;

    @Autowired
    private GameRecordsRepository gameRecordsRepository;

    private ModelMapper modelMapper;


    public GameReviewResDto reviewBlack(GameReviewReqDto gameReivewDto) {

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


        // 게임 고유 ID
        long gameId = gameReivewDto.getGameId();

        // 게임 고유 ID 기반으로, 어떤 게임에 대한 복기 요청인 지 조회 (-> 게임 객체 탐색)
        Games game = gamesRepository.findById(gameId);

        // 유저 돌 상태 (= 복기 요청한 유저가 흑돌 또는 백돌인 지 파악)
        // 1 : 흑돌, 2 : 백돌
        int turn = gameReivewDto.getTurn();

        // 현재 복기페이지에서 보여줘야 하는 좌표 수
        // 예를 들어, 1인 경우 흑돌 1개
        // 예를 들어, 3인 경우 흑 / 백 / 흑 3개 ...
        int size = gameReivewDto.getSize();

        // Pageable 오브젝트 내에서 size 값을 변수로 활용
        Pageable pageable0 = PageRequest.of(0, size);

        // 복기 페이지 중, 왼쪽 (= 단순 게임 기록 조회 목적)
        List<GameRecordsDto> gameRecordsList = gameRecordsRepository.findAllByGame(game, pageable0).toList()
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
        // ex) ['7&7&99', '8&8&92'] --> X&Y&Rate
        String xAndyAndrate = gameRecordForReivew.getReviewList();

        /** null check 현재는 "" 로 비교  */
        if (!xAndyAndrate.equals("")) {
            // Java Replace 1. [, ], ', " " -> 공란
            xAndyAndrate = xAndyAndrate.replace("[", "");
            xAndyAndrate = xAndyAndrate.replace("]", "");
            xAndyAndrate = xAndyAndrate.replace("'", "");
            xAndyAndrate = xAndyAndrate.replace(" ", "");

            // ',' 를 기준으로 배열형태로 변환 (via split)
            String[] arr_xAndyAndrate = xAndyAndrate.split(",");

            // For loop 추천 승률 수 만큼
            for (int i = 0; i < arr_xAndyAndrate.length; i++) {

                // 각 요소 별로 --> x, y, rate 파싱
                String xyrate = arr_xAndyAndrate[i];
                String[] arr_xyrate = xyrate.split("&");

                // 금수 리스트 비교 후, flag 결정 (추가 예정)

                // 가장 마지막 수 (= size)의 돌 상태 값 -> 해당 상태 값을 기준으로 복기 분석 제공
                GameReviewDto gameReviewDto = GameReviewDto.builder()
                        .stoneStatus(turn)
                        .x(Integer.valueOf(arr_xyrate[0]))
                        .y(Integer.valueOf(arr_xyrate[1]))
                        .winningRate(Integer.valueOf(arr_xyrate[2]))
                        .flag(1)
                        .build();

                gameReviewList.add(gameReviewDto);
            }

        }


        // 복기 페이지 요청에 대한 최종 응답
        GameReviewResDto gameReviewResDto = GameReviewResDto.builder()
                .size(size)
                .gameRecordList(gameRecordsList)
                .gameReviewRecordsList(gameReviewList)
                .build();

        return gameReviewResDto;




    }





}
