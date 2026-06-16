'use client';

import Link from 'next/link';
import { usePathname } from 'next/navigation';
import {
  Home, Clock, DollarSign, Users, CheckCircle, ChevronDown, LogOut,
} from 'lucide-react';

import { logoutRequest } from '@/services/authService';
import { useAuthStore } from '@/store/authStore';

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
} from '@/components/ui/sidebar';
import {
  Collapsible,
  CollapsibleContent,
  CollapsibleTrigger,
} from '@/components/ui/collapsible';
import { useState } from 'react';

// 메뉴 props 영역(링크, 아이콘, 라벨, 하위메뉴)
const NAV_ITEMS = [
  { href: '/', icon: Home, label: '홈' },
  { href: '/attendance', icon: Clock, label: '근태 관리' },
  { href: '/payroll', icon: DollarSign, label: '급여 관리' },
  { href: '/hr', icon: Users, label: '인사 관리' },
  {
    href: '/work',
    icon: Users,
    label: '업무 관리',
    children: [
      { href: '/work/schedule', label: '일정 관리' },
    ],
  },

  {
    href: '/approval',
    icon: CheckCircle,
    label: '전자 결재',
    children: [
      { href: '/approval', label: '내 문서함' },
      { href: '/approval/write', label: '문서 작성' },
      { href: '/approval/pending', label: '결재함' },
      { href: '/approval/process', label: '업무 처리함' },
      { href: '/approval/lines', label: '결재선 관리' },
      { href: '/approval/delegate', label: '대리 결재 관리' },
    ],
  },
];

export default function Menu() {
  const pathname = usePathname();

  const accessToken = useAuthStore((state) => state.accessToken);
  const authLogout = useAuthStore((state) => state.logout);

  const userInfo = useAuthStore((state) => state.userInfo);
  const [isProfileMenuOpen, setIsProfileMenuOpen] = useState(false);

  const handleLogout = async () => {
    try {
      await logoutRequest(accessToken);
    } catch (error) {
      console.error('로그아웃 API 호출 실패', error);
    } finally {
      authLogout();
    }
  };

  return (
    <Sidebar collapsible="none" className="w-40 bg-[#1a2f4e] border-r-0">
      {/* 로고 영역(추후 수정) */}
      <SidebarHeader>
        <div className="flex items-center p-2">
          <span className="font-bold text-white">24HR</span>
        </div>
      </SidebarHeader>

      {/* 네비게이션 */}
      <SidebarContent>
        <SidebarGroup>
          <SidebarGroupContent>
            <SidebarMenu>
              {NAV_ITEMS.map(({ href, icon: Icon, label, children }) => {
                const isActive = pathname === href;

                if (!children) {
                  return (
                    <SidebarMenuItem key={href}>
                      <SidebarMenuButton
                        asChild
                        isActive={isActive}
                        className={
                          isActive
                            ? 'bg-[#a4e6d2] text-[#1a2f4e] font-bold hover:bg-[#a4e6d2] hover:text-[#1a2f4e] rounded-none'
                            : 'text-white/80 hover:text-white hover:bg-[#A6FFEA]/20 rounded-none'
                        }
                      >
                        <Link href={href}>
                          <Icon size={20} strokeWidth={2} />
                          <span>{label}</span>
                        </Link>
                      </SidebarMenuButton>
                    </SidebarMenuItem>
                  );
                }

                const isChildActive = children.some((c) => pathname === c.href);

                return (
                  <Collapsible
                    key={href}
                    defaultOpen={isChildActive}
                    className="group/collapsible"
                  >
                    <SidebarMenuItem>
                      <CollapsibleTrigger asChild>
                        <SidebarMenuButton
                          isActive={isActive || isChildActive}
                          className={
                            isActive || isChildActive
                              ? 'bg-[#a4e6d2] text-[#1a2f4e] font-bold hover:bg-[#a4e6d2] hover:text-[#1a2f4e] rounded-none'
                              : 'text-white/80 hover:text-white hover:bg-[#A6FFEA]/20 rounded-none'
                          }
                        >
                          <Icon size={20} strokeWidth={2} />
                          <span>{label}</span>
                          <ChevronDown className="ml-auto transition-transform group-data-[state=open]/collapsible:rotate-180" />
                        </SidebarMenuButton>
                      </CollapsibleTrigger>
                      <CollapsibleContent>
                        <SidebarMenuSub className="border-white/10">
                          {children.map((child) => (
                            <SidebarMenuSubItem key={child.href}>
                              <SidebarMenuSubButton
                                asChild
                                isActive={pathname === child.href}
                                className={
                                  pathname === child.href
                                    ? 'bg-[#A6FFEA]/30 text-white font-semibold'
                                    : 'text-white/70 hover:text-white hover:bg-[#A6FFEA]/10'
                                }
                              >
                                <Link href={child.href}>
                                  <span>{child.label}</span>
                                </Link>
                              </SidebarMenuSubButton>
                            </SidebarMenuSubItem>
                          ))}
                        </SidebarMenuSub>
                      </CollapsibleContent>
                    </SidebarMenuItem>
                  </Collapsible>
                );
              })}
            </SidebarMenu>
          </SidebarGroupContent>
        </SidebarGroup>
      </SidebarContent>

      {/* 유저 정보(추후 수정) */}
      {/* 로그인 사용자 영역 */}
      <SidebarFooter>
        <div className="relative w-full pb-2">

          {/* 프로필 메뉴 */}
          {isProfileMenuOpen && (
            <div className="absolute bottom-full left-2 right-2 mb-2 bg-white border">
              <Link
                href="/my-info"
                onClick={() => setIsProfileMenuOpen(false)}
                className="block px-3 py-2 text-sm text-black"
              >
                내 정보 수정
              </Link>

              <Link
                href="/change-password"
                onClick={() => setIsProfileMenuOpen(false)}
                className="block px-3 py-2 text-sm text-black"
              >
                비밀번호 변경
              </Link>

              <button
                type="button"
                onClick={handleLogout}
                className="w-full px-3 py-2 text-left text-sm text-red-500"
              >
                로그아웃
              </button>
            </div>
          )}

          {/* 프로필 메뉴 열기 버튼 */}
          <button
            type="button"
            onClick={() =>
              setIsProfileMenuOpen((previous) => !previous)
            }
            className="flex w-full flex-col items-center gap-2"
          >
            <div className="flex h-8 w-8 items-center justify-center rounded-full bg-[#8a9bb0]">
              <Users
                size={20}
                className="text-white"
                strokeWidth={1.5}
              />
            </div>

            <p className="text-sm text-white">
              {userInfo?.name ?? '사용자'}
              {userInfo?.positionName && `(${userInfo.positionName})`}
            </p> 
          </button>
        </div>
      </SidebarFooter>
    </Sidebar>
  );
}