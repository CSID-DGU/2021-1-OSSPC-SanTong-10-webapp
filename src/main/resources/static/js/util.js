function findGame(){
    ajax('/api/game/async-search', 'POST', null, 'json');
}

function cancel(){
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

function gameIng(){
    alert('게임 진행 중입니다.');
    return false;
}


