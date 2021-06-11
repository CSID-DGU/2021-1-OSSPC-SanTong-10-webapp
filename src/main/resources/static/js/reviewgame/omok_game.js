// draw main board
gen_html_board(document.getElementById('omok-right'));
// init game state
var g_turn_color_3 = WHITE;
var g_board_3 = gen_board();

g_board_3[0][0] = g_turn_color_3;
var pos = document.getElementById(1 + '^' + 1);
// draw stone
if (g_board_3[0][0] == BLACK) {
    pos.className = 'black_stone';
}
if (g_board_3[0][0] == WHITE) {
    pos.className = 'white_stone';
}
