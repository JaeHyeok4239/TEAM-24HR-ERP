import { useAuthStore } from "@/store/authStore";

const BASE_URL = "http://localhost:8080";

export const apiRequest = async (url, options = {}) => {
  const accessToken =
    useAuthStore.getState().accessToken
    || localStorage.getItem("accessToken");

  const response = await fetch(`${BASE_URL}${url}`, {
    ...options,
    headers: {
      "Content-Type": "application/json",
      ...(accessToken && {
        Authorization: `Bearer ${accessToken}`,
      }),
      ...options.headers,
    },
  });

  if (!response.ok) {
    throw new Error("API 요청 실패");
  }

  return response;
};