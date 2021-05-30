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

/*
function fnSignUp() {
    const nickname = document.querySelector("input[name=nickname]");
    //JSON 형식으로 요청
    var nickJson = new Object();
    nickJson.nickname = nickname;

    fetch('/api/auth/nickname-check', {
        method: 'GET',
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify(nickJson),
    })

    .then(res => res.json())
    .then(function (data) {
        console.log(data);
        // 닉네임 중복확인 성공
        if (data?.statusCode == 1) {
            alert(data?.message);
            // alert("사용가능한 아이디입니다.");
        } else { // 닉네임 중복확인 실패 - 이미 사용중인 아이디
            alert(data.message);
            // alert("이미 사용 중인 아이디입니다.")

        }
    }).catch(function (error) {
        console.warn(error);
    });
}
*/

function init(){
    var form = document.querySelector("#signup");
    var nickname = document.getElementById("loginId").value;
    var password = document.getElementById("loginPw").value;
    var level = document.querySelector("input[name=level]:checked").value;
    var passwordConfirm = document.getElementById("loginPwCheck").value;

    //JSON 요청
    var signupJson = new Object();
    signupJson.nickname = nickname;
    //signupJson.user_id = nickname; 확인 후 수정
    signupJson.password = password;
    signupJson.level = level;
//
    form.addEventListener("submit",function(event){
        event.preventDefault();

        // 비밀번호 입력값과 비밀번호 확인값을 비교하는 로직
        if (!(!password && !passwordConfirm && password === passwordConfirm)) {
            alert("비밀번호와 비밀번호 확인 값이 다릅니다. 다시 확인해 주세요.");
            return;
        }

        var checkYn = document.querySelector("#checkYN").value;
        //중복확인 여부 체크
        if (checkYn != "Y") {
            alert("ID 중복 체크를 하셔야 합니다.");
            return;
        } else {
            document.querySelector("#checkYN").value = "N";
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
//window의 load이벤트 발생 감시
window.addEventListener("load", init);
