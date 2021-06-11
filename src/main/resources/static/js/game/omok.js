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


    // 1 상대 유저
    // 2 나

    // 1 , 2 --> 각각 흑 / 백 몰라

    // 2번 흑 / 백 인지 알려줘 (전제)



    // 보드

    // 부대 정보

    // 요청한 사람이 흑돌 / 백돌
    // 게임 정보
    // 가장 마지막의 x,y

    //

    // 1 흑
    // 2 백

    // board = np.array([
    //     [0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.],
    //     [0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.],
    //     [0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.],
    //     [0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.],
    //     [0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.],
    //     [0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.],
    //     [0.,0.,0.,0.,0.,0.,1.,0.,2.,1.,0.,0.,0.,0.,0.],
    //     [0.,0.,0.,0.,0.,0.,0.,1.,2.,0.,0.,0.,0.,0.,0.],
    //     [0.,0.,0.,0.,0.,0.,1.,2.,1.,0.,0.,0.,0.,0.,0.],
    //     [0.,0.,0.,0.,0.,0.,2.,0.,0.,2.,0.,0.,0.,0.,0.],
    //     [0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.],
    //     [0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.],
    //     [0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.],
    //     [0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.],
    //     [0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.]
    // ])


    // 0. django 테스트 방식 (우선, 자체 내에서 print 하는 식으로)
    // 2. 1과 2의 의미 다시 한 번 -> 해석 관점에 따라 달라지는 것이라면, 현재 블랙1 화이트2 기준으로 할 것.

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

            stone.className = 'empty_stone_blue';

            stone.id = i +'_' + j;
            colElem.appendChild(cross_h);
            colElem.appendChild(cross_v);
            colElem.appendChild(stone);

            rowElem.appendChild(colElem);
        }
        boardElem.appendChild(rowElem);
    }

    elem.appendChild(boardElem);
}
