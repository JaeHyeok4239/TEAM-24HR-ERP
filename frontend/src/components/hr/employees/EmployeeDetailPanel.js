"use client";

import { useState } from "react";
import { MousePointerClick } from "lucide-react";

import { Card, CardContent } from "@/components/ui/card";

import BasicInfoTab from "./detail/BasicInfoTab";
import HrInfoTab from "./detail/HrInfoTab";
import HistoryTab from "./detail/HistoryTab";
import {
  DETAIL_TABS,
  STATUS_LABELS,
} from "./detail/employeeDetailConstants";

const STATUS_STYLES = {
  ACTIVE: "bg-emerald-50 text-emerald-700",
  LEAVE: "bg-amber-50 text-amber-700",
  RESIGNED: "bg-slate-100 text-slate-500",
  INACTIVE: "bg-slate-100 text-slate-500",
  LOCKED: "bg-red-50 text-red-700",
};

const getAvatarLabel = (name) => {
  if (!name) {
    return "-";
  }

  return name.slice(-2);
};

export default function EmployeeDetailPanel({
  employee,
  onEmployeeUpdated,
  isLoading,
  options,
}) {
  const [activeTab, setActiveTab] = useState("basic");

  if (isLoading) {
    return <EmptyDetailPanel message="직원 상세 정보를 불러오는 중..." />;
  }

  if (!employee) {
    return <EmptyDetailPanel message="좌측에서 직원을 선택해주세요." />;
  }

  return (
    <Card className="flex h-full flex-col overflow-hidden rounded-xl border-slate-200 bg-white shadow-sm">
      <div className="shrink-0 border-b border-slate-200 bg-white px-5 py-3">
        <div className="flex flex-col gap-3 2xl:flex-row 2xl:items-center">
          <div className="flex min-w-[260px] items-center gap-3">
            <div className="flex h-10 w-10 shrink-0 items-center justify-center rounded-full bg-[#1a2f4e] text-sm font-bold text-white">
              {getAvatarLabel(employee.name)}
            </div>

            <div className="min-w-0">
              <div className="flex min-w-0 items-center gap-2">
                <p className="truncate text-base font-bold text-slate-950">
                  {employee.name}
                </p>

                <StatusBadge status={employee.status} />
              </div>

              <p className="mt-0.5 truncate text-xs font-medium text-slate-500">
                {employee.positionName ?? "-"} · {employee.departmentName ?? "-"}
              </p>
            </div>
          </div>

          <div className="flex h-10 min-w-0 flex-1 rounded-md bg-slate-100 p-1">
            {DETAIL_TABS.map((tab) => {
              const selected = activeTab === tab.value;

              return (
                <button
                  key={tab.value}
                  type="button"
                  onClick={() => setActiveTab(tab.value)}
                  className={[
                    "flex-1 rounded-md px-4 text-sm font-semibold transition-colors",
                    selected
                      ? "bg-white text-[#1a2f4e] shadow-sm"
                      : "text-slate-500 hover:text-slate-900",
                  ].join(" ")}
                >
                  {tab.label}
                </button>
              );
            })}
          </div>
        </div>
      </div>

      <CardContent className="min-h-0 flex-1 overflow-y-auto bg-white p-5">
        {activeTab === "basic" && (
          <BasicInfoTab
            employee={employee}
            onEmployeeUpdated={onEmployeeUpdated}
          />
        )}

        {activeTab === "hr" && (
          <HrInfoTab
            employee={employee}
            options={options}
            onEmployeeUpdated={onEmployeeUpdated}
          />
        )}

        {activeTab === "history" && <HistoryTab employee={employee} />}
      </CardContent>
    </Card>
  );
}

function EmptyDetailPanel({ message }) {
  return (
    <Card className="flex h-full flex-col overflow-hidden rounded-xl border-slate-200 bg-white shadow-sm">
      <CardContent className="flex flex-1 flex-col items-center justify-center text-center">
        <MousePointerClick
          size={80}
          strokeWidth={1.7}
          className="text-slate-300"
        />

        <p className="mt-6 text-base font-medium text-slate-400">{message}</p>
      </CardContent>
    </Card>
  );
}

function StatusBadge({ status }) {
  const label = STATUS_LABELS[status] ?? status ?? "-";
  const className = STATUS_STYLES[status] ?? "bg-slate-100 text-slate-500";

  return (
    <span
      className={[
        "shrink-0 rounded-full px-2 py-0.5 text-xs font-semibold",
        className,
      ].join(" ")}
    >
      {label}
    </span>
  );
}