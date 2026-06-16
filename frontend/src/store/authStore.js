import { create } from "zustand";

export const useAuthStore = create((set) => ({
  isLogin: false,
  accessToken: null,
  refreshToken: null,
  userInfo: null,

  login: (accessToken, refreshToken) => {
    localStorage.setItem("accessToken", accessToken);
    localStorage.setItem("refreshToken", refreshToken);

    set({
      isLogin: true,
      accessToken,
      refreshToken,
    });
  },

  logout: () => {
    localStorage.removeItem("accessToken");
    localStorage.removeItem("refreshToken");

    set({
      isLogin: false,
      accessToken: null,
      refreshToken: null,
      userInfo: null,
    });
  },

  initializeAuth: () => {
    const accessToken = localStorage.getItem("accessToken");
    const refreshToken = localStorage.getItem("refreshToken");

    set({
      isLogin: !!accessToken,
      accessToken,
      refreshToken,
    });
  },
}));