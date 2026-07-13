# 24HR

건설사 특화 HRM ERP 그룹웨어

내근직과 현장직의 근무 방식이 서로 다른 건설업 특성에 맞춰, 인사·근태·급여·전자결재·업무관리를 하나의 시스템으로 통합한 사내 그룹웨어입니다.

## 주요 기능

- **인사관리**: 직원 정보/부서/직급 관리, 인사이력, 인사평가 및 진급후보 관리
- **근태관리**: 정규직/일용직 출퇴근 기록(GPS 위치 인증), 근태 정정, 관리자 근태 현황
- **급여관리**: 급여 계산, 급여명세 조회, 부서별/월별 인건비 대시보드
- **전자결재**: 문서 유형별 결재선, 다단계 승인, 결재 위임
- **업무관리**: 일정관리(개인/부서/전사), 회의실 예약, 실시간 알림(WebSocket)
- **관리자 대시보드**: 인사 입/퇴사 현황, 인건비 통계

## 기술 스택

### Backend
- Java 17, Spring Boot 3.5.14
- Spring Security, JWT
- Spring Data JPA, Oracle 21c
- Redis (인증 토큰 저장)
- WebSocket(STOMP) — 실시간 알림

### Frontend
- Next.js 16, React 19
- Zustand, Tailwind CSS
- FullCalendar

### Infra
- Docker, Kubernetes(k3s)
- GitHub Actions (CI/CD)

## 아키텍처

![24HR ERD](docs/24HR%20ERD.png)

## 로컬 실행

```bash
# 1. 인프라(Oracle, Redis) 로컬 실행
docker compose -f infra/docker-compose.yml up -d

# 2. 백엔드 (기본은 사무실 공용 개발 DB에 연결됨 - 로컬 DB로 쓰려면
#    DB_URL / REDIS_HOST 환경변수를 localhost로 오버라이드)
cd backend
./gradlew bootRun

# 3. 프론트엔드
cd frontend
npm install
npm run dev
```

환경변수 항목은 `.env.sample`을 참고하세요.

## 브랜치 전략

[docs/branch-strategy.md](docs/branch-strategy.md) 참고

## Team

- 장재혁
- 김석률
- 이다인
- 김준호
- 한수빈
