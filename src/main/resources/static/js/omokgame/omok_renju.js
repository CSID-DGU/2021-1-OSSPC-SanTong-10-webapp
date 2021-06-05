function chk6(player, mover){
    // OOOOOO
    for(var i=-5; i <= 0; i++){
        if(player != mover.get_color(i+0)) continue;
        if(player != mover.get_color(i+1)) continue;
        if(player != mover.get_color(i+2)) continue;
        if(player != mover.get_color(i+3)) continue;
        if(player != mover.get_color(i+4)) continue;
        if(player != mover.get_color(i+5)) continue;
        return 1;
    }
    return 0;
}
function chk5(player, mover){
    // NOOOOON
    for(var i=-5; i <= -1; i++){
        if(player == mover.get_color(i+0)) continue;
        if(player != mover.get_color(i+1)) continue;
        if(player != mover.get_color(i+2)) continue;
        if(player != mover.get_color(i+3)) continue;
        if(player != mover.get_color(i+4)) continue;
        if(player != mover.get_color(i+5)) continue;
        if(player == mover.get_color(i+6)) continue;
        return 1;
    }
    return 0;
}
function chk4(player, mover){
    var cnt = 0;
    // normal 4
    // NOEOOON
    for(var i=-5; i <= -1; i++){
        if(player == mover.get_color(i+0)) continue;
        if(player != mover.get_color(i+1)) continue;
        if(EMPTY  != mover.get_color(i+2)) continue;
        if(player != mover.get_color(i+3)) continue;
        if(player != mover.get_color(i+4)) continue;
        if(player != mover.get_color(i+5)) continue;
        if(player == mover.get_color(i+6)) continue;
        cnt++;
    }
    // NOOEOON
    for(var i=-5; i <= -1; i++){
        if(player == mover.get_color(i+0)) continue;
        if(player != mover.get_color(i+1)) continue;
        if(player != mover.get_color(i+2)) continue;
        if(EMPTY  != mover.get_color(i+3)) continue;
        if(player != mover.get_color(i+4)) continue;
        if(player != mover.get_color(i+5)) continue;
        if(player == mover.get_color(i+6)) continue;
        cnt++;
    }
    // NOOOEON
    for(var i=-5; i <= -1; i++){
        if(player == mover.get_color(i+0)) continue;
        if(player != mover.get_color(i+1)) continue;
        if(player != mover.get_color(i+2)) continue;
        if(player != mover.get_color(i+3)) continue;
        if(EMPTY  != mover.get_color(i+4)) continue;
        if(player != mover.get_color(i+5)) continue;
        if(player == mover.get_color(i+6)) continue;
        cnt++;
    }

    // open 4
    // NEOOOOEN
    for(var i=-5; i <= -2; i++){
        if(player == mover.get_color(i+0)) continue;
        if(EMPTY  != mover.get_color(i+1)) continue;
        if(player != mover.get_color(i+2)) continue;
        if(player != mover.get_color(i+3)) continue;
        if(player != mover.get_color(i+4)) continue;
        if(player != mover.get_color(i+5)) continue;
        if(EMPTY  != mover.get_color(i+6)) continue;
        if(player == mover.get_color(i+7)) continue;
        cnt++;
        return cnt;
    }

    // half 4
    // NOOOOEN
    for(var i=-5; i <= -1; i++){
        if(player == mover.get_color(i+0)) continue;
        if(player != mover.get_color(i+1)) continue;
        if(player != mover.get_color(i+2)) continue;
        if(player != mover.get_color(i+3)) continue;
        if(player != mover.get_color(i+4)) continue;
        if(EMPTY  != mover.get_color(i+5)) continue;
        if(player == mover.get_color(i+6)) continue;
        cnt++;
        return cnt;
    }
    // NEOOOON
    for(var i=-5; i <= -2; i++){
        if(player == mover.get_color(i+0)) continue;
        if(EMPTY  != mover.get_color(i+1)) continue;
        if(player != mover.get_color(i+2)) continue;
        if(player != mover.get_color(i+3)) continue;
        if(player != mover.get_color(i+4)) continue;
        if(player != mover.get_color(i+5)) continue;
        if(player == mover.get_color(i+6)) continue;
        cnt++;
        return cnt;
    }
    return cnt;
}
function chk3(player, mover){
    var cnt = 0;
    for(var i=-5; i <= -2; i++){
        if(player == mover.get_color(i+0)) continue;
        if(EMPTY  != mover.get_color(i+1)) continue;
        if(player != mover.get_color(i+2)) continue;
        if(EMPTY  != mover.get_color(i+3)) continue;
        if(player != mover.get_color(i+4)) continue;
        if(player != mover.get_color(i+5)) continue;
        if(EMPTY  != mover.get_color(i+6)) continue;
        if(player == mover.get_color(i+7)) continue;
        cnt++;
    }
    for(var i=-5; i <= -2; i++){
        if(player == mover.get_color(i+0)) continue;
        if(EMPTY  != mover.get_color(i+1)) continue;
        if(player != mover.get_color(i+2)) continue;
        if(player != mover.get_color(i+3)) continue;
        if(EMPTY  != mover.get_color(i+4)) continue;
        if(player != mover.get_color(i+5)) continue;
        if(EMPTY  != mover.get_color(i+6)) continue;
        if(player == mover.get_color(i+7)) continue;
        cnt++;
    }
    for(var i=-5; i <= -3; i++){
        if(player == mover.get_color(i+0)) continue;
        if(EMPTY  != mover.get_color(i+1)) continue;
        if(EMPTY  != mover.get_color(i+2)) continue;
        if(player != mover.get_color(i+3)) continue;
        if(player != mover.get_color(i+4)) continue;
        if(player != mover.get_color(i+5)) continue;
        if(EMPTY  != mover.get_color(i+6)) continue;
        if(player == mover.get_color(i+7)) continue;
        cnt++;
        return cnt;
    }
    for(var i=-4; i <= -2; i++){
        if(player == mover.get_color(i+0)) continue;
        if(EMPTY  != mover.get_color(i+1)) continue;
        if(player != mover.get_color(i+2)) continue;
        if(player != mover.get_color(i+3)) continue;
        if(player != mover.get_color(i+4)) continue;
        if(EMPTY  != mover.get_color(i+5)) continue;
        if(EMPTY  != mover.get_color(i+6)) continue;
        if(player == mover.get_color(i+7)) continue;
        cnt++;
        return cnt;
    }
    return cnt;
}

function Mover(board){
    this.board = board ;
    this.x = 0;
    this.y = 0;
    this.dx = 1;
    this.dy = 0;

    this.get_color = function(idx){
        var _x = this.x + this.dx * idx;
        var _y = this.y + this.dy * idx;

        if(0 <= _x && _x < 15){
            if(0 <= _y && _y < 15){
                return this.board[_y][_x];
            }
        }
        return WALLS;
    }
    this.set_direction = function(dir){
        switch (dir){
            case HORIZONTAL:
                this.dx = 1;
                this.dy = 0;
                break;
            case VERTICAL:
                this.dx = 0;
                this.dy = 1;
                break;
            case MAIN_DIAGONAL:
                this.dx = 1;
                this.dy = 1;
                break;
            case SKEW_DIAGONAL:
                this.dx = 1;
                this.dy = -1;
                break;
        }
    }
}


function Renju(board){
    this.board = board || gen_board();
    this.movers = [];
    for(var i=0; i < 4; i++){
        this.movers.push(new Mover(this.board));
    }
    this.movers[HORIZONTAL   ].set_direction(HORIZONTAL   );
    this.movers[VERTICAL     ].set_direction(VERTICAL     );
    this.movers[MAIN_DIAGONAL].set_direction(MAIN_DIAGONAL);
    this.movers[SKEW_DIAGONAL].set_direction(SKEW_DIAGONAL);

    this.chk_rules = function(x, y, player){
        if(0 <= x && x < 15){
            if(0 <= y && y < 15){
                // 비어있지 않으면
                if(this.board[y][x] != EMPTY){
                    return NOT_ALLOWED;
                }
                // 패턴 세기
                this.board[y][x] = player; //착수
                var o6_cnt = 0;
                var o5_cnt = 0;
                var o4_cnt = 0;
                var o3_cnt = 0;
                for(var i=0; i < 4; i++){
                    // 4방향 체크
                    this.movers[i].x = x;
                    this.movers[i].y = y;
                    o6_cnt += chk6(player, this.movers[i]);
                    o5_cnt += chk5(player, this.movers[i]);
                    o4_cnt += chk4(player, this.movers[i]);
                    o3_cnt += chk3(player, this.movers[i]);
                }
                this.board[y][x] = EMPTY; // 돌 제거

                // 룰 체크
                if(player == WHITE){
                    if(o5_cnt > 0 || o6_cnt > 0){
                        return WIN;
                    }else{
                        // 백은 금수가 없음
                        return ALLOWED;
                    }
                }else{
                    // BLACK
                    if(o6_cnt > 0){
                        // 육목이상 금지
                        return NOT_ALLOWED;
                    }
                    if(o5_cnt > 0){
                        //오목일 때 승리
                        return WIN;
                    }
                    if(o4_cnt >= 2 || o3_cnt >= 2){
                        // 쌍삼, 쌍사인 경우 금수
                        return NOT_ALLOWED;
                    }
                    // 그 외 허용
                    return ALLOWED;
                }
            }
        }
        // 오목판 범위 밖은 금수
        return NOT_ALLOWED;
    }
}