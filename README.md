# 2021-1 OSSP 팀 산통깨지마 



### | 팀 구성원 : 

- 정용희
- 윤태권
- 신유은

---

### | 프로젝트 소개 : 

온라인 오목 게임과 복기 서비스를 제공합니다. 온라인으로 상대 유저와 1:1 오목 대국을 할 수 있고, 복기 서비스에서 활용한 모델은  `Neural Network` 를 기반으로 약 4만 게임 이상의 데이터로 흑 또는 백돌 각각의 입장에서 승리하는 패턴을 학습한 모델입니다. 유저는 복기 서비스에서 이 모델을 통해 추천 받은 위치 좌표를 통해 오목 게임 실력 향상을 도모할 수 있습니다. 

---

### | 주요 기능 :

1. 회원 관리 기능 : 
   - 회원 가입 / 로그인 
2. PVP 온라인 오목 게임  
   - 각 턴별로 30초 시간 제한 기능
   - 시간 제한 초과 시 오목 룰(렌주 룰) 기준, 착수 가능한 랜덤 위치에 자동 착수 기능 
   - 오목 렌주를 기반 금수 위치 식별 기능 
3. 진행한 게임에 대한 AI 기반 복기 서비스 

___

### | 개발 환경 :

- IDE : `IntelliJ`, `Pycharm`
- 언어 : `Java`, `Javascript`, `Python`
- 웹 프레임 워크 : `SpringBoot`, `Django` 
- 데이터베이스 : `MySQL`
- 웹서버 : `Nginx` 
- 운영체제 : `Ubuntu` 

---

### | 소스 코드 관리 (via Git Branch Strategy) 

![image-20210616090018477](/Users/youn/Library/Application Support/typora-user-images/image-20210616090018477.png)

`Git flow` 브랜치 전략을 일부 차용해서 소스 코드 관리에 활용했습니다. 자세한 깃 활용에 대한 설명은 다음 https://github.com/CSID-DGU/2021-1-OSSPC-SanTong-10/blob/main/TK/Git_Branch%20Strategy.md 링크에서 확인할 수 있습니다.

### | RESTful API 문서 정리 

API 서버 구축 시, 서버와 클라이언트 간 소통을 위해 문서를 적극 활용했습니다. (https://github.com/CSID-DGU/2021-1-OSSPC-SanTong-10-webapp/tree/develop/REST_APO_DOCS )

