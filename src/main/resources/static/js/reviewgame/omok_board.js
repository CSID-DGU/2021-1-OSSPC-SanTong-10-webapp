// ENUMS

var EMPTY = 0;
var BLACK = 1;
var WHITE = 2;
var WALLS = 3;

var HORIZONTAL = 0;
var VERTICAL = 1;
var MAIN_DIAGONAL = 2;
var SKEW_DIAGONAL = 3;

var NOT_ALLOWED = 0;
var ALLOWED = 1;
var WIN = 2;

function gen_board(){
    return [
        [0,0,0,0,0, 0,0,0,0,0, 0,0,0,0,0],
        [0,0,0,0,0, 0,0,0,0,0, 0,0,0,0,0],
        [0,0,0,0,0, 0,0,0,0,0, 0,0,0,0,0],
        [0,0,0,0,0, 0,0,0,0,0, 0,0,0,0,0],
        [0,0,0,0,0, 0,0,0,0,0, 0,0,0,0,0],

        [0,0,0,0,0, 0,0,0,0,0, 0,0,0,0,0],
        [0,0,0,0,0, 0,0,0,0,0, 0,0,0,0,0],
        [0,0,0,0,0, 0,0,0,0,0, 0,0,0,0,0],
        [0,0,0,0,0, 0,0,0,0,0, 0,0,0,0,0],
        [0,0,0,0,0, 0,0,0,0,0, 0,0,0,0,0],

        [0,0,0,0,0, 0,0,0,0,0, 0,0,0,0,0],
        [0,0,0,0,0, 0,0,0,0,0, 0,0,0,0,0],
        [0,0,0,0,0, 0,0,0,0,0, 0,0,0,0,0],
        [0,0,0,0,0, 0,0,0,0,0, 0,0,0,0,0],
        [0,0,0,0,0, 0,0,0,0,0, 0,0,0,0,0],
    ]
}


function gen_html_board(elem){
    var boardElem = document.createElement('div');
    boardElem.className = 'board';
    for(var i=0; i< 15; i++){
        var rowElem = document.createElement('div');
        rowElem.className = 'board_row';
        for(var j=0; j < 15; j++){
            var colElem = document.createElement('div');
            colElem.className = 'board_col';
            var cross_h = document.createElement('div');
            var cross_v = document.createElement('div');
            var stone = document.createElement('div');
            cross_h.className = 'cross_h';
            cross_v.className = 'cross_v';
            if( i == 0){
                cross_v.className = 'cross_v_top';
            }
            if(i == 15-1){
                cross_v.className = 'cross_v_bottom';
            }
            if(j == 0){
                cross_h.className = 'cross_h_left';
            }
            if(j == 15-1){
                cross_h.className = 'cross_h_right';
            }
            stone.className = 'empty_stone_white';
            stone.id = i +'^' + j;
            colElem.appendChild(cross_h);
            colElem.appendChild(cross_v);
            colElem.appendChild(stone);

            rowElem.appendChild(colElem);
        }
        boardElem.appendChild(rowElem);
    }
    elem.appendChild(boardElem);
}
