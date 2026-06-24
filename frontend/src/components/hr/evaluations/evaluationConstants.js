export const TABS = [
  { value: "manage", label: "평가운영관리" },
  { value: "results", label: "평가결과조회" },
  { value: "promotion", label: "진급심사대상자" },
];

export const PERIOD_STATUS_LABELS = {
  DRAFT: "준비중",
  OPEN: "진행중",
  CLOSED: "마감",
};

export const EVALUATION_STATUS_LABELS = {
  PENDING: "평가 전",
  SAVED: "임시저장",
  CONFIRMED: "확정",
};

export const HALF_TYPE_LABELS = {
  H1: "상반기",
  H2: "하반기",
};

export const INITIAL_PERIOD_FORM = {
  evaluationYear: new Date().getFullYear(),
  halfType: "H1",
  periodName: "",
  targetStartDate: "",
  targetEndDate: "",
  inputStartDate: "",
  inputEndDate: "",
};