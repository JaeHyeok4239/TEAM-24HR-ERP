"use client";

import { useEffect, useMemo, useState } from "react";
import {
  Bar,
  BarChart,
  CartesianGrid,
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

const CHART_COLORS = {
  join: "#1a2f4e",
  leave: "#cbd5e1",
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

  const selectedTypeLabel =
    FILTERS.find((filter) => filter.key === selectedType)?.label ?? "전체";

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
      description="최근 6개월 입사/퇴사 추이"
      rightContent={
        <div className="flex h-9 w-9 items-center justify-center rounded-lg bg-[#eef7ff] text-[#1a2f4e]">
          <BarChart3 size={19} />
        </div>
      }
    >
      <div className="flex h-full min-h-0 flex-col gap-3">
        <div className="flex shrink-0 items-center justify-between gap-3">
          <div className="flex rounded-lg bg-slate-100 p-1">
            {FILTERS.map((filter) => (
              <button
                key={filter.key}
                type="button"
                onClick={() => setSelectedType(filter.key)}
                className={[
                  "rounded-md px-3 py-1.5 text-xs font-semibold transition",
                  selectedType === filter.key
                    ? "bg-white text-[#1a2f4e] shadow-sm"
                    : "text-slate-500 hover:text-slate-900",
                ].join(" ")}
              >
                {filter.label}
              </button>
            ))}
          </div>

          <p className="whitespace-nowrap text-xs font-medium text-slate-400">
            기준: 최근 6개월
          </p>
        </div>

        {isLoading && (
          <div className="flex min-h-0 flex-1 items-center justify-center rounded-lg bg-slate-50">
            <p className="text-sm text-slate-400">
              데이터를 불러오는 중입니다.
            </p>
          </div>
        )}

        {!isLoading && errorMessage && (
          <div className="flex min-h-0 flex-1 items-center justify-center rounded-lg bg-red-50">
            <p className="text-sm text-red-500">{errorMessage}</p>
          </div>
        )}

        {!isLoading && !errorMessage && (
          <>
            <div className="grid shrink-0 grid-cols-3 gap-3">
              <SummaryBox
                icon={<Users size={15} />}
                label="현재 재직"
                value={summary.activeCount}
              />

              <SummaryBox
                icon={<UserPlus size={15} />}
                label="이번 달 입사"
                value={summary.joinCount}
              />

              <SummaryBox
                icon={<UserMinus size={15} />}
                label="이번 달 퇴사"
                value={summary.leaveCount}
              />
            </div>

            <div className="flex min-h-0 flex-1 flex-col rounded-lg bg-slate-50 p-3">
              <div className="mb-2 flex shrink-0 items-center justify-between">
                <p className="text-sm font-semibold text-slate-700">
                  입퇴사 추이
                </p>

                <p className="text-xs font-medium text-slate-400">
                  {selectedTypeLabel}
                </p>
              </div>

              <div className="min-h-[150px] flex-1">
                <ResponsiveContainer width="100%" height="100%">
                  <BarChart
                    data={chartData}
                    barGap={8}
                    margin={{ top: 8, right: 8, left: -24, bottom: 0 }}
                  >
                    <CartesianGrid
                      strokeDasharray="3 3"
                      vertical={false}
                      stroke="#e2e8f0"
                    />

                    <XAxis
                      dataKey="month"
                      tickLine={false}
                      axisLine={false}
                      tick={{ fontSize: 11, fill: "#64748b" }}
                    />

                    <YAxis
                      allowDecimals={false}
                      tickLine={false}
                      axisLine={false}
                      tick={{ fontSize: 11, fill: "#64748b" }}
                    />

                    <Tooltip
                      cursor={{ fill: "rgba(148, 163, 184, 0.12)" }}
                      contentStyle={{
                        borderRadius: "8px",
                        border: "1px solid #e2e8f0",
                        fontSize: "12px",
                      }}
                    />

                    <Bar
                      dataKey="joinCount"
                      name="입사"
                      fill={CHART_COLORS.join}
                      radius={[4, 4, 0, 0]}
                    />

                    <Bar
                      dataKey="leaveCount"
                      name="퇴사"
                      fill={CHART_COLORS.leave}
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
    <div className="rounded-lg border border-slate-100 bg-slate-50 px-3 py-2.5">
      <div className="flex items-center gap-1.5 text-slate-500">
        {icon}
        <span className="truncate text-xs font-semibold">{label}</span>
      </div>

      <p className="mt-1.5 text-xl font-bold text-slate-950">
        {Number(value).toLocaleString()}
      </p>
    </div>
  );
}