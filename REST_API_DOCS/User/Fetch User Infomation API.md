# 회원정보 조회 API

### | 메인 페이지 반환 받은 경우 로그인 여부 체크를 하기 위해 User Info API를 요청합니다.



**URL** : `/api/user/profile` 

**Method** : `GET`

**Authentication** : 선택 (인증 상태로 요청하는 경우, 유저의 프로필 정보를 반환)

**Request Body** : X

**Respose** : 

**1) 비로그인 상태 :  메인 페이지에 비로그인 상태로 접근**

```json
[HTTP/1.1 200]
{
    "statusCode": 1,
    "message": "비로그인 상태",
    "object": null
}
```



**2) 로그인 상태 :  메인 페이지에 로로그인 상태로 접근, 반환 받은 프로필 정보(닉네임) 활용**

```json
[HTTP/1.1 200]
{
    "statusCode": 2,
    "message": "회원정보 조회 성공 (로그인 상태)",
    "object": {
        "nickname": "테스트 닉네임"
    }
}
```

