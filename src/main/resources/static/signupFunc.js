function invalidCheck(){
    var inputId = document.querySelector("input[name=nickname]");
    var inputPwd = document.querySelector("input[name=password]");

    if(inputId.value.length > 10){
        alert("ID는 최대10자리까지만 가능합니다.");
        inputId.focus();
        return false;
    }

    var rePwd1 = /[^가-힣ㄱ-ㅎㅏ-ㅣa-zA-Z0-9]/; //특수문자용 정규식
    var rePwd2 = /[가-힣ㄱ-ㅎㅏ-ㅣa-zA-Z0-9]/; //문자와숫자,_용 정규식
    if(!rePwd1.test(inputPwd.value)){
        alert("비밀번호는 특수문자를 포함하세요.");
        inputPwd.focus();
        return false;
    }
    if(!rePwd2.test(inputPwd.value)){
        alert("영문 또는 숫자를 포함하세요.")
        inputPwd.focus();
        return false;
    }
    return true;
}

//ID 중복확인
function fnCheckDuplicateNickname() {
    const nickname = document.querySelector("input[name=nickname]").value;
    const data = { nickname: nickname };

    fetch('/api/auth/nickname-check', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(data),
    })

        .then(res => res.json())
        .then(function (data) {
            console.log(data);
            // 닉네임 중복확인 성공
            if (data?.statusCode == 1) {
                //중복확인 체크 여부
                document.querySelector("#checkYN").value = "Y";
                alert(data?.message);
            } else { // 닉네임 중복확인 실패
                alert(data.message);
            }
        }).catch(function (error) {
        console.warn(error);
    });
}

function init(){
    var form = document.querySelector("#signup");

    document.getElementById("loginId").addEventListener("keyup", function(e){
        const checkYn = document.querySelector("#checkYN").value;
        if (checkYn === "Y") {
            document.querySelector("#checkYN").value = "N";
        }
    });
    form.addEventListener("submit",function(event){
        event.preventDefault();

        var nickname = document.getElementById("loginId").value;
        var password = document.getElementById("loginPw").value;
        var level = document.querySelector("input[name=level]:checked").value;

        //JSON 요청
        var signupJson = new Object();
        signupJson.nickname = nickname;
        signupJson.password = password;
        signupJson.level = level;

        //중복확인 여부 체크
        var checkYn = document.querySelector("#checkYN").value;

        if (checkYn != "Y") {
            alert("ID 중복 체크를 하셔야 합니다.");
            return;
        }
        //else {
        //    document.querySelector("#checkYN").value = "N";
        //}

        //유효성 체크
        if (!invalidCheck()) {
            return;
        }

        fetch('/api/auth/signup', {
            method: 'POST',
            headers: {
                "Content-Type" : "application/json"
            },
            body: JSON.stringify(signupJson),
        })

            .then(res => res.json())
            .then(function (data) {
                console.log(data);
                // 성공
                if (data?.statusCode == 200) {
                    alert("가입되었습니다.");
                    window.location = "/login"
                } else { // 실패
                    alert(data.message);
                }
            }).catch(function (error) {
            console.warn(error);
        });
    });
}

window.addEventListener("load", init);