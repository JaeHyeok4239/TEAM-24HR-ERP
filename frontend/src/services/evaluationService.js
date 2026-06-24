import { apiRequest } from "@/lib/api";

const EVALUATION_API = "/api/hr/evaluations";

export const getEvaluationPeriodsRequest = async () => {
  const response = await apiRequest(`${EVALUATION_API}/periods`);
  return response.json();
};

export const createEvaluationPeriodRequest = async ({
  evaluationYear,
  halfType,
  periodName,
  targetStartDate,
  targetEndDate,
  inputStartDate,
  inputEndDate,
}) => {
  const response = await apiRequest(`${EVALUATION_API}/periods`, {
    method: "POST",
    body: JSON.stringify({
      evaluationYear: Number(evaluationYear),
      halfType,
      periodName,
      targetStartDate,
      targetEndDate,
      inputStartDate,
      inputEndDate,
    }),
  });

  return response.json();
};

export const openEvaluationPeriodRequest = async (periodId) => {
  if (!periodId) {
    throw new Error("평가기간 ID가 없습니다.");
  }

  const response = await apiRequest(`${EVALUATION_API}/periods/${periodId}/open`, {
    method: "PATCH",
  });

  return response.json();
};

export const closeEvaluationPeriodRequest = async (periodId) => {
  if (!periodId) {
    throw new Error("평가기간 ID가 없습니다.");
  }

  const response = await apiRequest(`${EVALUATION_API}/periods/${periodId}/close`, {
    method: "PATCH",
  });

  return response.json();
};

export const createEvaluationTargetsRequest = async (periodId) => {
  if (!periodId) {
    throw new Error("평가기간 ID가 없습니다.");
  }

  const response = await apiRequest(`${EVALUATION_API}/periods/${periodId}/targets`, {
    method: "POST",
  });

  return response.json();
};

export const getEvaluationTargetsRequest = async (periodId) => {
  if (!periodId) {
    throw new Error("평가기간 ID가 없습니다.");
  }

  const response = await apiRequest(`${EVALUATION_API}/periods/${periodId}/targets`);

  return response.json();
};

export const getEmployeeEvaluationDetailRequest = async (employeeEvaluationId) => {
  if (!employeeEvaluationId) {
    throw new Error("직원 평가 ID가 없습니다.");
  }

  const response = await apiRequest(`${EVALUATION_API}/${employeeEvaluationId}`);

  return response.json();
};

export const saveEmployeeEvaluationRequest = async (
  employeeEvaluationId,
  { answers, comment },
) => {
  if (!employeeEvaluationId) {
    throw new Error("직원 평가 ID가 없습니다.");
  }

  const response = await apiRequest(`${EVALUATION_API}/${employeeEvaluationId}/save`, {
    method: "PATCH",
    body: JSON.stringify({
      answers,
      comment,
    }),
  });

  return response.json();
};

export const confirmEmployeeEvaluationRequest = async (
  employeeEvaluationId,
  { answers, comment },
) => {
  if (!employeeEvaluationId) {
    throw new Error("직원 평가 ID가 없습니다.");
  }

  const response = await apiRequest(
    `${EVALUATION_API}/${employeeEvaluationId}/confirm`,
    {
      method: "PATCH",
      body: JSON.stringify({
        answers,
        comment,
      }),
    },
  );

  return response.json();
};

export const getEvaluationResultsRequest = async () => {
  const response = await apiRequest(`${EVALUATION_API}/results`);
  return response.json();
};

export const getEmployeeEvaluationHistoriesRequest = async (employeeId) => {
  if (!employeeId) {
    throw new Error("직원 ID가 없습니다.");
  }

  const response = await apiRequest(`${EVALUATION_API}/results/${employeeId}`);

  return response.json();
};

export const getPromotionCandidatesRequest = async () => {
  const response = await apiRequest(`${EVALUATION_API}/promotion-candidates`);
  return response.json();
};