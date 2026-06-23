export const DETAIL_TABS = [
  { value: "basic", label: "기본정보" },
  { value: "hr", label: "인사정보" },
  { value: "history", label: "인사 이력" },
];

export const STATUS_LABELS = {
  ACTIVE: "재직",
  LEAVE: "휴직",
  RESIGNED: "퇴사",
  INACTIVE: "비활성",
  LOCKED: "잠김",
};

export const EMPLOYMENT_TYPE_LABELS = {
  REGULAR: "정규직",
  DAILY: "일용직",
};

export const ROLE_ITEMS = [
  {
    code: "USER",
    label: "일반 사용자",
    description: "시스템 기본 사용 권한",
  },
  {
    code: "ADMIN",
    label: "시스템 관리자",
    description: "전체 시스템 및 권한 관리",
  },
  {
    code: "HR_OPERATOR",
    label: "인사 실무자",
    description: "직원 등록 및 기본 인사정보 관리",
  },
  {
    code: "HR_MANAGER",
    label: "인사 관리자",
    description: "권한 부여, 퇴사 처리, 민감정보 관리",
  },
  {
    code: "ATTENDANCE_MANAGER",
    label: "근태 관리자",
    description: "근태, 출퇴근, 휴가 정보 관리",
  },
  {
    code: "PAYROLL_MANAGER",
    label: "급여 관리자",
    description: "급여 산정 및 급여 정보 관리",
  },
];

export const BANK_OPTIONS = [
  "국민은행",
  "신한은행",
  "하나은행",
  "우리은행",
  "농협은행",
  "기업은행",
  "카카오뱅크",
  "케이뱅크",
  "토스뱅크",
  "SC제일은행",
  "씨티은행",
  "새마을금고",
  "신협",
  "우체국",
  "수협은행",
  "부산은행",
  "대구은행",
  "광주은행",
  "전북은행",
  "경남은행",
  "제주은행",
];