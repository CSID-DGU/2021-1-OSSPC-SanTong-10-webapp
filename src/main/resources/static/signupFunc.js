function invalidCheck(){
    //dom트리에서 name속성값이 nickname인 객체찾기
    var inputId = document.querySelector("input[name=nickname]");
    //dom트리에서 name속성값이 password인 객체찾기
    var inputPwd = document.querySelector("input[name=password]");

    if(inputId.value.length > 10){
        alert("ID는 최대10자리까지만 가능합니다.");
        inputId.focus();
        return false;
    }

    var rePwd1 = /\W/; //특수문자용 정규식
    var rePwd2 = /\w/; //문자와숫자,_용 정규식
    //정규식 확인 메서드 : test()
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