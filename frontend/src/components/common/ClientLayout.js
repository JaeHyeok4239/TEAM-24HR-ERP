"use client";

import { useEffect, useRef, useState } from "react";
import { usePathname, useRouter } from "next/navigation";
import LoginForm from "@/components/auth/LoginForm";
import { SidebarProvider } from "../ui/sidebar";

import { getMyInfoRequest } from "@/services/userService";
import { useAuthStore } from "@/store/authStore";
import MainMenu from "@/components/Menu";
import { Bell } from "lucide-react";
import { useNotification } from "@/hooks/useNotification";

function NotificationBell() {
  const { notifications, removeNotification, clearNotifications } = useNotification();
  const [show, setShow] = useState(false);

  return (
    <div className="fixed top-3 right-4 z-50">
      <button
        onClick={() => setShow((v) => !v)}
        className="relative p-1.5 text-gray-500 hover:text-gray-700 bg-white rounded-full shadow"
      >
        <Bell size={20} />
        {notifications.length > 0 && (
          <span className="absolute top-0 right-0 w-4 h-4 bg-red-500 rounded-full text-[10px] flex items-center justify-center text-white font-bold">
            {notifications.length > 9 ? "9+" : notifications.length}
          </span>
        )}
      </button>

      {show && (
        <div className="absolute right-0 top-10 w-72 bg-white rounded-lg shadow-xl overflow-hidden">
          <div className="p-3 border-b border-gray-100 flex items-center justify-between">
            <span className="text-sm font-semibold text-gray-700">알림</span>
            <button
              onClick={() => {
                setShow(false);
                clearNotifications();
              }}
              className="text-gray-400 hover:text-gray-600 text-xl leading-none"
            >
              ×
            </button>
          </div>
          <div className="max-h-80 overflow-y-auto">
            {notifications.length === 0 ? (
              <p className="text-center text-sm text-gray-400 py-8">
                새 알림이 없어요
              </p>
            ) : (
              notifications.map((n, i) => (
                <div
                  key={i}
                  className="p-3 border-b border-gray-50 hover:bg-gray-50 flex items-start gap-2"
                >
                  <div className="flex-1 min-w-0">
                    <p className="text-xs font-semibold text-blue-600">{n.title}</p>
                    <p className="text-xs text-gray-600 mt-0.5">{n.message}</p>
                  </div>
                  <button
                    onClick={() => removeNotification(i)}
                    className="flex-shrink-0 text-gray-300 hover:text-gray-500 text-base leading-none mt-0.5"
                  >
                    ×
                  </button>
                </div>
              ))
            )}
          </div>
        </div>
      )}
    </div>
  );
}

export default function ClientLayout({ children }) {
  const initializedRef = useRef(false);

  const isLogin = useAuthStore((state) => state.isLogin);
  const isAuthLoading = useAuthStore((state) => state.isAuthLoading);
  const initializeAuth = useAuthStore((state) => state.initializeAuth);
  const setUserInfo = useAuthStore((state) => state.setUserInfo);
  const authLogout = useAuthStore((state) => state.logout);

  const router = useRouter();
  const pathname = usePathname();
  const userInfo = useAuthStore((state) => state.userInfo);

  const isPasswordChangePage = pathname.startsWith("/user/password-change");
  const needPasswordChange = userInfo?.isFirstLogin === "Y";

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

  useEffect(() => {
    if (isAuthLoading || !isLogin) {
      return;
    }

    if (needPasswordChange && !isPasswordChangePage) {
      router.replace("/user/password-change");
    }
  }, [
    isAuthLoading,
    isLogin,
    needPasswordChange,
    isPasswordChangePage,
    router,
  ]);

  // 로그인 상태 확인이 끝날 때까지 화면 분기 대기
  if (isAuthLoading) {
    return (
      <div className="flex w-full h-screen justify-center items-center bg-gradient-to-br from-[#eefdff] via-[#2a436b] to-[#061429]">
        <div className="relative w-14 h-14">
          {/* 바깥 회전 링 */}
          <div className="absolute inset-0 rounded-full border-4 border-[#669da0]/20 border-t-[#669da0] animate-spin" />
          {/* 안쪽 역회전 링 ([animation-direction:reverse]로 반대로 돌게 설정) */}
          <div className="absolute inset-2 rounded-full border-4 border-[#669da0]/10 border-b-[#669da0] animate-spin [animation-direction:reverse] [animation-duration:1.5s]" />
        </div>
      </div>
    );
  }

  // 로그인하지 않은 상태
  if (!isLogin) {
    return <LoginForm />;
  }

  if (needPasswordChange && !isPasswordChangePage) {
    return (
      <div className="flex min-h-screen items-center justify-center bg-slate-50">
        <p className="text-sm text-slate-500">
          비밀번호 변경 화면으로 이동 중입니다.
        </p>
      </div>
    );
  }

  // 최초 로그인 사용자는 비밀번호 변경 화면만 표시
  if (needPasswordChange && isPasswordChangePage) {
    return <div className="min-h-screen bg-slate-50">{children}</div>;
  }

  // 로그인한 상태
  return (
    <div className="flex h-screen bg-slate-100">
      <SidebarProvider>
        <MainMenu />
        <main className="flex-1">{children}</main>
      </SidebarProvider>
      <NotificationBell />
    </div>
  );
}
