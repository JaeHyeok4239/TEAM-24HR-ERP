import { apiRequest } from "@/lib/api";

export const getMonthlyAttendanceStats = async (yearMonth, employeeId) => {
  const response = await apiRequest(`/api/attendance/monthly/summary?yearMonth=${yearMonth}&targetEmployeeId=${employeeId}`, {
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

// 특정 직원 특정 달 근태 기록 뽑기
export const getMonthlyCalendarEvents = async (yearMonth, employeeId) => {
  // 자기 근태, 관리자 시점 근태 분기 처리
  const url = employeeId 
    ? `/api/attendance/monthly/calendar/${employeeId}?yearMonth=${yearMonth}`
    : `/api/attendance/monthly/calendar/me?yearMonth=${yearMonth}`;
      
  const response = await apiRequest(url);
  return response.json();
};

// 달력에서 쓰일 뱃지 함수
export const getStatusLabel = (status) => {
    switch(status) {
        case 'WORK': case 'OUT': return '출근';
        case 'LATE': return '지각';
        case 'ABSENT': return '결근';
        case 'LEAVE': return '휴가';
        default: return '미출근';
    }
};

export const getStatusColor = (status) => {
    switch(status) {
        case 'WORK': case 'OUT': return '#22c55e';
        case 'LATE': return '#eab308';
        case 'ABSENT': return '#ef4444';
        case 'LEAVE': return '#a855f7';
        default: return '#e5e7eb';
    }
};

// 일용직 명단 불러오기
export const getDailyWorkersRequest = async () => {
  const response = await apiRequest(`/api/attendance/daily-workers`, {
    method: "GET",
  });
  return response.json();
};

// 일용직 근태 기록 일괄 저장
export const saveDailyAttendanceBatchRequest = async (attendanceList) => {
  const response = await apiRequest(`/api/attendance/batch/daily-batch`, {
    method: "POST",
    body: JSON.stringify(attendanceList),
  });
  return response.text();
};

// 일용직 근태 정정
export const applyDailyCorrectionRequest = async (logId, dtoList) => {
    const token = localStorage.getItem('accessToken'); 

    const response = await fetch(`/api/attendance/daily/${logId}`, {
        method: 'PATCH',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${token}` 
        },
        body: JSON.stringify(dtoList)
    });

    if (!response.ok) {
        // 에러 처리 로직 (필요시 추가)
        const errorData = await response.json().catch(() => ({}));
        throw new Error(errorData.message || '수정 요청 처리에 실패했습니다.');
    }

    return response;
};

// 일용직 근태 기록 유지용
export const getDailyManagementRequest = async (date) => {
    const response = await apiRequest(`/api/attendance/daily-management?date=${date}`);
    return response.json();
};