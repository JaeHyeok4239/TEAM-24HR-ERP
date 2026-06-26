const BASE_URL = "http://localhost:8080";

export async function getPayrolls({
  month,
  employeeNo,
  departmentId,
  page = 0,
  size = 10,
}) {
  const params = new URLSearchParams();

  if (month) params.append("month", month);
  if (employeeNo) params.append("employeeNo", employeeNo);
  if (departmentId) params.append("departmentId", departmentId);

  params.append("page", page);
  params.append("size", size);

  const response = await fetch(
    `${BASE_URL}/api/payrolls?${params.toString()}`
  );

  if (!response.ok) {
    throw new Error("급여 목록 조회 실패");
  }

  return response.json();
}


export async function getPayrollDetail(payrollId) {
  const response = await fetch(
    `http://localhost:8080/api/payrolls/${payrollId}`
  );

  if (!response.ok) {
    throw new Error("급여 상세 조회 실패");
  }

  return response.json();
}