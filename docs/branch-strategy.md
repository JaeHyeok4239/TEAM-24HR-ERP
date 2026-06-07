# Git 브랜치 전략 및 커밋 메시지 규칙

## 브랜치 전략

| 브랜치     | 설명        |
| ------- | --------- |
| main    | 최종 배포 브랜치 |
| develop | 개발 통합 브랜치 |
| feature | 기능 개발 브랜치 |

### feature 브랜치 예시

```text
feature/login
feature/auth
feature/user
feature/hr
feature/attendance
feature/payroll
```

---

## develop 브랜치를 사용하는 이유

main 브랜치는 항상 배포 가능한 안정적인 상태를 유지해야 한다.

만약 feature 브랜치에서 개발한 기능을 바로 main 브랜치에 병합하면,
테스트가 완료되지 않은 기능이 main에 포함될 수 있으며,
여러 기능이 동시에 개발되는 경우 충돌이나 오류가 발생할 위험이 높아진다.

따라서 develop 브랜치를 중간 통합 브랜치로 사용한다.

각 개발자는 feature 브랜치에서 기능을 개발하고,
완료된 기능은 develop 브랜치에 병합한다.

develop 브랜치에서 통합 테스트를 진행한 후,
안정성이 확인되면 main 브랜치에 병합한다.

---

## 브랜치 흐름

```text
feature/login
feature/hr
feature/payroll
        ↓
      develop
        ↓
       main
```

---

## 브랜치 규칙

* 기능 단위로 feature 브랜치 생성
* develop 브랜치를 기준으로 작업
* 작업 완료 후 Pull Request 생성
* 직접 main 브랜치 Push 금지
* 코드 리뷰 후 Merge 진행

---

## 협업 규칙

* 작업 시작 전 이슈 공유
* 작업 완료 후 일정관리 상태 업데이트
* 회의 후 회의록 작성
* API 변경 시 API 명세 수정
* develop Merge 전 코드 확인 진행

---

# Commit Message Convention

## 형식

```text
<type>: <작업 내용>
```

### 예시

```text
feat: 로그인 기능 구현
feat: 사용자 등록 API 추가

fix: 로그인 예외 처리 수정
fix: 사용자 조회 오류 수정

docs: 브랜치 전략 문서 작성
docs: API 명세 수정

refactor: 사용자 서비스 로직 분리

chore: .gitignore 수정
chore: Oracle 의존성 추가
```

---

## Commit Type

| 키워드      | 설명                   |
| -------- | -------------------- |
| feat     | 새로운 기능 추가            |
| fix      | 버그 수정                |
| docs     | 문서 수정                |
| style    | 코드 스타일 변경 (기능 변경 없음) |
| design   | UI 디자인 변경            |
| test     | 테스트 코드 추가 및 수정       |
| refactor | 코드 리팩토링              |
| build    | 빌드 관련 파일 수정          |
| ci       | CI 설정 파일 수정          |
| perf     | 성능 개선                |
| chore    | 기타 설정 변경, 패키지 수정     |
| rename   | 파일 또는 폴더명 변경         |
| remove   | 파일 삭제                |
| revert   | 이전 커밋 되돌리기           |

---

# Pull Request 작성 규칙

## 제목 예시

```text
feat: 사용자 등록 기능 구현
```

## 내용 예시

```text
## 변경사항

- 사용자 등록 API 추가
- 사용자 등록 화면 추가
- 사용자 권한 매핑 기능 추가

## 주요 수정 파일

- UserController.java
- UserService.java
- UserRepository.java
- user-register/page.js

## 테스트 완료

- 사용자 등록 성공 확인
- 필수값 검증 확인
- 중복 로그인 ID 검증 확인
```
