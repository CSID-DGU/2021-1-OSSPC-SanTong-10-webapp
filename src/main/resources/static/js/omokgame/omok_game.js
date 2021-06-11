// draw main board
gen_html_board(document.getElementById('omok-left'));
// init game state
var g_turn_color_2 = BLACK;
var g_board_2 = gen_board();

g_board_2[0][0] = g_turn_color_2;
var pos = document.getElementById(0 + '_' + 0);
// draw stone
if (g_board_2[0][0] == BLACK) {
    pos.className = 'black_stone';
}
if (g_board_2[0][0] == WHITE) {
    pos.className = 'white_stone';
}
