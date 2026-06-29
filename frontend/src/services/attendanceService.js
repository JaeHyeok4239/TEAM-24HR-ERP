import { apiRequest } from "@/lib/api";

export const getMonthlyAttendanceStats = async (yearMonth) => {
  const response = await apiRequest(`/api/attendance/summary?yearMonth=${yearMonth}`, {
    method: "GET",
  });

  return response.json();
};
