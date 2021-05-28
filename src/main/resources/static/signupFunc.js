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

function fnCheckDuplicateNickname() {
    const nickname = document.querySelector("input[name=nickname]").value;
    fetch('/api/auth/nickname-check/'+nickname).then(function (response) {
        if (response.ok) {
            return response.json();
        }
        return Promise.reject(response);
    }).then(function (data) {
        console.log(data);
        // 닉네임 중복확인 성공
        if (data?.statusCode == 1) {
            alert(data?.message);
        } else { // 닉네임 중복확인 실패
            alert(data.message);
        }
    }).catch(function (error) {
        console.warn(error);
    });
}

function fnSignUp() {
    const nickname = document.querySelector("input[name=nickname]");
    fetch('/api/auth/nickname-check', {
        method: 'GET',
        body: new URLSearchParams({nickname}),
    }).then(function (response) {
        if (response.ok) {
            return response.json();
        }
        return Promise.reject(response);
    }).then(function (data) {
        console.log(data);
        // 닉네임 중복확인 성공
        if (data?.statusCode == 1) {
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
    form.addEventListener("submit",function(event){
        event.preventDefault();
        fetch('/api/auth/signup', {
            method: 'POST',
            body: new URLSearchParams(new FormData(event.target)),
        }).then(function (response) {
            if (response.ok) {
                return response.json();
            }
            return Promise.reject(response);
        }).then(function (data) {
            console.log(data);
            // 성공
            if (data?.statusCode == 200) {
                window.location = "localhost:8080/login"
            } else { // 실패
                alert(data.message);
            }
        }).catch(function (error) {
            console.warn(error);
        });
    });
}
//window의 load이벤트 발생 감시
window.addEventListener("load", init);