'use client';

import Link from 'next/link';
import { usePathname } from 'next/navigation';
import { Home, Clock, DollarSign, Users, CheckCircle } from 'lucide-react';

import { logoutRequest } from '@/services/authService';
import { useAuthStore } from '@/store/authStore';

// 메뉴 props 영역(링크, 아이콘, 라벨)
const NAV_ITEMS = [
  { href: '/', icon: Home,        label: '홈' },
  { href: '/attendance', icon: Clock,        label: '근태 관리' },
  { href: '/payroll',    icon: DollarSign,   label: '급여 관리' },
  { href: '/hr',         icon: Users,        label: '인사 관리' },
  { href: '/approval',   icon: CheckCircle,  label: '전자 결재' },
];

export default function Menu() {
  const pathname = usePathname();

  const accessToken = useAuthStore((state) => state.accessToken);
  const authLogout = useAuthStore((state) => state.logout);

  const handleLogout = async () => {
    try {
      await logoutRequest(accessToken);
    } catch (error) {
      console.error("로그아웃 API 호출 실패", error);
    } finally {
      authLogout();
    }
  };

  return (
    <aside className="fixed w-36 left-0 top-0 h-screen bg-[#1a2f4e] flex flex-col z-50">
      {/* 로고 영역(추후 수정) */}
      <div className='py-3 flex items-center'>
        <span className='font-bold text-white ml-3'>24HR</span>
      </div>
      {/* 네비게이션 */}
      <nav className="flex-1">
        {NAV_ITEMS.map(({ href, icon: Icon, label }) => {
          const isActive = pathname === href;
          return (
            <Link
              key={href}
              href={href}
              className={`flex items-center gap-2 py-3 text-md transition-colors
                ${isActive
                  ? 'bg-[#a4e6d2] font-semibold'
                  : 'text-white/80 hover:text-white hover:bg-[#A6FFEA]/50'
                }`}
            >
              <Icon size={22} strokeWidth={2} className="w-10" />
              <span>{label}</span>
            </Link>
          );
        })}
      </nav>

      {/* 유저 정보(추후 수정) */}
      <div className="flex flex-col items-center pb-6 gap-2">
        <div className="w-14 h-14 rounded-full bg-[#8a9bb0] flex items-center justify-center">
          <Users size={28} className="text-white" strokeWidth={1.5} />
        </div>
        <p className="text-white text-sm">김철수(팀장)</p>
        <button onClick={handleLogout} className="px-3 py-1 text-xs bg-red-500 text-white rounded">로그아웃</button>
      </div>

    </aside>
  );
}