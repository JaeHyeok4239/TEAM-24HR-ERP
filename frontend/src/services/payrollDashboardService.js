import { apiRequest } from "@/lib/api";

export async function getMonthlyCost() {

  const response = await apiRequest(`/api/dashboard/monthly-cost`);

  if (!response.ok) throw new Error("월별 급여 통계 조회 실패");

  return response.json();
}

export async function getDepartmentCost() {

  const response = await apiRequest(`/api/dashboard/department-cost`);

  if (!response.ok) throw new Error("부서별 급여 통계 조회 실패");

  return response.json();
}