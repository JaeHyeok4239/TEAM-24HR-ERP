"use client";

import { useEffect } from "react";
import Menu from "@/components/Menu";
import LoginForm from "@/components/auth/LoginForm";
import { useAuthStore } from "@/store/authStore";
import { SidebarProvider } from "../ui/sidebar";

export default function ClientLayout({ children }) {
  const { isLogin, initializeAuth } = useAuthStore();

  useEffect(() => {
    initializeAuth();
  }, [initializeAuth]);

  if (!isLogin) {
    return <LoginForm />;
  }

  return (
    <div className="flex h-screen bg-slate-100">
      <SidebarProvider>
      <Menu />
      <main className="flex-1">
        {children}
      </main>
      </SidebarProvider>
    </div>
  );
}