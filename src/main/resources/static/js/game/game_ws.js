var stompClient = null;

var loginUser = null;
var loginUserTurn = null;

var opponentUser = null;
var opponentUserTurn = null;
var gameId = null;

// 각 턴별로 제한 시간 (30초)
var getTimeByTurn = 30;

// 회원 정보 조회 시 응답 데이터
var userInfo = null;


// 유저가 [게임 찾기 요청] 클릭한 시점
const ajax = (ajax_url, ajax_type, ajax_data, ajax_data_type) => {


    if (userInfo.statusCode == 2) {
        // 게임 찾기 클릭 시, 게임 찾는 중 / 취소 버튼 등장
        document.getElementById("findGame").style.display  = "none";
        document.getElementById("findingGame").style.display = "";     // '게임찾는 중' 버튼
        document.getElementById("cancel").style.display    = "";     // '취소' 버튼

        // 게임 찾기 버튼(게임 종류 후) --> visible
        document.getElementById("findGame2").style.display = "none";
        // 복기 하기 버튼 --> visible
        document.getElementById("review").style.display = "none";

        $.ajax({
            // 요청을 보낼 URL
            url: ajax_url,
            // HTTP 요청 방식 (GET, POST)
            type: ajax_type,
            // HTTP 요청과 함께 서버로 보낼 데이터
            data: ajax_data,
            // 서버에서 보내줄 데이터의 타입 ( xml, text, html, json 등)
            dataType: ajax_data_type,
        })
            .done(function (data) {
                // {
                // "gameMatchingResult":"SUCCESS",
                // "gameSubDestination":"/topic/game/16",
                // "loginUser": {"nickname":"test","turn":true,"gameLevel":2},
                // "opponentUser": {"nickname":"test2","turn":false,"gameLevel":2}
                // "gameId" : 1
                // }
                console.log('API(/api/game/async-search) 응답 : ' + JSON.stringify(data));

                // API 응답 오브젝트 형태로 Parsing.
                var res_all = JSON.parse(JSON.stringify(data));
                var res_loginUser = JSON.parse(JSON.stringify(res_all.loginUser));
                var res_opponentUser = JSON.parse(JSON.stringify(res_all.opponentUser));

                if (res_all.gameMatchingResult == "SUCCESS") {
                    // console.log("게임 매칭 성공!");
                    // [게임 중 버튼으로 변경]
                    document.getElementById("findingGame").style.display = "none";
                    document.getElementById("cancel").style.display = "none";
                    document.getElementById("gameIng").style.display = "";

                    // 로그인 유저 고유 닉네임
                    loginUser = res_loginUser.nickname;
                    var login_nickname = document.getElementById("login-nickname");
                    login_nickname.innerText = loginUser;

                    // 로그인 유저 돌 상태
                    loginUserTurn = res_loginUser.turn;

                    // 게임 상대 유저 고유 닉네임
                    opponentUser = res_opponentUser.nickname;
                    var oppo_nickname = document.getElementById("oppo-nickname")
                    oppo_nickname.innerText = opponentUser

                    // 게임 상대 유저의 돌 상태
                    opponentUserTurn = res_opponentUser.turn;

                    // 게임 고유 ID
                    gameId = res_all.gameId;

                    // console.log("로그인 유저 고유 닉네임 : " + loginUser);
                    // console.log("로그인 유저 돌 상태 : " + loginUserTurn);
                    // console.log("게임 상대 유저 고유 닉네임 : " + opponentUser);
                    // console.log("게임 상대 유저 돌 상태 : " + opponentUserTurn);
                    // console.log("게임 정보 (고유 ID) : " + gameId);

                    document.getElementById("box-left").style.display = "";
                    document.getElementById("box-right").style.display = "";
                    if (loginUserTurn == BLACK) {
                        document.getElementById("box-left").style.backgroundColor = "#000000";
                        document.getElementById("box-left").style.color = "#ffffff";
                        document.getElementById("box-right").style.backgroundColor = "#ffffff";

                        // 흑돌 사이즈에 금수 관련 인덱스 추가
                        document.getElementById("ban-sign-left").style.visibility = 'visible';

                    } else {
                        document.getElementById("box-left").style.backgroundColor = "#ffffff";
                        document.getElementById("box-right").style.backgroundColor = "#000000";
                        document.getElementById("box-right").style.color = "#ffffff";

                        // 흑돌 사이즈에 금수 관련 인덱스 추가
                        document.getElementById("ban-sign-right").style.visibility = 'visible';
                    }

                    // 생성된 게임 정보 구독 경로
                    var sub_destination = res_all.gameSubDestination;
                    // console.log("게임 구독 정보 : " + sub_destination);


                    if (stompClient !== null) {

                        // 5초 타이머 (게임 준비) - Block (게임 취소 등 요청 불가)

                        // [비동기] 각 턴 별로 30초 제한 시간
                        //최초 흑돌 선이므로, 이곳 타이머는 흑돌용 --> 타이머 30초 경과 시, 흑돌 유저가 랜덤하게 착수하도록.
                        initTimerForFiveSecs(BLACK);

                        // 구독한 경로로 서버가 SEND 하는 경우 콜백 (게임 정보를 받는 경우 이 곳으로)
                        stompClient.subscribe(sub_destination, function (response) {

                            // 타이머 해제 (setInterval() 종료)
                            clearInterval(timerId);

                            // [비동기] 각 턴 별로 30초 제한 시간
                            // 특정 유저가 착수를 한 경우, 상대유저에게 30초 제한 시간 부여 (턴 별로 __ 유저에게 부여)

                            // [게임 서버] -> 각 클라이언트로 데이터 전송하면 받는 부분.
                            // !게임 종료 응답 받는 경우 -> 게임 찾기 | 복기 하기 선택 화면으로 넘김
                            // {"loginUserNickname":"test2","opponentUserNickname":"test","gameId":39,"loginUserTurn":1,"opponentUserTurn":0,
                            // "x":0,"y":1, "isFinish" : 1}
                            // console.log("응답 받는 부분 : " + response.body);

                            // String loginUserNickname
                            // String opponentUserNickname
                            // long gameId
                            // int loginUserTurn
                            // int opponentUserTurn
                            // int x
                            // int y
                            // boolean isFinish (승부 결정 수 (= 결정 수) 1 : ~결정수 , 2 : 결정수)

                            var resStoneObj = JSON.parse(response.body);


                            // 착수한 돌의 상태 체크 (흑 또는 백) via loginUserTurn (착수한 유저의 돌 상태 값 활용)
                            // 1 : 흑돌 -> BLACK , 2 : 백돌 -> WHITE
                            if (resStoneObj.loginUserTurn == BLACK) {
                                g_turn_color = BLACK;
                            } else {
                                g_turn_color = WHITE;
                            }

                            // 오목판에, 흑 또는 백돌 렌더링 (VIEW) -> 서버로부터 착수 데이터 정보를 수신하는 경우 호출. (좌표 값 받아와서)
                            g_board[resStoneObj.y][resStoneObj.x] = g_turn_color;

                            // 보드 체크 (현재 착수한 상태에서, 놓을 수 있는 수, 놓을 수 없는 수, 게임 위닝 수)
                            chk_turn_board();


                            // 결정 수인 경우, 나머지 오목판에 착수하지 못하도록 설정
                            // 게임 종료 이후 이므로, 타이머 실행시키지 않는다.
                            if (resStoneObj.isFinish == 2) {
                                for (var k in g_allowed) {
                                    g_allowed[k] = NOT_ALLOWED;
                                }
                                /** 게임 승패 띄워주기 + 프로필 정보 띄우기 */

                                // 메인 페이지 또는 복기 페이지 연결되는 버튼 띄우기
                                console.log("게임 종료!")
                                // 게임 진행 중 버튼 --> invisible
                                document.getElementById("gameIng").style.display = "none";


                                // 로그인 유저 승리 (왼쪽 사이드 '승' 출력)
                                if (loginUserTurn ==  g_turn_color) {
                                    document.getElementById("game_result_left").innerText = "승";
                                    document.getElementById("game_result_right").innerText = "패";
                                }

                                // 로그인 유저 패배 (왼쪽 사이드 '패' 출력)
                                if (opponentUserTurn == g_turn_color) {
                                    document.getElementById("game_result_left").innerText = "패";
                                    document.getElementById("game_result_right").innerText = "승";
                                }


                                // 게임 찾기 버튼 --> visible
                                document.getElementById("findGame2").style.display = "";
                                // 복기 하기 버튼 --> visible
                                document.getElementById("review").style.display = "";


                            } else {
                                // 착수 시점에, 상대유저에게 타이머 시작
                                SetTime = getTimeByTurn;
                                if (g_turn_color == BLACK) {
                                    initTimerForFiveSecs(WHITE);
                                } else {
                                    initTimerForFiveSecs(BLACK);
                                }
                            }

                        });
                    } else {
                        // 게임 찾기 시, 웹소켓 연결이 되지 않은 경우 (-> 예외처리)
                        console.log("게임 찾기 요청 시, 웹소켓 연결되지 않은 상태인 경우");
                    }
                } else {
                    console.log("게임 매칭 실패 (취소 또는 타임아웃)")
                    //게임 매칭 요청 취소 또는 타임 아웃인 경우 공통처리
                    document.getElementById("findGame").style.display = "";
                    document.getElementById("findingGame").style.display = "none";
                    document.getElementById("cancel").style.display = "none";
                    document.getElementById("gameIng").style.display = "none";
                }
            })
            .fail((data) => {
                console.log('에러 : ' + JSON.stringify(data));
            })


    } else {
        alert("로그인 후 이용 가능합니다.")
        // 로그인 페이지로 이동
        window.location = "/login"
    }
};


function initTimerForFiveSecs(stoneStatus) {
    // 1초 단위로 msg_time() 함수 호출
    timerId = setInterval(msg_time, 1000, stoneStatus);
}


// 30초 제한 시간
var SetTime = getTimeByTurn;
// 돌 상태 여부, 유저
function msg_time(stoneStatus) {
    // @Param : stoneStatus -> 해당 유저의 돌 상태 (흑 또는 백)
    // 로그인 유저 또는 게임 상대 유저 중, 인풋으로 받은 돌 상태를 가진 유저의 타이머를 제어
    console.log("msg_time() is called");
    var htmlForTimer = null;
    if (stoneStatus == loginUserTurn) {
        htmlForTimer = document.getElementById("nickname");
    } else {
        htmlForTimer = document.getElementById("oppo_nickname");
    }


    m = (SetTime % 60) + "초"; // 초 단위로 남은 시간 계산
    var msg = m;
    // HTML 남은 시간 표기
    htmlForTimer.innerText = msg;
    // 1초 단위로 --
    SetTime--;
    // 타이머 종료 조건
    if (SetTime < 0) {

        // 타이머 해제 (setInterval() 종료)
        clearInterval(timerId);


        if (stoneStatus == loginUserTurn) {
            // 착수 가능한 위치 좌표를 가지고 있는 리스트
            var arr_available = new Array();
            // 금수 좌표를 가지고 있는 리스트
            var arr_unavailable = new Array();
            // 랜덤 위치에 착수
            // chk_turn_board() -> 를 통해서 g_board 상태 중 착수할 수 있는 위치가 최신화 (최초, 각 유저가 착수하는 시점)
            for (var key in g_allowed){
                // 1 중에서 랜덤으로 하나 픽해서 시간 초과하는 경우 그 곳에 돌 놓기.
                console.log("키 : " + key + ", 값 : " + g_allowed[key]);
                // 착수할 수 있는 위치 조건 (1 = 착수 가능)
                if (g_allowed[key] == ALLOWED) {
                    arr_available.push(key);
                }

                if (g_allowed[key] == NOT_ALLOWED) {
                    arr_unavailable.push(key);
                }
            }
            // 직전 좌표까지 저장
            var arr_prevState = new Array();
            for (var keys in g_board) {
                console.log(keys + " : " + g_board[keys]);
                for (var key in g_board[keys]) {
                    // key 0 ~ 14
                    if (g_board[key][keys] == BLACK || g_board[key][keys] == WHITE) {
                        // y(key), x(keys)
                        arr_prevState.push(key + "_" + keys + "_" + g_board[key][keys]);
                    }
                }
            }


            // 착수 가능한 위치 좌표를 가지고 있는 리스트 인덱스 수 내에서 랜덤 수를 활용해서,
            // 지정한 시간 초과했을 경우, 랜덤한 위치에 착수할 좌표 값 추출
            var ran_X_Y = arr_available[Math.floor(Math.random() * arr_available.length)];
            // X, Y 파싱, ex) x_y "_" 로 슬라이싱 (ex : 7_8)
            var arr_ran_X_Y = ran_X_Y.split("&");

            var stoneObject = new Object();
            // String loginUserNickname
            // String opponentUserNickname
            // long gameId
            // int loginUserTurn (1 : 흑돌 -> BLACK , 2 : 백돌 -> WHITE)
            // int opponentUserTurn (1 : 흑돌 -> BLACK , 2 : 백돌 -> WHITE)
            // int x
            // int y
            // boolean isFinish  (1 : ~결정수 , 2 : 결정수)
            stoneObject.loginUserNickname = loginUser;
            stoneObject.opponentUserNickname = opponentUser;
            stoneObject.gameId = gameId;
            stoneObject.loginUserTurn = loginUserTurn;
            stoneObject.opponentUserTurn = opponentUserTurn;
            stoneObject.unallowedList = arr_unavailable
            stoneObject.prevStateList = arr_prevState;

            // 랜덤으로 착수할 때는 항상 착수 가능한 지역 중 게임을 끝내지 않도록 설정
            stoneObject.isFinish = 1;
            stoneObject.x = arr_ran_X_Y[1];
            stoneObject.y = arr_ran_X_Y[0];

            // SEND (= 착수)
            stompClient.send("/app/place-stone/"+gameId, {} ,JSON.stringify(stoneObject));
        }

        // 제한 시간 리셋
        SetTime = getTimeByTurn;
        // 반대돌로 다시 시간 제한 (현재 스톤 상태의 반대 값을 인풋으로 넣는다)
        // 착수 시점에 스톤 상태가 바뀌므로, 반대 값을 넣는다.
        if (stoneStatus == BLACK) {
            initTimerForFiveSecs(WHITE);
        } else {
            initTimerForFiveSecs(BLACK);
        }
    }
}



// 로그인 여부 체크 후, 로그인 시점인 경우 웹소켓 커넥트
const ajax_user = (ajax_url, ajax_type, ajax_data, ajax_data_type) => {
    $.ajax({
        // 요청을 보낼 URL
        url: ajax_url,
        // HTTP 요청 방식 (GET, POST)
        type: ajax_type,
        // HTTP 요청과 함께 서버로 보낼 데이터
        data: ajax_data,
        // 서버에서 보내줄 데이터의 타입 ( xml, text, html, json 등)
        dataType: ajax_data_type,
    })
        .done(function (data) {
            // {
            // "statusCode : 1,
            // "message" : "비로그인 상태",
            // "object" : null
            // }
            console.log('API(/api/user/profile) 응답 : ' + JSON.stringify(data));

            var resStatusUser = JSON.parse(JSON.stringify(data));
            // 게임 매칭 클릭 시, 로그인 여부 체크할 수 있도록
            userInfo = resStatusUser;

            if (resStatusUser.statusCode == 1) {
                console.log("비로그인 상태");
            } else {
                console.log("로그인 상태 -> 웹소켓 연결");
                // 회원정보 업데이트

                var socket = new SockJS('/gs-guide-websocket');
                stompClient = Stomp.over(socket);
                stompClient.connect({}, function (frame) {
                    console.log('Connected: ' + frame);
                });
            }
        })
        .fail((data) => {
            console.log('에러 : ' + JSON.stringify(data));
        })
};

$(document).ready(function (){
    ajax_user('/api/user/profile', 'GET', null, 'json');
});

