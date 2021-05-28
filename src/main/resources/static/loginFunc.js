function init(){
    var btnLogin = document.querySelector("#btnLogin");
    var form = document.querySelector("form");
    form.addEventListener("submit",function(event){
        event.preventDefault();
        fetch('/api/auth/login', {
            method: 'POST',
            body: new URLSearchParams(new FormData(event.target)),
        }).then(function (response) {
            if (response.ok) {
                return response.json();
            }
            return Promise.reject(response);
        }).then(function (data) {
            console.log(data);
            // 로그인 성공
            if (data?.statusCode == 200) {
                window.location = "localhost:8080/index"
            } else { // 로그인 실패
                alert(data.message);
            }
        }).catch(function (error) {
            console.warn(error);
        });
    });
}
//window의 load이벤트 발생 감시
window.addEventListener("load", init);