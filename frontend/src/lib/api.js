import { useAuthStore } from "@/store/authStore";

const BASE_URL = "http://localhost:8080";

const clearAuthAndRedirect = () => {
  useAuthStore.getState().logout();
  window.location.replace("/");
};

const refreshAccessToken = async () => {
  const refreshToken = localStorage.getItem("refreshToken");

  if (!refreshToken) {
    throw new Error("Refresh Token이 없습니다.");
  }

  const response = await fetch(`${BASE_URL}/api/auth/refresh`, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify({
      refreshToken,
    }),
  });

  if (!response.ok) {
    throw new Error("Access Token 재발급에 실패했습니다.")
  }

  const data = await response.json();
  const newAccessToken = data.accessToken;

  if (!newAccessToken) {
    throw new Error("재발급 응답에 Access Token이 없습니다.");
  }

  localStorage.setItem("accessToken", newAccessToken);

  useAuthStore.setState({
    accessToken: newAccessToken,
  });

  return newAccessToken
}

export const apiRequest = async (url, options = {}) => {
  const accessToken =
    useAuthStore.getState().accessToken
    || localStorage.getItem("accessToken");

  const request = (token) => {
    return fetch(`${BASE_URL}${url}`, {
      ...options,
      headers: {
        "Content-Type": "application/json",
        ...options.headers,
        ...(token && {
          Authorization: `Bearer ${token}`,
        }),
      },
    });
  };

  let response = await request(accessToken);

  if (response.status === 401 && url !== "/api/auth/login") {
    try {
      const newAccessToken = await refreshAccessToken();

      response = await request(newAccessToken);
    } catch (error) {
      clearAuthAndRedirect();

      throw error;
    }
  }

  if (!response.ok) {
    throw new Error(`API 요청 실패: ${response.status}`);
  }

  return response;
};

import axios from "axios";

const api = axios.create({
    baseURL: "http://localhost:8080",
    headers: {
        "Content-Type": "application/json"
    }
});

export default api;
