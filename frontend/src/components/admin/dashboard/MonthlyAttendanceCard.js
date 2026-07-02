"use client";

import { Bar, BarChart, ResponsiveContainer, Tooltip, XAxis, YAxis, Cell } from "recharts";
import AdminDashboardCard from "./AdminDashboardCard";

const CHART_COLORS = {
  work: "#22c55e",
  late: "#eab308",
  absent: "#ef4444",
  leave: "#a855f7",
};

// 더미 데이터
const DUMMY_DATA = [
  { name: "출근", count: 61, color: CHART_COLORS.work },
  { name: "지각", count: 8, color: CHART_COLORS.late },
  { name: "결근", count: 4, color: CHART_COLORS.absent },
  { name: "휴가", count: 3, color: CHART_COLORS.leave },
];

export default function MonthlyAttendanceCard() {
  return (
    <AdminDashboardCard 
        title="월별 근태 현황" 
        description="전체 재직자 76명 기준 통계"
    >
      <div className="flex flex-col h-full gap-4">
        {/* 요약 박스 */}
        <div className="grid grid-cols-4 gap-2">
          {DUMMY_DATA.map((item) => (
            <div key={item.name} className="bg-slate-50 p-3 rounded-lg border border-slate-100 text-center">
              <p className="text-[10px] text-slate-500 font-bold uppercase">{item.name}</p>
              <p className="text-lg font-bold text-slate-800">{item.count}</p>
            </div>
          ))}
        </div>

        {/* 차트 영역 */}
        <div className="flex-1 min-h-0">
          <ResponsiveContainer width="100%" height="100%">
            <BarChart data={DUMMY_DATA} layout="vertical" margin={{ left: 0, right: 20 }}>
              <XAxis type="number" hide />
              <YAxis type="category" dataKey="name" tick={{ fontSize: 12 }} width={40} />
              <Tooltip cursor={{ fill: 'transparent' }} />
              <Bar dataKey="count" radius={[0, 4, 4, 0]} barSize={25}>
                {DUMMY_DATA.map((entry, index) => (
                  <Cell key={`cell-${index}`} fill={entry.color} />
                ))}
              </Bar>
            </BarChart>
          </ResponsiveContainer>
        </div>
      </div>
    </AdminDashboardCard>
  );
}