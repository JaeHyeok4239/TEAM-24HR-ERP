"use client";

import { useEffect } from "react";
import { useRouter } from "next/navigation";
import { useAuthStore } from "@/store/authStore";
import { hasAnyRole, ATTENDANCE_VIEW_ROLES } from "@/lib/roles";

export default function AttendanceLayout({ children }) {
  const { userInfo, isAuthLoading } = useAuthStore();
  const router = useRouter();

  useEffect(() => {
    if (!isAuthLoading && !hasAnyRole(userInfo?.roles, ATTENDANCE_VIEW_ROLES)) {
      alert("근태 관리 권한이 없습니다.");
      router.replace("/"); 
    }
  }, [userInfo, isAuthLoading, router]);

  if (isAuthLoading) return <div>권한 확인 중</div>;

  return <>{children}</>;
}