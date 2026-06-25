import { apiRequest } from "@/lib/api";

const BASE_URL = "http://localhost:8080";

// 로그인 요청
export const loginRequest = async (loginId, password) => {
  const response = await fetch(`${BASE_URL}/api/auth/login`, {
    method: "POST",
    credentials: "include",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify({
      loginId,
      password,
    }),
  });

  if (!response.ok) {
    throw new Error("로그인 실패");
  }

  return response.json();
};

// 로그아웃 요청
export const logoutRequest = async () => {
  return apiRequest("/api/auth/logout", {
    method: "POST",
  });
};