import { apiRequest } from "@/lib/api";

export const getHrEmployeeFlowRequest = async () => {
  const response = await apiRequest("/api/admin/dashboard/hr-employee-flow", {
    method: "GET",
  });

  return response.json();
};
