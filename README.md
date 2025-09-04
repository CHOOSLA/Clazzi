# Clazzi

Jetpack Compose 기반으로 만든 학습용 안드로이드 앱입니다. Firebase와 연동하여 실시간 투표와 채팅 기능을 구현하면서 Compose, Navigation, ViewModel 등의 기본 개념을 연습할 수 있습니다.

## 주요 기능
- **사용자 인증**: Firebase Authentication을 이용한 이메일 회원가입/로그인
- **투표**: 투표 목록 조회, 이미지와 마감일을 포함한 투표 생성, 참여, 결과 공유
- **채팅**: 다른 사용자와 실시간으로 메시지 주고받기
- **마이페이지**: 닉네임 확인 및 로그아웃

## 폴더 구조
```
app/src/main/java/com/example/clazzi
 ├─ model/        # 데이터 모델 (Vote, Message 등)
 ├─ repository/   # Firebase 및 REST API 연동
 ├─ ui/
 │   ├─ components/  # 권한 요청, 이미지/카메라 선택 컴포넌트
 │   └─ screens/     # Auth, VoteList, Vote, CreateVote, Chat 등 화면
 ├─ util/         # 날짜 포맷 등 유틸 함수
 └─ viewmodel/    # ViewModel과 Factory 클래스
```

## 사용 기술
- Kotlin, Jetpack Compose, Material3, Navigation Compose
- Firebase Authentication, Cloud Firestore, Firebase Storage
- Coil을 이용한 이미지 로딩
- Retrofit 기반 REST API 예제 (미구현)

## 실행 방법
1. Android Studio에서 저장소를 열고 `google-services.json`을 `app/` 폴더에 추가합니다.
2. Firebase 프로젝트를 설정하고 Authentication, Firestore, Storage를 활성화합니다.
3. 에뮬레이터나 실제 기기에서 앱을 실행합니다.

## 학습 포인트
- Compose를 이용한 화면 구성과 상태 관리
- Firebase와 연동한 실시간 데이터 처리
- 단순한 MVVM 구조(ViewModel + Repository)
- 권한 요청 및 이미지/카메라 활용
