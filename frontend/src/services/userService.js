import { apiRequest } from '@/lib/api';

// 현재 로그인 사용자 정보 조회
export const getMyInfoRequest = async () => {
  const response = await apiRequest('/api/users/me');

  return response.json();
};

// 현재 로그인 사용자 정보 수정
export const updateMyInfoRequest = async (requestData) => {
  const response = await apiRequest('/api/users/me', {
    method: 'PATCH',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(requestData),
  });

  return response.json();
};