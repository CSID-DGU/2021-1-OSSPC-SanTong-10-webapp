// async function cancel(){
//     document.getElementById("findGame").style.display = ""; // '게임 찾기' 버튼 보이기
//     document.getElementById("findingGame").style.display = "none";     // '게임찾는 중' 버튼 숨기기
//     document.getElementById("cancel").style.display = "none";    // '취소' 버튼 숨기기
//     document.getElementById("gameIng").style.display = "none";       // '게임진행 중' 버튼 숨기기
//
//     await fetch('/api/game/cancel-search', {
//         method: 'POST',
//         headers: {
//             "Content-Type": "application/json"
//         },
//         body: '',
//     }).then(res => res.json()
//     ).then(function (data) {
//         console.log("취소 성공 " + JSON.stringify(data));
//
//         //  성공
//         // if (data.statusCode == 200) {
//
//         // }else{//실패
//         // }
//     }).catch(function (error) {
//         console.warn(error);
//     });
//
// }
//
// [게임 매칭 API]
// var userJson =  new Object(); //현재 로그인 정보
// var loginJson = new Object(); //현재 매칭 로그인 정보
// var opponentJson = new Object(); //매칭 대상자 정보
//
// /*
//    '게임찾기' 버튼 클릭 시,
// */
// function findGame(){
//     if(userJson.nickname){
//         document.getElementById("findGame").style.display  = "none";
//         document.getElementById("findingGame").style.display = "";     // '게임찾는 중' 버튼
//         document.getElementById("cancel").style.display    = "";     // '취소' 버튼
//
//
//         fetch('/api/game/async-search', {
//             method: 'POST',
//             headers: {
//                 "Content-Type": "application/json"
//             },
//             body: '',
//         }).then(res => res.json()
//         ).then(function (data) {
//             console.log("매칭 성공 : " + JSON.stringify(data));
//
//             // 로그인 성공
//             if (data.gameMatchingResult == "SUCCESS") {
//                 loginJson = data.loginUser
//                 opponentJson = data.opponentUser
//
//                 /* UI
//                 if(loginJson.turn === true){//흑돌
//                    document.getElementById("loginInfo").style.backgroundColor = "black"
//                    document.getElementById("loginInfo").style.background.fontcolor = "white"
//                 }else{//백돌
//                    document.getElementById("loginInfo").style.backgroundColor = "white"
//                    document.getElementById("loginInfo").style.background.fontcolor = "black"
//                 }
//
//                 if(opponentJson.turn === true){//흑돌
//                    document.getElementById("oppentInfo").style.backgroundColor = "black"
//                    document.getElementById("loginInfo").style.background.fontcolor = "white"
//                 }else{//백돌
//                    document.getElementById("oppentInfo").style.backgroundColor = "white"
//                    document.getElementById("loginInfo").style.background.fontcolor = "black"
//                 }
//                 */
//
//                 document.getElementById("loginInfo").style.display = "";
//                 document.getElementById("oppentInfo").style.display    = "";
//                 document.getElementById("loginInfo").innerText = loginJson.nickname  //아이디 세팅
//                 document.getElementById("oppentInfo").innerText = opponentJson.nickname  //아이디 세팅
//             }else{//게임 매칭 요청 취소 또는 타임 아웃인 경우 공통처리
//                 document.getElementById("findGame").style.display = "";
//                 document.getElementById("findingGame").style.display = "none";
//                 document.getElementById("cancel").style.display = "none";
//                 document.getElementById("gameIng").style.display = "none";
//             }
//         }).catch(function (error) {
//             console.warn(error);
//         });
//
//     }else{               //비로그인 상태
//         alert("로그인 후 이용 가능합니다.")   //alert창
//         window.location = "/login"      //로그인 페이지로 이동
//     }
// }
//
// [메인페이지 로드 시 회원정보 조회 API]
// body onload="getUser()"
//
//     <script>
//     function getUser(){
//
//
//         fetch('/api/auth/profile', {
//             method: 'GET',
//             headers: {
//                 "Content-Type": "application/json"
//             },
//             body: '',
//         }).then(res => res.json()
//         ).then(function (data) {
//             console.log("유저 조회 성공 : " + JSON.stringify(data));
//
//             // 유저 조회 성공
//             if (data.statusCode == 2) {
//                 userJson.nickname = data.object.nickname;
//             } else { // 로그인 실패
//                 alert(data.message);
//             }
//         }).catch(function (error) {
//             console.warn(error);
//         });
//     }
// </script>
//
