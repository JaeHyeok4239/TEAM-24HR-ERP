"use client";

import { useState, useEffect } from "react";
import Link from "next/link";
import { usePathname } from "next/navigation";
import {
  Home,
  Clock,
  DollarSign,
  Users,
  CheckCircle,
  ChevronDown,
  ShieldCheck,
} from "lucide-react";

import { logoutRequest } from "@/services/authService";
import { useAuthStore } from "@/store/authStore";

import { Collapsible } from "@base-ui/react/collapsible";
import { Menu } from "@base-ui/react/menu";

import {
  ROLES,
  HR_MANAGE_ROLES,
  HR_VIEW_ROLES,
  DEFAULT_APR_ROLES,
  filterNavItemsByRole,
} from "@/lib/roles";

import {
  Sidebar,
  SidebarContent,
  SidebarFooter,
  SidebarGroup,
  SidebarGroupContent,
  SidebarHeader,
  SidebarMenu,
  SidebarMenuButton,
  SidebarMenuItem,
  SidebarMenuSub,
  SidebarMenuSubButton,
  SidebarMenuSubItem,
} from "@/components/ui/sidebar";

const NAV_ITEMS = [
  { href: "/", icon: Home, label: "홈" },
  {
    href: "/admin",
    icon: ShieldCheck,
    label: "관리자 대시보드",
    allowedRoles: [ROLES.ADMIN],
  },
  {
    href: "/attendance",
    icon: Clock,
    label: "근태 관리",
    children: [
      { href: "/attendance/my", label: "내 근태 현황" },
      { 
        href: "/attendance/admin/regular", 
        label: "정규직 근태 관리",
        allowedRoles: [ROLES.ADMIN, ROLES.ATTENDANCE]
      },
      { 
        href: "/attendance/admin/daily", 
        label: "일용직 근태 관리",
        allowedRoles: [ROLES.ADMIN, ROLES.ATTENDANCE]
      },
    ],
  },
  {
    href: "/payroll",
    icon: DollarSign,
    label: "급여 관리",
    children: [
      { href: "/payroll", label: "급여 대장" },
      { href: "/payroll/calculate", label: "급여 계산" },
      { href: "/payroll/salary", label: "기본급 관리" },
    ],
  },
  {
    href: "/hr",
    icon: Users,
    label: "인사 관리",
    children: [
      {
        href: "/hr/employees",
        label: "직원 목록",
        allowedRoles: HR_VIEW_ROLES,
      },
      {
        href: "/hr/evaluations",
        label: "인사평가",
        allowedRoles: HR_VIEW_ROLES,
      },
      {
        href: "/hr/reference-data",
        label: "기준정보 관리",
        allowedRoles: HR_MANAGE_ROLES,
      },
    ],
  },
  {
    href: "/work",
    icon: Users,
    label: "업무 관리",
    children: [
      { href: "/work/schedule", label: "일정 관리" },
      { href: "/work/meeting", label: "회의실 예약" },
    ],
  },
  {
    href: "/approval",
    icon: CheckCircle,
    label: "전자 결재",
    children: [
      { href: "/approval/document/write", label: "문서 작성"},
      { href: "/approval/document", label: "내 문서함"},
      { href: "/approval/pending", label: "결재함"},
      { href: "/approval/lines", label: "결재선 관리", allowedRoles : HR_MANAGE_ROLES},
      { href: "/approval/delegate", label: "대리 결재 관리", allowedRoles : DEFAULT_APR_ROLES},
    ],
  },
];

export default function MainMenu() {
  const pathname = usePathname();

  const authLogout = useAuthStore((state) => state.logout);
  const userInfo = useAuthStore((state) => state.userInfo);

  const filteredNavItems = filterNavItemsByRole(
    NAV_ITEMS,
    userInfo?.roles ?? [],
  );


  // 현재 열린 부모 메뉴의 href를 단일 상태로 관리
  const [openMenu, setOpenMenu] = useState(() => {
    const active = NAV_ITEMS.find(
      (item) => item.children?.some((c) => pathname === c.href)
    );
    return active?.href ?? null;
  });

  const handleLogout = async () => {
    try {
      await logoutRequest();
    } catch (error) {
      // 로그아웃 API가 실패해도 프론트 인증 상태는 초기화한다.
    } finally {
      authLogout();
    }
  };

  return (
    <Sidebar collapsible="none" className="w-46 bg-[#1a2f4e] border-r-0">
      <SidebarHeader>
        <div className="flex items-center p-2">
          <span className="font-bold text-white">24HR</span>
        </div>
      </SidebarHeader>

      <SidebarContent className="w-46">
        <SidebarGroup>
          <SidebarGroupContent>
            <SidebarMenu>
              {filteredNavItems.map((item) => {
                const isActive = pathname === item.href;

                if (!item.children) {
                  return (
                    <SidebarMenuItem key={item.href}>
                      <SidebarMenuButton
                        render={<Link href={item.href} />}
                        isActive={isActive}
                        className={
                          isActive
                            ? "bg-[#a4e6d277] text-[#1a2f4e] font-bold hover:bg-[#a4e6d2] hover:text-[#1a2f4e] rounded-none"
                            : "text-white/80 hover:text-white hover:bg-[#A6FFEA]/20 rounded-none"
                        }
                      >
                        <item.icon size={20} strokeWidth={2} />
                        <span>{item.label}</span>
                      </SidebarMenuButton>
                    </SidebarMenuItem>
                  );
                }

                return (
                  <ControlledCollapsibleMenuItem
                    key={item.href}
                    item={item}
                    pathname={pathname}
                    open={openMenu === item.href}
                    onOpenChange={(next) =>
                      setOpenMenu(next ? item.href : null)
                    }
                  />
                );
              })}
            </SidebarMenu>
          </SidebarGroupContent>
        </SidebarGroup>
      </SidebarContent>

      <SidebarFooter>
        <div className="relative w-full pb-2">
          <Menu.Root>
            <Menu.Trigger className="flex w-full flex-col items-center gap-2 outline-none cursor-pointer">
              <div className="flex h-8 w-8 items-center justify-center rounded-full bg-[#8a9bb0]">
                <Users size={20} className="text-white" strokeWidth={1.5} />
              </div>
              <p className="text-sm text-white">
                {userInfo?.name ?? "사용자"}
                {userInfo?.positionName && `(${userInfo.positionName})`}
              </p>
            </Menu.Trigger>

            <Menu.Portal>
              <Menu.Positioner side="top" align="center" sideOffset={8}>
                <Menu.Popup className="w-40 h-45 bg-[#e2fcff] rounded-xl shadow-lg py-1 flex flex-col z-50 transition-opacity duration-200 opacity-100 data-[starting-style]:opacity-0 data-[ending-style]:opacity-0">
                  <div className="flex flex-col justify-center gap-1">
                    <Menu.Item
                      render={<Link href="/user/my-info" />}
                      className="mt-3 p-3 text-sm text-[#051a3a] hover:text-[#59a3c0] hover:bg-[#bfe7ee] outline-none"
                    >
                      내 정보 수정
                    </Menu.Item>

                    <Menu.Item
                      render={<Link href="/user/password-change" />}
                      className="p-3 text-sm text-[#073468] hover:text-[#59a3c0] hover:bg-[#bfe7ee] outline-none"
                    >
                      비밀번호 변경
                    </Menu.Item>

                    <Menu.Item
                      render={
                        <div
                          style={{ cursor: "pointer" }}
                          type="button"
                          onClick={handleLogout}
                          className="p-3 text-left text-sm text-[#ff3737] hover:text-[#ff0000] hover:bg-[#bfe7ee] outline-none"
                        />
                      }
                    >
                      로그아웃
                    </Menu.Item>
                  </div>
                </Menu.Popup>
              </Menu.Positioner>
            </Menu.Portal>
          </Menu.Root>
        </div>
      </SidebarFooter>
    </Sidebar>
  );
}

function ControlledCollapsibleMenuItem({ item, pathname, open, onOpenChange }) {
  const Icon = item.icon;
  const isChildActive = item.children.some((c) => pathname === c.href);
  const isActive = pathname === item.href;

  return (
    <Collapsible.Root
      open={open}
      onOpenChange={onOpenChange}
      className="group/collapsible"
    >
      <SidebarMenuItem>
        <Collapsible.Trigger
          render={
            <SidebarMenuButton
              isActive={isActive || isChildActive}
              className={
                isActive || isChildActive
                  ? "bg-[#a4e6d277] text-[#1a2f4e] font-bold hover:bg-[#a4e6d277] hover:text-[#1a2f4e] rounded-none"
                  : "text-white/80 hover:text-white hover:bg-[#A6FFEA]/20 rounded-none"
              }
            />
          }
        >
          <Icon size={20} strokeWidth={2} />
          <span>{item.label}</span>
          <ChevronDown className="ml-auto transition-transform group-data-[open]:rotate-180" />
        </Collapsible.Trigger>

        <Collapsible.Panel>
          <SidebarMenuSub className="p-3 border-white/10">
            {item.children.map((child) => (
              <SidebarMenuSubItem key={child.href}>
                <SidebarMenuSubButton
                  render={<Link href={child.href} />}
                  isActive={pathname === child.href}
                  className={
                    pathname === child.href
                      ? "bg-[#8fc8cc]/80! font-bold transition-colors duration-200"
                      : "text-white/70 hover:text-white hover:bg-[#A6FFEA]/10 transition-colors duration-200"
                  }
                >
                  <span>{child.label}</span>
                </SidebarMenuSubButton>
              </SidebarMenuSubItem>
            ))}
          </SidebarMenuSub>
        </Collapsible.Panel>
      </SidebarMenuItem>
    </Collapsible.Root>
  );
}