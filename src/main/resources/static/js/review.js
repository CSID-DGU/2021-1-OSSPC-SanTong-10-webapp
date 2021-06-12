/** 복기 페이지 초기화면 요청 값(static) */
const INIT_SIZE = 0;

/** */
// 그 좌표 값(가장 높은 확률)이 실제 유저가 놓은 좌표 값과 동일한 경우 -> Flag 0 ()
// 그 좌표 값(가장 높은 확률)이 실제 유저가 놓은 좌표 값과 동일하지 않은 경우 -> Flag 1 ()
// 그 좌표 값(Top 2~4)이 실제 유저가 놓은 좌표 값과 동일한 경우 -> Flag 2 ()
// 그 좌표 값(Top 2~4)이 실제 유저가 놓은 좌표 값과 동일하지 않은 경우 -> Flag 3 ()
const REVIEW_TOP_SAME = 0;
const REVIEW_TOP_NOT_SAME = 1;
const REVIEW_NOT_TOP_SAME = 2;
const REVIEW_NOT_TOP_NOT_SAME = 3;


// 현재 사이즈 (= 현재 오목판에 렌더링된 좌표의 총 개수)
// 예를 들어, 흑-백-흑 인 경우(왼쪽 오목판 기준), current_size = 3
var current_size = 0;

/** 유저가 진행한 게임에 대한 정보 조회 */
// 복기 페이지 진입 시점
const ajax_fetch_reviewInfo = (ajax_url, ajax_type, ajax_data, ajax_data_type) => {
    $.ajax({
        // 요청을 보낼 URL
        url: ajax_url,
        // HTTP 요청 방식 (GET, POST)
        type: ajax_type,
        // 컨텐트 타입
        contentType: 'application/json',
        // HTTP 요청과 함께 서버로 보낼 데이터
        data: ajax_data,
        // 서버에서 보내줄 데이터의 타입 ( xml, text, html, json 등)
        dataType: ajax_data_type,
    })
        .done(function (data) {
            /**
             {
                 "statusCode" : 200,
                 "message" : "복기 페이지 데이터 응답",
                 "object" :
                 {
                     "size" : the-size-of-gameRecordList,
                     "gameRecordList" :
                         [
                             {
                                "x" : n, (0~14)
                                "y" : m, (0~14)
                                "stoneStatus" : 1 or 2 (1 : 흑 2 : 백)
                             },
                             {
                                "x" : n, (0~14)
                                "y" : m, (0~14)
                                "stoneStatus" : 1 or 2 (1 : 흑 2 : 백)
                             },
                             .
                             .
                             .
                         ],
                     "gameReviewRecordList" :
                         [

                         ]
                 }
             }
             * */
            var resReviewApi = JSON.parse(JSON.stringify(data));
            console.log('API(/api/game-review) 응답 : ' + JSON.stringify(data));
            var resReviewApiObject = JSON.parse(JSON.stringify(resReviewApi.object));
            console.log('resReviewApiObject : ' + JSON.stringify(resReviewApiObject));
            console.log("size : " + resReviewApiObject.size);
            console.log("gameRecordList :" + JSON.stringify(resReviewApiObject.gameRecordList));

            // 현재 오목판 수에 표현된 사이즈
            current_size = resReviewApiObject.size;
            console.log("현재 사이즈 : " + current_size);

            // for loop
            var gameRecordList = JSON.parse(JSON.stringify(resReviewApiObject.gameRecordList));
            for (var i = 0; i < gameRecordList.length; i++) {

                console.log("i.x : " + gameRecordList[i].x);
                console.log("i.y : " + gameRecordList[i].y);
                console.log(("i.stoneStatus : " + gameRecordList[i].stoneStatus));

                // 오목판 돌 Draw
                /** FROM [omok_game.js] */
                /** [!] 오른쪽 오목판에서 해당 시점 유저가 마지막에 놓은 위치는 특정 돌 상태로 둘 것 (인덱스 기반) */
                // 실제 돌 UI를 렌더링 할 포지션 값 (x_y)
                var position_left = document.getElementById(gameRecordList[i].y + '_' + gameRecordList[i].x);
                var position_right = document.getElementById(gameRecordList[i].y + '^' + gameRecordList[i].x);
                // 착수할 돌의 상태
                if (gameRecordList[i].stoneStatus == BLACK) {
                    g_board_left[gameRecordList[i].x][gameRecordList[i].y] = BLACK;
                    g_board_right[gameRecordList[i].x][gameRecordList[i].y] = BLACK;
                    position_left.className = 'black_stone';
                    position_right.className = 'black_stone';
                } else {
                    g_board_left[gameRecordList[i].x][gameRecordList[i].y] = WHITE;
                    g_board_right[gameRecordList[i].x][gameRecordList[i].y] = WHITE;
                    position_left.className = 'white_stone';
                    position_right.className = 'white_stone';
                }
                // 오른쪽 오목판에서 해당 시점 유저가 마지막에 놓은 위치는 특정 돌 상태로 둘 것
                if (i == gameRecordList.length-1) {
                    if (gameRecordList[i].stoneStatus == BLACK) {
                        g_board_right[gameRecordList[i].x][gameRecordList[i].y] = BLACK;
                        position_right.className = 'green_stone';
                    } else {
                        g_board_right[gameRecordList[i].x][gameRecordList[i].y] = WHITE;
                        position_right.className = 'green_stone';
                    }
                }
            }

            console.log("gameReviewRecordList :" + JSON.stringify(resReviewApiObject.gameReviewRecordsList));
            var gameReviewRecordList = JSON.parse(JSON.stringify(resReviewApiObject.gameReviewRecordsList));
            /**
             {
                 "x" : n,
                 "y" : m,
                 "stoneStatus" : 1 or 2,
                 "winningRate" : nn,
                 "flag" : 0
             }
             <Flag> 값 설명
                복기 데이터 각 좌표 값이 갖는 의미
                금수 위치 제외하고, TOP 4를 보여준다.
                각 Element 별로 x, y 값이 의미하는 것은 AI 모델이 착수할 위치
                그 좌표 값(가장 높은 확률)이 실제 유저가 놓은 좌표 값과 동일한 경우 -> Flag 0
                그 좌표 값(가장 높은 확률)이 실제 유저가 놓은 좌표 값과 동일하지 않은 경우 -> Flag 1
                그 좌표 값(Top 2~4)이 실제 유저가 놓은 좌표 값과 동일한 경우 -> Flag 2
                그 좌표 값(Top 2~4)이 실제 유저가 놓은 좌표 값과 동일하지 않은 경우 -> Flag 3
             * */

            for (var i = 0; i < gameReviewRecordList.length; i ++) {

                console.log("i.x : " + gameReviewRecordList[i].x);
                console.log("i.y : " + gameReviewRecordList[i].y);
                console.log("i.winningRate : " + gameReviewRecordList[i].winningRate);
                console.log("i.flag : " + gameReviewRecordList[i].flag);

                // 각 Element의 Flag 값에 따라서 돌 색상이 달라진다.
                // 유저가 놓은 수와 겹치는 경우, Overwrite 한다.
                // 실제 돌 UI를 렌더링 할 포지션 값 (x_y) on 오른쪽 오목판
                var position_right = document.getElementById(gameReviewRecordList[i].y + '^' + gameReviewRecordList[i].x);
                var flag = gameReviewRecordList[i].flag;

                if (flag == REVIEW_TOP_SAME) {
                    // 노란색
                    position_right.className = 'noran_stone';
                }

                if (flag == REVIEW_TOP_NOT_SAME) {
                    // 파란색
                    position_right.className = 'noran_stone';
                }

                if (flag == REVIEW_NOT_TOP_SAME) {
                    // 주황색
                    position_right.className = 'noran_stone';
                }

                if (flag == REVIEW_NOT_TOP_NOT_SAME) {
                    // 빨간색
                    position_right.className = 'noran_stone';
                }
            }
            // for loop
            // 오른쪽 오목판에 돌 Draw (OverWrite) - 복기 데이터 승률 데이터 렌더링

        })
        .fail((data) => {
            console.log('에러 : ' + JSON.stringify(data));
        })

};

// 요청 값 : 로그인 유저(흑 또는 백), 게임 ID

// 초기화면 :
// 흑 : 흑돌 하나(왼), 흑돌 하나(오)
// 백 : 흑돌 - 백돌(왼), 흑돌 - 백돌(오)

// 시점 별로 데이터 조회 (사이즈 값 초기화)
// 이전, 다음 버튼 조회 (사이즈 값 조절 +-)

// RequestBody(JSON) 값 (복기 페이지에서, 초기화를 위한 요청)
// 돌 상태에 따라서 초기 화면이 다름.
// 흑 : 왼쪽 오른쪽 오목판 모두 첫 흑돌 좌표 값
// 백 : 왼쪽 오른쪽 오목판 모두 흑돌 - 백돌 좌표 값
var reviewGaeObject = new Object();
reviewGaeObject.size = INIT_SIZE;

// 복기 페이지 접근 시 바로 호출
ajax_fetch_reviewInfo('/api/game-review', 'POST',  JSON.stringify(reviewGaeObject), 'json');


// 단, 사이즈가 1 또는 2인 경우에는 이전으로 갈 수 없다.
// 마지막 사이즈인 경우, 다음 버튼을 누를 수 없도록 한다.

// 이전 버튼 -2
// 이전 버튼으로 갈 때, 초기화를 하고 다시 그림
function prevButton() {

    for (var i=0; i < 15; i++) {

        for (var j = 0; j < 15; j++) {

            var ids_left = i + '_' + j;
            var position_left = document.getElementById(ids_left);
            var ids_right = i + '^' + j;
            var position_right = document.getElementById(ids_right);

            position_left.className = '';
            position_right.className = '';

        }
    }
    reviewGaeObject.size = current_size - 2;
    ajax_fetch_reviewInfo('/api/game-review', 'POST',  JSON.stringify(reviewGaeObject), 'json');
}

// 다음 버튼 +2
// 다음 버튼으로 갈 때, 초기화를 하고 다시 그림
function nextButton() {

    for (var i=0; i < 15; i++) {

        for (var j = 0; j < 15; j++) {

            var ids_left = i + '_' + j;
            var position_left = document.getElementById(ids_left);
            var ids_right = i + '^' + j;
            var position_right = document.getElementById(ids_right);

            position_left.className = '';
            position_right.className = '';

        }
    }
    reviewGaeObject.size = current_size + 2;
    ajax_fetch_reviewInfo('/api/game-review', 'POST',  JSON.stringify(reviewGaeObject), 'json');
}