"use client";

import { useEffect, useRef } from "react";

import Menu from "@/components/Menu";
import LoginForm from "@/components/auth/LoginForm";
import { SidebarProvider } from "../ui/sidebar";

import { getMyInfoRequest } from "@/services/userService";
import { useAuthStore } from "@/store/authStore";

export default function ClientLayout({ children }) {
  const initializedRef = useRef(false);

  const isLogin = useAuthStore((state) => state.isLogin);
  const isAuthLoading = useAuthStore((state) => state.isAuthLoading);
  const initializeAuth = useAuthStore((state) => state.initializeAuth);
  const setUserInfo = useAuthStore((state) => state.setUserInfo);
  const authLogout = useAuthStore((state) => state.logout);

  useEffect(() => {
    // 개발 환경에서 useEffect가 중복 실행 방지
    if (initializedRef.current) {
      return;
    }

    initializedRef.current = true;

    const restoreAuth = async () => {
      // 1. localStorage에서 토큰 복구
      const hasAccessToken = initializeAuth();

      // 2. Access Token이 없으면 로그인 화면 표시
      if (!hasAccessToken) {
        return;
      }

      try {
        // 3. 현재 로그인 사용자 정보 조회
        const userInfo = await getMyInfoRequest();

        // 4. 사용자 정보 저장 및 로그인 상태 확정
        setUserInfo(userInfo);
      } catch (error) {
        // 5. 사용자 정보 조회 실패 시 인증 정보 초기화
        console.error("로그인 상태 복구 실패:", error);
        authLogout();
      }
    };

    restoreAuth();
  }, [initializeAuth, setUserInfo, authLogout]);

  // 로그인 상태 확인이 끝날 때까지 화면 분기 대기
  if (isAuthLoading) {
    return <div>로그인 정보 확인 중</div>;
  }

  // 로그인하지 않은 상태
  if (!isLogin) {
    return <LoginForm />;
  }

  // 로그인한 상태
  return (
    <div className="flex h-screen bg-slate-100">
      <SidebarProvider>
        <Menu />
        <main className="flex-1">{children}</main>
      </SidebarProvider>
    </div>
  );
}
