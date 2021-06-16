# Game Searching API (via Async)

### | 게임 매칭 요청 API (비동기) 

게임 매칭 요청은 서버 측에서 비동기 방식으로 처리합니다. 게임 찾기 요청 이후, 클라이언트 측은 Non-Blocking 상태로 있고, 서버 측은 매칭 성공 또는 실패 시점에 응답을 합니다. 따라서 클라이언트 측에서 게임 찾기 요청을 보낸 이후, 응답 받기 전 게임 찾기 취소 요청을 할 수 있습니다.

예외 처리를 제외하고, 게임 매칭에 대한 응답은 총 세 가지가 있습니다. `SUCCESS`, `CANCEL`, `TIMEOUT` 각각의 응답 상황에 따라서 어떤 처리 로직으로 전개해야 하는 지는 아래에서 자세히 설명하겠습니다. 

**URL** : `/api/game/async-search`

**Method** : `POST`

**Authentication** : O (로그인 전제) 

**Request Body** : X 

**Respose** : 

**1. SUCCESS, 게임 매칭 성공한 경우 : ** 

```json
[HTTP/1.1 200]
{
    "gameMatchingResult": "SUCCESS",
    "gameSubDestination": "/topic/11", // WebSocket STOMP SUB 경로 (인게임 데이터 주고 받는 경로 값)
    "loginUser": {
        "nickname": "tk",
        "turn": true,  // true : 흑돌, false : 백돌
        "gameLevel": 2 // 오목 게임 수준 (0 : 초보, 1 : 중수, 2 : 고수)
    },
    "opponentUser": {
        "nickname": "username",
        "turn": false, // true : 흑돌, false : 백돌
        "gameLevel": 2 // 오목 게임 수준 (0 : 초보, 1 : 중수, 2 : 고수)
    }
}
```



### | 게임 매칭 요청 취소 또는 타임 아웃인 경우 공통 처리 : 

`게임 찾기 중` 에서 `게임 찾기` 버튼 상태로 전환.  



**2. CANCEL, (로그인) 유저가 게임 매칭 취소 요청을 한 경우 : ** 

```json
[HTTP/1.1 200]
{
    "gameMatchingResult": "CANCEL",
    "gameSubDestination": null,
    "loginUser": null,
    "opponentUser": null
}
```

**3. TIMEOUT, 서버 측에서 지정한 처리 시간을 초과한 경우(게임 매칭 실패) : ** 

```json
[HTTP/1.1 200]
{
    "gameMatchingResult": "TIMEOUT",
    "gameSubDestination": null,
    "loginUser": null,
    "opponentUser": null
}
```

