// draw main board
gen_html_board(document.getElementById('pos_board'));

// set game
let g_turn_color = EMPTY;
var g_board = gen_board();
var g_renju = new Renju(g_board);


// set event
var g_allowed = {};

function clickEventHander(ids, type) {
    if (g_allowed[ids] == WIN) {
        var t = ids.split('_');
        var y = parseInt(t[0]);
        var x = parseInt(t[1]);
        g_board[y][x] = g_turn_color;
        var bar = document.getElementById('status_bar');
        if (g_turn_color == WHITE) {
            bar.innerHTML = "WHITE WIN!!!";
        } else {
            bar.innerHTML = "BLACK WIN!!!";
        }
        var stone = document.getElementById(ids);
        // draw stone
        if (g_turn_color == BLACK) {
            stone.className = 'black_stone';
        }
        if (g_turn_color == WHITE) {
            stone.className = 'white_stone';
        }
        // end game
        for (var k in g_allowed) {
            g_allowed[k] = NOT_ALLOWED;
        }

        console.log({
            player: localStorage.getItem('player'),
            dolType: localStorage.getItem('dol_type'),
            board: g_renju.board,
            clicked: ids,
        })
        socket.emit('win', {
            player: localStorage.getItem('player'),
            dolType: localStorage.getItem('dol_type'),
            board: g_renju.board,
            clicked: ids,
        });
    }
    if (g_allowed[ids] == ALLOWED) {
        var t = ids.split('_');
        var y = parseInt(t[0]);
        var x = parseInt(t[1]);
        g_board[y][x] = g_turn_color;
        // change turn
        if (g_turn_color == WHITE) {
            g_turn_color = BLACK;
        } else {
            g_turn_color = WHITE;
        }

        chk_turn_board();
    }
}

function readyForGame() {
    for (var i = 0; i < 15; i++) {
        for (var j = 0; j < 15; j++) {
            var ids = i + '_' + j;
            var pos = document.getElementById(ids);
            g_allowed[ids] = true;
            (function (ids) {
                pos.addEventListener('click', function () {
                    if (g_allowed[ids]) {
                        socket.emit('update_game_data', {
                            board: g_renju.board,
                            userId: localStorage.getItem('player'),
                            clicked: ids
                        });
                    }

                    clickEventHander(ids);
                }, false);
            })(ids);
        }
    }

    // socket.emit('set_timer', {
    //   dolType: localStorage.getItem('dol_type'),
    //   player: localStorage.getItem('player'),
    //   seconds: SECOND
    // });

    // $('.positive').find('span').text(SECOND);
}

function updateGameData(color, board) {
    g_turn_color = color;
    g_renju = new Renju(g_board);
}

function chk_turn_board() {
    var bar = document.getElementById('status_bar');
    if (g_turn_color == WHITE) {
        bar.innerHTML = "WHITE TURN";
    } else {
        bar.innerHTML = "BLACK TURN";
    }

    for (var i = 0; i < 15; i++) {
        for (var j = 0; j < 15; j++) {
            var ids = i + '_' + j;
            var pos = document.getElementById(ids);

            var result = g_renju.chk_rules(j, i, g_turn_color, g_board);
            // chk win

            // set allowed map
            if (result == NOT_ALLOWED) {
                g_allowed[ids] = NOT_ALLOWED;
            }
            if (result == ALLOWED) {
                g_allowed[ids] = ALLOWED;
            }
            if (result == WIN) {
                g_allowed[ids] = WIN;
            }

            // draw stone
            if (g_board[i][j] == BLACK) {
                pos.className = 'black_stone';
            }
            if (g_board[i][j] == WHITE) {
                pos.className = 'white_stone';
            }

            //draw empty stone by turn
            if (g_turn_color == WHITE && g_board[i][j] == EMPTY) {
                pos.className = 'empty_stone_white';
            }
            if (g_turn_color == BLACK && g_board[i][j] == EMPTY) {
                pos.className = 'empty_stone_black';
            }

            // draw red
            if (g_board[i][j] == EMPTY && result == NOT_ALLOWED) {
                pos.className = 'red_stone';
            }
        }
    }
}

// chk_turn_board();
