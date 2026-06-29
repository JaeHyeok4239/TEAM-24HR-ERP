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

export const sendPasswordResetCodeRequest = async (loginId, email) => {
  return await apiRequest("/api/auth/password-reset/code", {
    method: "POST",
    body: JSON.stringify({
      loginId,
      email,
    }),
  });
};

export const verifyPasswordResetCodeRequest = async (loginId, email, code) => {
  const response = await apiRequest("/api/auth/password-reset/code/verify", {
    method: "POST",
    body: JSON.stringify({
      loginId,
      email,
      code,
    }),
  });

  return await response.json();
};

export const resetPasswordRequest = async (
  resetToken,
  newPassword,
  newPasswordConfirm
) => {
  return await apiRequest("/api/auth/password-reset", {
    method: "POST",
    body: JSON.stringify({
      resetToken,
      newPassword,
      newPasswordConfirm,
    }),
  });
};