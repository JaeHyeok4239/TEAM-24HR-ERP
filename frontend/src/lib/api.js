import { useAuthStore } from "@/store/authStore";

// TODO(auth-refactor)(추후/나중에 수정)
// 배포/환경 분리를 위해 나중에 수정할것
// 예시:
// const BASE_URL = process.env.NEXT_PUBLIC_API_BASE_URL ?? "http://localhost:8080";
// .env.local
// NEXT_PUBLIC_API_BASE_URL=http://localhost:8080
// const BASE_URL = "http://localhost:8080";

const API_ORIGIN =
  process.env.NEXT_PUBLIC_API_ORIGIN ??
  (process.env.NODE_ENV === "production" ? "" : "http://localhost:8080");

const clearAuthAndRedirect = async () => {
  try {
    const accessToken =
      useAuthStore.getState().accessToken ||
      localStorage.getItem("accessToken");

    await fetch(`${API_ORIGIN}/api/auth/logout`, {
      method: "POST",
      credentials: "include",
      headers: {
        "Content-Type": "application/json",
        ...(accessToken && {
          Authorization: `Bearer ${accessToken}`,
        }),
      },
    });
  } catch {
    // 로그아웃 API가 실패해도 프론트 인증 상태는 초기화한다.
  } finally {
    useAuthStore.getState().logout();
    window.location.replace("/");
  }
};

const refreshAccessToken = async () => {
  const response = await fetch(`${API_ORIGIN}/api/auth/refresh`, {
    method: "POST",
    credentials: "include",
    headers: {
      "Content-Type": "application/json",
    },
  });

  if (!response.ok) {
    throw new Error("Access Token 재발급에 실패했습니다.");
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

  return newAccessToken;
};

export const apiRequest = async (url, options = {}) => {
  const accessToken =
    useAuthStore.getState().accessToken || localStorage.getItem("accessToken");

  const request = (token) => {

    const isFormData = options.body instanceof FormData;

    return fetch(`${API_ORIGIN}${url}`, {
      ...options,
      credentials: "include",
      headers: {
        ...(!isFormData && { "Content-Type": "application/json" }),
        ...options.headers,
        ...(token && {
          Authorization: `Bearer ${token}`,
        }),
      },
    });
  };

  let response = await request(accessToken);

  const isRefreshExcludedUrl =
    url === "/api/auth/login" ||
    url === "/api/auth/refresh" ||
    url === "/api/auth/logout" ||
    url.startsWith("/api/auth/password-reset");

  if (response.status === 401 && !isRefreshExcludedUrl) {
    try {
      const newAccessToken = await refreshAccessToken();

      response = await request(newAccessToken);
    } catch (error) {
      clearAuthAndRedirect();

      throw error;
    }
  }

  if (response.status === 403) {
    let errorData = null;

    try {
      errorData = await response.json();
    } catch {
      // JSON 형식의 오류 응답이 아니면 기본 메시지를 사용한다.
    }

    const error = new Error(errorData?.message || "접근 권한이 없습니다.");

    error.status = 403;
    error.code = errorData?.code ?? errorData?.errorCode ?? null;

    throw error;
  }

  if (!response.ok) {
    let errorData = null;

    try {
      errorData = await response.json();
    } catch {
      // JSON 형식의 오류 응답이 아니면 기본 메시지를 사용한다.
    }

    const error = new Error(
      errorData?.message || `요청 처리에 실패했습니다. (${response.status})`,
    );

    error.status = response.status;
    error.code = errorData?.code ?? errorData?.errorCode ?? null;

    throw error;
  }

  return response;
};
