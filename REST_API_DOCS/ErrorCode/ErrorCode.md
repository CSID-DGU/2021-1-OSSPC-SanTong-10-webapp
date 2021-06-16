# ErrorCode 

각 에러 상황에 대해서 공통적인 예외 처리를 위해 에러 코드를 아래와 같이 정의합니다. 각 페이지, 또는 API 요청에 대한 반환 오브젝트 중 **`"code"`** 값이   각 고유의 에러 코드입니다. 

```java
// 1. 인증 관련 에러 코드
BADCREDENTIALS_EXCEPTION(401, "AUTH001", "가입하지 않은 아이디이거나, 잘못된 비밀번호입니다.")
USERNAMENOTFOUND_EXCEPTION(401, "AUTH002", "가입하지 않은 아이디이거나, 잘못된 비밀번호입니다.")
UNAUTHORIZED_EXCEPTION(401, "AUTH003", "인증이 요구되는 요청 대상, 세션 만료")
// 2. 게임 관련 에러 코드
NOTFOUNDUSER_EXCEPTION(404, "GAME001", "요청한 유저를 매칭 대기열에서 찾을 수 없습니다.")
```

