function init(){
    var btnLogin = document.querySelector("#btnLogin");
    var form = document.querySelector("form");
    form.addEventListener("submit",function(){
        var requestURL = "/api/auth/login";
        this.action = requestURL;
        this.method='post';
        this.submit();
    });
}
//window의 load이벤트 발생 감시
window.addEventListener("load", init);