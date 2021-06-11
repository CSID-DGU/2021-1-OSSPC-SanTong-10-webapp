function init(){
    var btnLogin = document.querySelector("#btnLogin");
    var form = document.querySelector("form");
    form.addEventListener("submit",function(event){
        event.preventDefault();

        var nickname = document.getElementById("loginId").value;
        var password = document.getElementById("loginPw").value;

        console.log("로그인 아이디, 패스워드: " + nickname + "/ " + password);

        //JSON 형식으로 요청
        var loginJson = new Object();

        loginJson.nickname = nickname;
        loginJson.password = password;

        fetch('/api/auth/login', {
            method: 'POST',
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(loginJson),
        }).then(res => res.json()
        ).then(function (data) {
            console.log("로그인 성공 : " + JSON.stringify(data));

            // 로그인 성공
            if (data.statusCode == 200) {
                window.location = "/game"
            } else { // 로그인 실패
                alert(data.message);
            }
        }).catch(function (error) {
            console.warn(error);
        });
    });
}

window.addEventListener("load", init);