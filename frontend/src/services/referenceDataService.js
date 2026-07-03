import { apiRequest } from '@/lib/api';

const DEPARTMENT_API =
  '/api/hr/reference-data/departments';

export const getDepartmentsRequest = async () => {
  const response = await apiRequest(DEPARTMENT_API);

  return response.json();
};

export const createDepartmentRequest = async (
  requestData
) => {
  const response = await apiRequest(DEPARTMENT_API, {
    method: 'POST',
    body: JSON.stringify(requestData),
  });

  return response.json();
};

export const updateDepartmentRequest = async (
  departmentId,
  requestData
) => {
  const response = await apiRequest(
    `${DEPARTMENT_API}/${departmentId}`,
    {
      method: 'PATCH',
      body: JSON.stringify(requestData),
    }
  );

  return response.json();
};

const POSITION_API = "/api/hr/reference-data/positions";

export const getPositionsRequest = async () => {
  const response = await apiRequest(POSITION_API);
  return response.json();
};

export const createPositionRequest = async (requestData) => {
  const response = await apiRequest(POSITION_API, {
    method: "POST",
    body: JSON.stringify(requestData),
  });

  return response.json();
};

export const updatePositionRequest = async (positionId, requestData) => {
  const response = await apiRequest(`${POSITION_API}/${positionId}`, {
    method: "PATCH",
    body: JSON.stringify(requestData),
  });

  return response.json();
};