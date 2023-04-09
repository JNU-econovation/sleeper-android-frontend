# Sleeper
<img width="1000" alt="Thumbnail" src="https://user-images.githubusercontent.com/114472483/230789138-636143c5-a94a-4d6a-84db-742dee340d79.png">

## 서비스 소개
Sleeper는 불규칙한 수면 패턴을 가진 20대 대학생이 효율적이고 규칙적인 수면을 할 수 있도록 돕습니다. 규칙적인 수면은 신체적, 정신적으로 굉장히 중요하지만, 대학생들은 불규칙한 생활 패턴으로 매일 다른 시간에 수면을 취하거나, 권장 수면 시간보다 적게 수면을 취합니다. Sleeper는 규칙적인 수면을 통해 사용자에게 더 나은 내일을 살아갈 수 있는 원동력을 제공합니다.

사용자가 규칙적인 수면을 취했을 때 캐릭터가 성장해 규칙적인 수면을 유도합니다. 잠들기 전 하루 감사일기로 하루동안 좋았던 일을 생각하며 스트레스를 잊도록 합니다. 바빠서 잠을 못자는 사람들에게는 수면 사이클 시간 계산으로 적게 자더라도 효율적인 수면을 취할 수 있도록 돕습니다.


## 기술 스택
#### Backend
- Java 11
- Spring Boot 2.7.5
- Spring MVC
- JPA

#### AOS
- kotlin
- Android Studio
- Retrofit

#### InfraStructure
- H2 / MySQL
- PostMan

## 역할 분담
### Backend

**황대선**
- 유지 서버
- 수면 관리 서버
- 감사일기 서버
- 캐릭터 서버
- 돈 서버
- 알림 서버
- 상점 서버
- 아이템 서버
- 관리자 서버

**임수미**
- 로그인 서버

### AOS

**장현지**
- AOS App

| 메시지      | 설명 |
|:----------:|------|
| feat     | 기능 추가 작업에 대한 커밋 |
| update   | 기능 수정 작업에 대한 커밋 |
| fix      | 버그 코드 수정에 대한 커밋 |
| refactor | 코드 리팩토링 작업에 대한 커밋|
| docs     | 문서 작업에 대한 커밋 |

## 화면 및 기능 소개
### 1-1. 회원가입_로그인 정보 설정
<img width="150" alt="Register" src="https://user-images.githubusercontent.com/114472483/230788863-e53ecfac-46af-459c-8856-395462da76a6.png">

- 아이디를 중복 체크합니다.
- 비밀번호를 재확인합니다.
- 나이를 제외한 개인 정보는 받지 않습니다.
- 추후 수면 분석 알고리즘 도입 시 연령별 수면 패턴에 대한 데이터가 필요하기 때문에 이용자의 나이 정보를 수집합니다.

### 1-2. 회원가입_목표 수면 시간 설정
<img width="300" alt="SetGoal" src="https://user-images.githubusercontent.com/114472483/230788893-a3dd12cb-6bc9-405e-aa3f-83940c745e88.png">

- 회원 가입 시에 수면 시작, 종료 시간을 설정합니다.
- 설정한 목표 시간에 맞춰 규칙적으로 수면을 취하도록 유도합니다.


### 2. 로그인
<img width="150" alt="login" src="https://user-images.githubusercontent.com/114472483/230788822-b0ec085f-e9f7-4f02-ba8f-10ea3030e4af.png">

- 사용자가 가장 처음에 보는 화면입니다.

### 3. 홈화면
<img width="300" alt="KakaoTalk_Photo_2023-04-10-02-47-59" src="https://user-images.githubusercontent.com/114472483/230788389-d3e41382-ee6a-4804-b796-999c32c5b27b.png">

- 수면 시작 버튼을 눌러 감사 일기를 작성할 수 있습니다.
- 규칙적인 수면을 유지했을 때, 캐릭터가 성장합니다.
- 캐릭터 경험치를 지급하여 규칙적인 수면을 지키도록 유도합니다.

### 4. 감사 일기
<img width="150" alt="ThanksNote" src="https://user-images.githubusercontent.com/114472483/230788992-30ee8bec-0c56-4412-a1f1-1dc864368ea6.png">

- 잠에 들기 전 하루 동안 있었던 좋은 일을 기록합니다.
- 감사 일기 등록이 완료되면 수면 모드가 활성화됩니다.

### 5. 알람 설정 및 기상 시간 추천
<img width="300" alt="Alarm" src="https://user-images.githubusercontent.com/114472483/230789015-0140139b-07b8-4eda-9b67-195d657e5077.png">

- 목표로 설정한 수면 시간을 바탕으로 알람이 설정됩니다.
- 알람을 수정하는 경우, 오늘의 취침 시간을 바탕으로 기상 시간을 추천받을 수 있습니다.
- 1시간 30분 단위로 기상 시간을 추천해줍니다.

### 6. 수면 캘린더
<img width="300" alt="Calendar" src="https://user-images.githubusercontent.com/114472483/230789043-034fc2e4-9cec-4d6f-9a8a-9590547a5fa2.png">

- 날짜별로 설정 수면 시간, 목표 수면 시간, 수면 점수, 감사 일기 등의 내용을 확인할 수 있습니다.

## 에코노베이션 복덩이팀 발표영상

- [기획](https://youtu.be/wOqimi4O8H4?t=2123) - PM 이시현
- [백엔드](https://youtu.be/wOqimi4O8H4?t=2499) - 백엔드 개발자 황대선
