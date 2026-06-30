import { apiRequest } from "@/lib/api";

export async function getSalaryList() {

  const response = await apiRequest(`/api/salaries`);

  if (!response.ok) {throw new Error("기본급 조회 실패");}

  return response.json();
}

export async function createSalary(request) {

  const response = await apiRequest(`/api/salaries`, {

    method: "POST",

    headers: {"Content-Type": "application/json",},

    body: JSON.stringify(request),

  });

  if (!response.ok) {throw new Error("기본급 등록 실패");}
}

export async function updateSalary(employeeId, request) {

  const response = await apiRequest(

    `/api/salaries/${employeeId}`,

    {
      method: "PUT",

      headers: {"Content-Type": "application/json",},

      body: JSON.stringify(request),
    }

  );

  if (!response.ok) {throw new Error("기본급 수정 실패");}
}