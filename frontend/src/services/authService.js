import { apiRequest } from "@/lib/api";

const BASE_URL = "http://localhost:8080";

export const loginRequest = async (loginId, password) => {
  const response = await fetch(`${BASE_URL}/api/auth/login`, {
    method: "POST",
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

export const logoutRequest = async () => {
  return apiRequest("/api/auth/logout", {
    method: "POST",
  });
};