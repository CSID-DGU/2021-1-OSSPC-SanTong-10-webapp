// draw main board
gen_html_board(document.getElementById('pos_board'));

// init game state
var g_turn_color = null;
var g_board = gen_board();
var g_renju = new Renju(g_board);

// 225 개의 위치에 모두 1 (= ALLOWED) 상태로 지정하고,
// 클릭 이벤트가 있을 때마다, 착수 정보 오브젝트를 STOMP SEND를 서버 측에 전송한다.
// 서버 측에서는 메시지 핸들러를 통해 해당 게임방을 구독하고 있는 유저들에게 착수된 데이터를 재전송한다.
var g_allowed = {};
for (var i=0; i < 15; i++) {
    for(var j=0; j < 15; j++){

        var ids = i + '_' + j;
        var pos = document.getElementById(ids);
        g_allowed[ids] = true;

        (function(ids) {
            // -> 웹 소켓 수신 이벤
            pos.addEventListener('click',function() {

                if (loginUser == null) {
                    alert("게임 시작 전 입니다.");
                    return;
                }

                // '흑돌 선'으로 지정하기 위해서, 게임 시작 후 백돌이 먼저 수를 두는 경우를 체크.
                var count = 0;
                for (var key in g_allowed){
                    if (g_allowed[key] == 1) {
                        count++;
                    }
                    if (count == 225) {
                        if (loginUserTurn == WHITE) {
                            alert("흑돌 선 입니다.");
                            return;
                        }
                    }
                }

                // 착수하는 유저의 '턴'이 아닌 경우 -> Alert 띄우기
                // 착수 시점 이후에, g_turn_color 가 상대방이 착수 이전인 경우 아직 변하지 않는다.
                // 따라서, 착수한 돌의 상태와, 인게임 내에서 로그인 유저의 돌 상태가 같은 경우 = 상대방 턴
                // 이 경우, Alert로 상대방 턴임을 안내하고, return.
                if (loginUserTurn == g_turn_color) {
                    alert("상대방 턴 입니다 :) 잠시만 기다려주세요.");
                    return;
                }

                // [STOMP] 서버에 착수한 수의 정보를 포함한 부대 정보 전송
                // 유저 고유 ID 또는 닉네임
                // 게임 고유 ID
                // 흑 또는 백돌 상태
                // X, Y 좌표 값
                console.log("[ALLOWED] 로그인 유저 (= 착수한 유저) : " + loginUser);
                console.log("[ALLOWED] 게임 상대 유저  : " + opponentUser);
                console.log("[ALLOWED] 게임 고유 ID : " + gameId);
                console.log("[ALLOWED] 로그인 유저 (= 착수한 유저)의 돌 상태 : " + loginUserTurn);
                console.log("[ALLOWED] 게임 상대 유저의 돌 상태 : " + opponentUserTurn);

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


                // 금수 좌표를 가지고 있는 리스트
                var arr_unavailable = new Array();
                for (var key in g_allowed) {
                    // console.log(key + " : "  + g_allowed[key]);
                    if (g_allowed[key] == 0) {
                        console.log("g_allowed[key] : " + key);
                        arr_unavailable.push(key);
                    }
                }
                stoneObject.unallowedList = arr_unavailable

                // 직전 좌표까지 저장
                var arr_prevState = new Array();
                console.log(typeof g_board);
                console.log(typeof g_allowed);
                for (var key in g_board) {


                    // if (g_board[key] == BLACK || g_board[key] == WHITE) {
                    //     console.log("g_board[key] : " + key);
                    //     arr_prevState.push(key);
                    // }
                }


                // JSON object 중, 착수할 좌표 값만 새로 받아와서 STOMP SEND Frame 전송
                if (g_allowed[ids] == WIN) {
                    // 해당 위치에 '수'를 놓는 경우 게임이 끝나는 경우 (결정 수인 경우)
                    // 서버 측에 게임 종료된 시점에도 동일하게 데이터 전송.
                    var t = ids.split('_');
                    var y = parseInt(t[0]);
                    var x = parseInt(t[1]);

                    stoneObject.isFinish = 2;
                    stoneObject.x = x;
                    stoneObject.y = y;
                    stompClient.send("/app/place-stone/"+gameId, {} ,JSON.stringify(stoneObject));
                }

                else if (g_allowed[ids] == ALLOWED) {
                    // 해당 위치에 '수'를 놓는 경우 게임이 계속 진행되는 경우 (결정 수가 아닌 경우)
                    var t = ids.split('_');
                    var y = parseInt(t[0]);
                    var x = parseInt(t[1]);

                    stoneObject.isFinish = 1;
                    stoneObject.x = x;
                    stoneObject.y = y;
                    stompClient.send("/app/place-stone/"+gameId, {} ,JSON.stringify(stoneObject));
                }
            }, false);

        })(ids);
    }
}

function chk_turn_board() {

    for (var i=0; i < 15; i++) {
        for (var j=0; j < 15; j++) {
            var ids = i + '_' + j;
            var pos = document.getElementById(ids);
            // 각 턴별로 Result -> 룰에 의해 둘 수 없는 곳, 허용되는 곳, 게임 위닝 샷을 나눈다.
            // 단, 착수 후 턴의 색이 구별되므로, 착수 시점 이후에는 상대방 턴 기준으로 결과 값을 추출해야 한다.
            var result = null;
            if (g_turn_color == BLACK) {
                result = g_renju.chk_rules(j, i, WHITE);
            } else {
                result = g_renju.chk_rules(j, i, BLACK);
            }

            if (result == NOT_ALLOWED) {
                g_allowed[ids] = NOT_ALLOWED;
            }
            if (result == ALLOWED ) {
                g_allowed[ids] = ALLOWED;

            }
            if (result == WIN ) {
                g_allowed[ids] = WIN;
            }

            // draw stone
            if (g_board[i][j] == BLACK) {
                pos.className = 'black_stone';
            }
            if (g_board[i][j] == WHITE) {
                pos.className = 'white_stone';
            }
            // draw red (금수 위치)
            // 화이트 입장에서 금수
            if (g_board[i][j] == EMPTY && result == NOT_ALLOWED && g_turn_color==BLACK) {
                pos.className = 'red_stone';
            }
            // 블랙 입장에서 금수
            if (g_board[i][j] == EMPTY && result == NOT_ALLOWED && g_turn_color==WHITE) {
                pos.className = 'blue_stone';
            }
        }
    }
}
// 최초, 오목판 로드 됐을 때 호출 (-> 모든 위치에 착수할 수 있는 상태)
chk_turn_board();


