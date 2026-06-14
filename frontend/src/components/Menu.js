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

// 메뉴 props 영역(링크, 아이콘, 라벨, 하위메뉴)
const NAV_ITEMS = [
  { href: '/', icon: Home, label: '홈' },
  { href: '/attendance', icon: Clock, label: '근태 관리' },
  { href: '/payroll', icon: DollarSign, label: '급여 관리' },
  { href: '/hr', icon: Users, label: '인사 관리' },
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
      <SidebarFooter>
        <div className="flex flex-col items-center gap-2 pb-2">
          <div className="w-8 h-8 rounded-full bg-[#8a9bb0] flex items-center justify-center">
            <Users size={20} className="text-white" strokeWidth={1.5} />
          </div>
          <p className="text-white text-sm">김철수(팀장)</p>
          <button
            onClick={handleLogout}
            className="px-3 py-1 text-xs bg-red-500 text-white rounded flex items-center gap-1"
          >
            <LogOut size={12} />
            로그아웃
          </button>
        </div>
      </SidebarFooter>
    </Sidebar>
  );
}