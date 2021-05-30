var stompClient = null;

var loginUser = null;
var loginUserTurn = null;

var opponentUser = null;
var opponentUserTurn = null;
var gameId = null;




function setConnected(connected) {
    $("#connect").prop("disabled", connected);
    $("#disconnect").prop("disabled", !connected);
    if (connected) {
        $("#conversation").show();
    }
    else {
        $("#conversation").hide();
    }
    $("#greetings").html("");
}

// 메인 페이지 접근 시, 로그인 여부 확인하는 API 결과 값 (: 로그인 상태)
// 웹소켓 연결 시점
function connect() {
    var socket = new SockJS('/gs-guide-websocket');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        setConnected(true);
        console.log('Connected: ' + frame);
    });
}

// 유저가 [게임 찾기 요청] 클릭한 시점
function gameSearch() {
    console.log("게임 찾기 요청");
    const ajax = (ajax_url, ajax_type, ajax_data, ajax_data_type) => {
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

                // 생성된 게임 정보 구독 경로
                var sub_destination = res_all.gameSubDestination;
                console.log("게임 구독 정보 : " + sub_destination);

                // 로그인 유저 고유 닉네임
                loginUser = res_loginUser.nickname;
                // 로그인 유저 돌 상태
                loginUserTurn = res_loginUser.turn;

                // 게임 상대 유저 고유 닉네임
                opponentUser = res_opponentUser.nickname;

                // 게임 상대 유저의 돌 상태
                opponentUserTurn = res_opponentUser.turn;

                // 게임 고유 ID
                gameId = res_all.gameId;

                console.log("로그인 유저 고유 닉네임 : " + loginUser);
                console.log("로그인 유저 돌 상태 : " + loginUserTurn);
                console.log("게임 상대 유저 고유 닉네임 : " + opponentUser);
                console.log("게임 상대 유저 돌 상태 : " + opponentUserTurn);
                console.log("게임 정보 (고유 ID) : " + gameId);


                if (stompClient !== null) {
                    // 구독한 경로로 서버가 SEND 하는 경우 콜백 (게임 정보를 받는 경우 이 곳으로)
                    stompClient.subscribe(sub_destination, function (response) {
                        // [게임 서버] -> 각 클라이언트로 데이터 전송하면 받는 부분.
                        // !게임 종료 응답 받는 경우 -> 게임 찾기 | 복기 하기 선택 화면으로 넘김
                        // {"loginUserNickname":"test2","opponentUserNickname":"test","gameId":39,"loginUserTurn":1,"opponentUserTurn":0,
                        // "x":0,"y":1, "isFinish" : 1}
                        console.log("응답 받는 부분 : " + response.body);

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

                        if (resStoneObj.loginUserTurn == 1) {
                            g_turn_color = BLACK;
                        } else {
                            g_turn_color = WHITE;
                        }


                        // 오목판에, 흑 또는 백돌 렌더링 (VIEW) -> 서버로부터 착수 데이터 정보를 수신하는 경우 호출. (좌표 값 받아와서)
                        g_board[resStoneObj.y][resStoneObj.x] = g_turn_color;

                        chk_turn_board();

                        // 결정 수인 경우, 나머지 오목판에 착수하지 못하도록 설정
                        if (resStoneObj.isFinish == 2) {
                            for (var k in g_allowed) {
                                g_allowed[k] = NOT_ALLOWED;
                            }
                        }



                    });
                } else {
                    // 게임 찾기 시, 웹소켓 연결이 되지 않은 경우 (-> 예외처리)
                    console.log("게임 찾기 요청 시, 웹소켓 연결되지 않은 상태인 경우");
                }

            })
            .fail((data) => {
                console.log('에러 : ' + JSON.stringify(data));
            })
    };

    ajax('/api/game/async-search', 'POST', null, 'json');

}

function disconnect() {

    if (stompClient !== null) {
        stompClient.disconnect();
    }
    setConnected(false);
    console.log("Disconnected");
}

function sendName() {
    stompClient.send("/app/place-stone", {"gameStatue" : 1}, JSON.stringify({'name': $("#name").val()}));
}

function showGreeting(message) {
    console.log("message : " + message);
    $("#greetings").append("<tr><td>" + message + "</td></tr>");
}

$(function () {
    $("form").on('submit', function (e) {
        e.preventDefault();
    });
    $( "#connect" ).click(function() { connect(); });
    $( "#gamesearch" ).click(function() { gameSearch(); });
    $( "#disconnect" ).click(function() { disconnect(); });
    $( "#send" ).click(function() { sendName(); });
});

