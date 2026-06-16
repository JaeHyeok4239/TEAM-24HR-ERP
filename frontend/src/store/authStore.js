import { create } from "zustand";

export const useAuthStore = create((set) => ({
  isLogin: false,
  isAuthLoading: true,

  accessToken: null,
  refreshToken: null,

  userInfo: null,

  // 로그인 API 성공 후 토큰 저장
  login: (accessToken, refreshToken) => {
    localStorage.setItem("accessToken", accessToken);
    localStorage.setItem("refreshToken", refreshToken);

    set({
      accessToken,
      refreshToken,
      isAuthLoading: true,
    });
  },

  // /api/users/me 성공 후 사용자 정보 저장
  setUserInfo: (userInfo) => {
    set({
      userInfo,
      isLogin: true,
      isAuthLoading: false,
    });
  },

  // 인증 확인 중 상태 변경
  setAuthLoading: (isAuthLoading) => {
    set({
      isAuthLoading,
    });
  },

  // 로그아웃 및 인증 정보 초기화
  logout: () => {
    localStorage.removeItem("accessToken");
    localStorage.removeItem("refreshToken");

    set({
      isLogin: false,
      isAuthLoading: false,
      accessToken: null,
      refreshToken: null,
      userInfo: null,
    });
  },

  // 새로고침 시 localStorage 토큰 복구
  initializeAuth: () => {
    const accessToken = localStorage.getItem("accessToken");
    const refreshToken = localStorage.getItem("refreshToken");

    if (!accessToken) {
      set({
        isLogin: false,
        isAuthLoading: false,
        accessToken: null,
        refreshToken: null,
        userInfo: null,
      });

      return false;
    }

    set({
      isLogin: false,
      isAuthLoading: true,
      accessToken,
      refreshToken,
      userInfo: null,
    });

    return true;
  },
}));