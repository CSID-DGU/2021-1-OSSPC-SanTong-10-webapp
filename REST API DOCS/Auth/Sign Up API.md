# 회원가입 APIs

### **|** 회원가입 진행 과정에서 아래 두 API가 있습니다. 

회원가입 시, 닉네임 또는 패스워드 관련 정규식 체크 후 서버에 요청해주시면 됩니다. (정규식은 임의로 설정해도 무방) 먼저 프론트 측에서 정규식으로 입력 값에 대한 체크를 하고 추후에 서버에서도 막을 필요가 있는 경우 추가할 예정입니다. 

**1) 닉네임 중복체크 API**

**2) 회원가입 요청 API** 

___

### 닉네임 중복체크 API 

**URL** : `/api/auth/nickname-check` 

**Method** : `GET`

**Authentication** : 인증 필요 X 

**Request Body** : 

```json
{
    "nickname" : "(고유) 닉네임 값"
}
```

**Respose** :

```json
// 닉네임 중복 체크 결과에 대해서 유저에게 안내 해주시면 됩니다. 
// 예) 중복체크 성공 또는 실패 

[HTTP/1.1 200]
{
    "statusCode": 1,
    "message": "요청 닉네임으로 회원가입이 가능한 경우",
    "object": null
}
 
[HTTP/1.1 200]
{
    "statusCode": 2,
    "message": "요청 닉네임으로 회원가입이 불가능한 경우",
    "object": null
}
```



___

### 회원가입 요청 API 

**URL** : `/api/auth/signup` 

**Method** : `POST`

**Authentication** : 인증 필요 X 

**Request Body** : 

```json
{
    "nickname" : "(고유) 닉네임 값",
    "password" : "패스워드 값"
 }
```

**Respose** :

**1) 회원가입 성공 ** :

회원가입 성공 메시지를 응답 받은 경우, 로그인 페이지로 이동시키면 됩니다. 

```json
[HTTP/1.1 200]

{
    "statusCode": 200,
    "message": "회원가입 성공",
    "object": null
}
```

