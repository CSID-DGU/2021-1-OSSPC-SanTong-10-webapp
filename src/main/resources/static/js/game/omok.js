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

    // 흑돌 또는 백돌 요청 여부 알고 있고
    // 각 좌표 값 별로 돌 상태 값이 있기 때문에
    // 2 또는 1을 결정

    // 예) 복기 서비스 (흑 요청) +  x, y (흑) = 2
    // 예) 복기 서비스 (흑 요청) +  x, y (백) = 1

    // n loop
    // 이중 배열(베이스) --> 인덱스 값 활용--> 특정 위치에 값을 넣고 완성 --> 인풋 활용 --> (모델 통과)결과 저장 (DB 저장)

    // 분석 요청하는 데이터 셋 --> 흑 // 백  --> 다음 흑을 어디에 둘까? (로그인 유저 = 복기 요청 유저 = 흑돌)
    // 흑 (x,y) 백(x,y)
    // 베이스 상태
    // base = [
    //     [0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.],
    //     [0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.],
    //     [0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.],
    //     [0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.],
    //     [0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.],
    //     [0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.],
    //     [0.,0.,0.,0.,0.,0.,2.,0.,0.,0.,0.,0.,0.,0.,0.],
    //     [0.,0.,0.,0.,0.,0.,0.,1.,0.,0.,0.,0.,0.,0.,0.],
    //     [0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.],
    //     [0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.],
    //     [0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.],
    //     [0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.],
    //     [0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.],
    //     [0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.],
    //     [0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.,0.]
    // ]

    // 총 30개

    // 15개 (흑)

    // for (15)
    // json - 순서대로 파싱하고 --> 15번

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

            stone.id = i +'&' + j;
            colElem.appendChild(cross_h);
            colElem.appendChild(cross_v);
            colElem.appendChild(stone);

            rowElem.appendChild(colElem);
        }
        boardElem.appendChild(rowElem);
    }

    elem.appendChild(boardElem);
}
