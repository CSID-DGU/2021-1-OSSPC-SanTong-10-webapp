# Game Searching API (via Async)

### | 게임 매칭 요청 API (비동기) 

게임 매칭 요청은 서버 측에서 비동기 방식으로 처리합니다. 게임 찾기 요청 이후, 클라이언트 측은 Non-Blocking 상태로 있고, 서버 측은 매칭 성공 또는 실패 시점에 응답을 합니다. 따라서 클라이언트 측에서 게임 찾기 요청을 보낸 이후, 응답 받기 전 게임 찾기 취소 요청을 할 수 있습니다.

**URL** : `/api/game/async-search`

**Method** : `POST`

**Authentication** : O (로그인 상태에서만 요청할 수 있습니다.)

**Request Body** : X 

**Respose** : 

