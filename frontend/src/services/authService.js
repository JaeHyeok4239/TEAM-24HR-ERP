import { apiRequest } from "@/lib/api";

// (추후/나중에 수정)
// 배포/환경 분리를 위해 나중에 수정할 것
// 예시:
// const BASE_URL = process.env.NEXT_PUBLIC_API_BASE_URL ?? "http://localhost:8080";
// .env.local
// NEXT_PUBLIC_API_BASE_URL=http://localhost:8080
const API_ORIGIN =
  process.env.NEXT_PUBLIC_API_ORIGIN ??
  (process.env.NODE_ENV === "production" ? "" : "http://localhost:8080");

// 로그인 요청
export const loginRequest = async (loginId, password) => {
  const response = await fetch(`${API_ORIGIN}/api/auth/login`, {
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