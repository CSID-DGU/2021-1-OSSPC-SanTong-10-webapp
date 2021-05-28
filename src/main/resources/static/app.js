var stompClient = null;

function setConnected(connected) {
    $("#connect").prop("disabled", connected);
    $("#disconnect").prop("disabled", !connected);
    if (connected) {
        $("#conversation").show();
    }
    else {
        $("#conversation").hide();
    }
    $("#greetings").html("");
}

// 메인 페이지 (로그인 상태로 접근하는 경우) 웹소켓 연결
function connect() {
    var socket = new SockJS('/gs-guide-websocket');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        setConnected(true);
        console.log('Connected: ' + frame);
    });
}

// 게임 찾기
function gameSearch() {
    console.log("do Request Game Search!");
    $("#gamesearch").prop("disabled", true);
    if (stompClient !== null) {
        stompClient.subscribe('/topic/greetings', function (greeting) {
            showGreeting(JSON.parse(greeting.body).content);
        }, {"test" : 1});
    } else {
        // 게임 찾기 시, 웹소켓 연결이 되지 않은 경우 (-> 예외처리)
        console.log("Game Search : 웹소켓 연결 X");
    }

    const ajax = (ajax_url, ajax_type, ajax_data, ajax_data_type) => {
        $.ajax({
            // 요청을 보낼 URL
            url: ajax_url,


            // HTTP 요청 방식 (GET, POST)
            type: ajax_type,


            // HTTP 요청과 함께 서버로 보낼 데이터
            data: ajax_data,


            // 서버에서 보내줄 데이터의 타입 ( xml, text, html, json 등)
            dataType: ajax_data_type,
        })
            .done(function (data) {
                console.log('Success : ' + data);
            })
            .fail((data) => {
                console.log('failed' + JSON.stringify(data));
            })
    };

    ajax('/api/game/async-search', 'GET', null, 'text');

}

function disconnect() {

    if (stompClient !== null) {
        stompClient.disconnect();
    }
    setConnected(false);
    console.log("Disconnected");
}

function sendName() {
    stompClient.send("/app/hello", {"gameStatue" : 1}, JSON.stringify({'name': $("#name").val()}));
}

function showGreeting(message) {
    console.log("message : " + message);
    $("#greetings").append("<tr><td>" + message + "</td></tr>");
}

$(function () {
    $("form").on('submit', function (e) {
        e.preventDefault();
    });
    $( "#connect" ).click(function() { connect(); });
    $( "#gamesearch" ).click(function() { gameSearch(); });
    $( "#disconnect" ).click(function() { disconnect(); });
    $( "#send" ).click(function() { sendName(); });
});

