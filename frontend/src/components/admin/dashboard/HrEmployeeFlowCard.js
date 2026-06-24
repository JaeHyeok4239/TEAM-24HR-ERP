"use client";

import { useEffect, useMemo, useState } from "react";
import {
  Bar,
  BarChart,
  CartesianGrid,
  Legend,
  ResponsiveContainer,
  Tooltip,
  XAxis,
  YAxis,
} from "recharts";
import { BarChart3, UserMinus, UserPlus, Users } from "lucide-react";

import { getHrEmployeeFlowRequest } from "@/services/adminDashboardService";
import AdminDashboardCard from "./AdminDashboardCard";

const FILTERS = [
  { key: "ALL", label: "전체" },
  { key: "REGULAR", label: "정규직" },
  { key: "DAILY", label: "일용직" },
];

const EMPTY_SUMMARY = {
  activeCount: 0,
  joinCount: 0,
  leaveCount: 0,
};

export default function HrEmployeeFlowCard() {
  const [selectedType, setSelectedType] = useState("ALL");
  const [data, setData] = useState(null);
  const [isLoading, setIsLoading] = useState(true);
  const [errorMessage, setErrorMessage] = useState("");

  useEffect(() => {
    const fetchDashboardData = async () => {
      try {
        setIsLoading(true);
        setErrorMessage("");

        const response = await getHrEmployeeFlowRequest();
        setData(response);
      } catch (error) {
        console.error("인사 변동 현황 조회 실패:", error);
        setErrorMessage("인사 변동 현황을 불러오지 못했습니다.");
      } finally {
        setIsLoading(false);
      }
    };

    fetchDashboardData();
  }, []);

  const summary = data?.summary?.[selectedType] ?? EMPTY_SUMMARY;

  const chartData = useMemo(() => {
    if (!data?.monthly) {
      return [];
    }

    return data.monthly.map((item) => {
      if (selectedType === "REGULAR") {
        return {
          month: item.monthLabel,
          joinCount: item.regularJoinCount,
          leaveCount: item.regularLeaveCount,
        };
      }

      if (selectedType === "DAILY") {
        return {
          month: item.monthLabel,
          joinCount: item.dailyJoinCount,
          leaveCount: item.dailyLeaveCount,
        };
      }

      return {
        month: item.monthLabel,
        joinCount: item.regularJoinCount + item.dailyJoinCount,
        leaveCount: item.regularLeaveCount + item.dailyLeaveCount,
      };
    });
  }, [data, selectedType]);

  return (
    <AdminDashboardCard
      title="인사 변동 현황"
      description="최근 6개월 입사/퇴사 추이를 고용형태별로 확인합니다."
      rightContent={
        <div className="rounded-lg bg-blue-50 p-2 text-blue-600">
          <BarChart3 size={22} />
        </div>
      }
    >
      <div className="flex h-full min-h-0 flex-col gap-4">
        <div className="flex items-center justify-between gap-3">
          <div className="flex rounded-lg bg-slate-100 p-1">
            {FILTERS.map((filter) => (
              <button
                key={filter.key}
                type="button"
                onClick={() => setSelectedType(filter.key)}
                className={`rounded-md px-3 py-1.5 text-xs font-semibold transition ${
                  selectedType === filter.key
                    ? "bg-white text-blue-600 shadow-sm"
                    : "text-slate-500 hover:text-slate-900"
                }`}
              >
                {filter.label}
              </button>
            ))}
          </div>

          <p className="text-xs text-slate-400">기준: 최근 6개월</p>
        </div>

        {isLoading && (
          <div className="flex flex-1 items-center justify-center rounded-lg bg-slate-50">
            <p className="text-sm text-slate-400">
              데이터를 불러오는 중입니다.
            </p>
          </div>
        )}

        {!isLoading && errorMessage && (
          <div className="flex flex-1 items-center justify-center rounded-lg bg-red-50">
            <p className="text-sm text-red-500">{errorMessage}</p>
          </div>
        )}

        {!isLoading && !errorMessage && (
          <>
            <div className="grid grid-cols-3 gap-3">
              <SummaryBox
                icon={<Users size={16} />}
                label="현재 재직자"
                value={summary.activeCount}
              />
              <SummaryBox
                icon={<UserPlus size={16} />}
                label="이번 달 입사"
                value={summary.joinCount}
              />
              <SummaryBox
                icon={<UserMinus size={16} />}
                label="이번 달 퇴사"
                value={summary.leaveCount}
              />
            </div>

            <div className="min-h-0 flex-1 rounded-lg bg-slate-50 p-4">
              <div className="mb-3 flex items-center justify-between">
                <p className="text-sm font-semibold text-slate-700">
                  최근 6개월 입퇴사 추이
                </p>
                <p className="text-xs text-slate-400">
                  {FILTERS.find((filter) => filter.key === selectedType)?.label}
                </p>
              </div>
              <div className="h-56 w-full">
                <ResponsiveContainer width="100%" height={220}>
                  <BarChart
                    data={chartData}
                    barGap={8}
                    margin={{ top: 10, right: 10, left: -20, bottom: 0 }}
                  >
                    <CartesianGrid strokeDasharray="3 3" vertical={false} />
                    <XAxis
                      dataKey="month"
                      tickLine={false}
                      axisLine={false}
                      tick={{ fontSize: 12 }}
                    />
                    <YAxis
                      allowDecimals={false}
                      tickLine={false}
                      axisLine={false}
                      tick={{ fontSize: 12 }}
                    />
                    <Tooltip />
                    <Legend />
                    <Bar
                      dataKey="joinCount"
                      name="입사"
                      fill="#60a5fa"
                      radius={[4, 4, 0, 0]}
                    />
                    <Bar
                      dataKey="leaveCount"
                      name="퇴사"
                      fill="#cbd5e1"
                      radius={[4, 4, 0, 0]}
                    />
                  </BarChart>
                </ResponsiveContainer>
              </div>
            </div>
          </>
        )}
      </div>
    </AdminDashboardCard>
  );
}

function SummaryBox({ icon, label, value }) {
  return (
    <div className="rounded-lg border border-slate-100 bg-slate-50 px-3 py-3">
      <div className="flex items-center gap-2 text-slate-500">
        {icon}
        <span className="text-xs font-semibold">{label}</span>
      </div>

      <p className="mt-2 text-xl font-bold text-slate-950">
        {Number(value).toLocaleString()}
      </p>
    </div>
  );
}
