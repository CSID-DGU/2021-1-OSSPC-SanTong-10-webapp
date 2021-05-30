// draw main board
gen_html_board(document.getElementById('pos_board'));

// init game state
var g_turn_color = null;
var g_board = gen_board();
var g_renju = new Renju(g_board);


// set event
var g_allowed = {};
for(var i=0; i < 15; i++){
    for(var j=0; j < 15; j++){

        var ids = i + '_' + j;
        var pos = document.getElementById(ids);
        g_allowed[ids] = true;

        (function(ids) {
            // -> 웹 소켓 수신 이벤
            pos.addEventListener('click',function() {


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

                // 제한 시간 초과 (30) 되는 경우, 둘 수 있는 곳 중, 랜덤으로 착수한 후, STOMP SEND
                // setTimeout() ->





                if (g_allowed[ids] == WIN) {
                    // 해당 위치에 '수'를 놓는 경우 게임이 끝나는 경우 (결정 수인 경우)
                    // 서버 측에 게임 종료된 시점에도 동일하게 데이터 전송.
                    var t = ids.split('_');
                    var y = parseInt(t[0]);
                    var x = parseInt(t[1]);

                    alert(x + " WIN " + y + " 클릭!");

                    stoneObject.isFinish = 2;
                    stoneObject.x = x;
                    stoneObject.y = y;
                    stompClient.send("/app/place-stone/"+gameId, {} ,JSON.stringify(stoneObject));

                    // [STOMP] 서버에 착수한 수의 정보를 포함한 부대 정보 전송
                    // 유저 고유 ID 또는 닉네임
                    // 게임 고유 ID
                    // 흑 또는 백돌 상태
                    // X, Y 좌표 값

                }

                else if (g_allowed[ids] == ALLOWED) {
                    // 해당 위치에 '수'를 놓는 경우 게임이 계속 진행되는 경우 (결정 수가 아닌 경우)

                    var t = ids.split('_');
                    var y = parseInt(t[0]);
                    var x = parseInt(t[1]);
                    alert(x + " ALLOWED " + y + " 클릭!");

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
            var result = g_renju.chk_rules(j, i, g_turn_color);

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
            // draw red
            if (g_board[i][j] == EMPTY && result == NOT_ALLOWED) {
                pos.className = 'red_stone';
            }
        }
    }
    console.log("g_allowed : " + g_allowed);
    for (var key in g_allowed){
        // 1 중에서 랜덤으로 하나 픽해서 시간 초과하는 경우 그 곳에 돌 놓기.
        console.log("attr: " + key + ", value: " + g_allowed[key]);
    }
}

chk_turn_board();
