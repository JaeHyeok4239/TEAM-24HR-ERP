import { apiRequest } from "@/lib/api";

const HR_EMPLOYEE_API = "/api/hr/employees";

export const getHrEmployeeFormOptionsRequest = async () => {
  const response = await apiRequest(`${HR_EMPLOYEE_API}/form-options`);

  return response.json();
};

const createQueryString = (params) => {
  const searchParams = new URLSearchParams();

  Object.entries(params).forEach(([key, value]) => {
    if (value !== undefined && value !== null && value !== "") {
      searchParams.append(key, value);
    }
  });

  const queryString = searchParams.toString();

  return queryString ? `?${queryString}` : "";
};

export const getHrEmployeesRequest = async ({
  departmentId,
  status,
  employmentType,
  keyword,
} = {}) => {
  const queryString = createQueryString({
    departmentId,
    status,
    employmentType,
    keyword,
  });

  const response = await apiRequest(`${HR_EMPLOYEE_API}${queryString}`);

  return response.json();
};

export const getHrDepartmentTreeRequest = async () => {
  const response = await apiRequest(`${HR_EMPLOYEE_API}/departments/tree`);

  return response.json();
};

export const getHrEmployeeDetailRequest = async (employeeId) => {
  if (!employeeId) {
    throw new Error("직원 ID가 없습니다.");
  }

  const response = await apiRequest(`${HR_EMPLOYEE_API}/${employeeId}`);

  return response.json();
};

export const createHrEmployeeRequest = async ({
  loginId,
  password,
  name,
  email,
  departmentId,
  positionId,
  employmentType,
}) => {
  const requestBody = {
    loginId,
    password,
    name,
    email,
    employmentType,
  };

  if (employmentType === "REGULAR") {
    requestBody.departmentId = Number(departmentId);
    requestBody.positionId = Number(positionId);
  }

  const response = await apiRequest(HR_EMPLOYEE_API, {
    method: "POST",
    body: JSON.stringify(requestBody),
  });

  return response.json();
};