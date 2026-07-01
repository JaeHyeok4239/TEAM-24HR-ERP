import { apiRequest } from "@/lib/api";

export const getMonthlyAttendanceStats = async (yearMonth) => {
  const response = await apiRequest(`/api/attendance/monthly/summary?yearMonth=${yearMonth}`, {
    method: "GET",
  });

  return response.json();
};

// HrEmployeeService.js의 getEmployeesRequest에 date만 추가(이름도 수정)
// 기존 거에 붙이면 오류날까봐 혹시 몰라 따로 뺐습니다
export const getEmployeesFilterRequest = async ({
  departmentId,
  status,
  employmentType,
  keyword,
  date,
} = {}) => {
  const queryString = createQueryString({
    departmentId,
    status,
    employmentType,
    keyword,
    date,
  });

  const response = await apiRequest(`${HR_EMPLOYEE_API}${queryString}`);

  return response.json();
};