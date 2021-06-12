function findGame() {
    ajax('/api/game/async-search', 'POST', null, 'json');
}

function cancel() {
    fetch('/api/game/cancel-search', {
        method: 'POST'
    }).then(res => res.json()
    ).then(function (data) {
        // 게임 매칭 취소 API를 응답을 통해 처리하는 것은 없다 (-> 매칭 요청 응답 중 CANCEL 응답으로 대체)
        console.log("게임 매칭 취소 요청 응답 : " + JSON.stringify(data));

        // 게임 매칭 취소 성공
        if (data.statusCode == 200) {
            console.log("취소 성공");
        } else {
            console.log("취소 실패");
        }
    }).catch(function (error) {
        console.warn(error);
    });
}

// AI 모델 측에 데이터 전송
// 응답 받은 시점에 리뷰 페이지 이동 (응답 받을 때 까지 로딩 바 구현)
function review() {

    console.log("GameId :" + gameId);
    console.log("LoginUserTurn : " + loginUserTurn);

    var doRevewObject = new Object();
    doRevewObject.gameID = gameId;
    doRevewObject.userStoneStatus = loginUserTurn;
    /**
    {
        "object":
                {
                "gameID" : 367,
                "userStoneStatus": 2,
                "placeandStatus":
                    [
                        [
                            {
                                "rx" : 2,
                                "ry" : 3
                            },
                            {
                                "x": 1,
                                "y": 2,
                                "stoneStatus": 1
                            },
                            {
                                "x": 1,
                                "y": 4,
                                "stoneStatus": 2
                            }
                        ]
                    ]
                }
    }
*/
    ajax_req_review('/api/do-review', 'POST', JSON.stringify(doRevewObject), 'json');
}

const ajax_req_review = (ajax_url, ajax_type, ajax_data, ajax_data_type) => {
    $.ajax({
        // 요청을 보낼 URL
        url: ajax_url,
        // HTTP 요청 방식 (GET, POST)
        type: ajax_type,
        // 컨텐트 타입
        contentType: 'application/json',
        // HTTP 요청과 함께 서버로 보낼 데이터
        data: ajax_data,
        // 서버에서 보내줄 데이터의 타입 ( xml, text, html, json 등)
        dataType: ajax_data_type,
    })
        .done(function (data) {
            console.log("복기서비스 요청 응답 : " + JSON.stringify(data));
            // 복기 페이지로 이동
            location.href = '/review-game';
        })
        .fail((data) => {
            console.log('에러 : ' + JSON.stringify(data));
        })

};
