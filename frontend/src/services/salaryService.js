const BASE_URL = "http://localhost:8080";

export async function getSalaryList() {

  const response = await fetch(`${BASE_URL}/api/salaries`);

  if (!response.ok) {throw new Error("기본급 조회 실패");}

  return response.json();
}

export async function createSalary(request) {

  const response = await fetch(`${BASE_URL}/api/salaries`, {

    method: "POST",

    headers: {"Content-Type": "application/json",},

    body: JSON.stringify(request),

  });

  if (!response.ok) {throw new Error("기본급 등록 실패");}
}

export async function updateSalary(salaryId, request) {

  const response = await fetch(

    `${BASE_URL}/api/salaries/${salaryId}`,

    {
      method: "PUT",

      headers: {"Content-Type": "application/json",},

      body: JSON.stringify(request),
    }

  );

  if (!response.ok) {throw new Error("기본급 수정 실패");}
}