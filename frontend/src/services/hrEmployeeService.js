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

export const updateHrEmployeeBasicInfoRequest = async (
  employeeId,
  { name, phone, email, zipcode, address, addressDetail }
) => {
  if (!employeeId) {
    throw new Error("직원 ID가 없습니다.");
  }

  const response = await apiRequest(
    `${HR_EMPLOYEE_API}/${employeeId}/basic-info`,
    {
      method: "PATCH",
      body: JSON.stringify({
        name,
        phone,
        email,
        zipcode,
        address,
        addressDetail,
      }),
    }
  );

  return response.json();
};

export const updateHrEmployeeEmploymentInfoRequest = async (
  employeeId,
  {
    departmentId,
    positionId,
    employmentType,
    status,
    hireDate,
    resignationDate,
    changeReason,
  }
) => {
  if (!employeeId) {
    throw new Error("직원 ID가 없습니다.");
  }

  const response = await apiRequest(
    `${HR_EMPLOYEE_API}/${employeeId}/employment-info`,
    {
      method: "PATCH",
      body: JSON.stringify({
        departmentId,
        positionId,
        employmentType,
        status,
        hireDate,
        resignationDate,
        changeReason,
      }),
    }
  );

  return response.json();
};

export const updateHrEmployeeRolesRequest = async (
  employeeId,
  { roleCodes, changeReason },
) => {
  if (!employeeId) {
    throw new Error("직원 ID가 없습니다.");
  }

  const response = await apiRequest(`${HR_EMPLOYEE_API}/${employeeId}/roles`, {
    method: "PATCH",
    body: JSON.stringify({
      roleCodes,
      changeReason,
    }),
  });

  return response.json();
};

export const findHrEmployeeSensitiveInfoRequest = async (employeeId) => {
  if (!employeeId) {
    throw new Error("직원 ID가 없습니다.");
  }

  const response = await apiRequest(
    `${HR_EMPLOYEE_API}/${employeeId}/sensitive-info`,
    {
      method: "GET",
    },
  );

  return response.json();
};

export const updateHrEmployeeSensitiveInfoRequest = async (
  employeeId,
  { bankName, accountNumber, accountHolder, rrn },
) => {
  if (!employeeId) {
    throw new Error("직원 ID가 없습니다.");
  }

  const response = await apiRequest(
    `${HR_EMPLOYEE_API}/${employeeId}/sensitive-info`,
    {
      method: "PATCH",
      body: JSON.stringify({
        bankName,
        accountNumber,
        accountHolder,
        rrn,
      }),
    },
  );

  return response.json();
};

export const findHrEmployeeHistoriesRequest = async (employeeId) => {
  if (!employeeId) {
    throw new Error("직원 ID가 없습니다.");
  }

  const response = await apiRequest(
    `${HR_EMPLOYEE_API}/${employeeId}/histories`,
    {
      method: "GET",
    },
  );

  return response.json();
};