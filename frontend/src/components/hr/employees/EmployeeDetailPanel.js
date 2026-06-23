"use client";

import { useState } from "react";

import { Card, CardContent, CardHeader } from "@/components/ui/card";

import BasicInfoTab from "./detail/BasicInfoTab";
import HrInfoTab from "./detail/HrInfoTab";
import HistoryTab from "./detail/HistoryTab";
import { DETAIL_TABS } from "./detail/employeeDetailConstants";

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
    return <EmptyDetailPanel message="직원을 선택해주세요." />;
  }

  return (
    <Card className="flex h-full flex-col overflow-hidden rounded-xl border-slate-200 bg-white shadow-sm">
      <CardHeader className="flex h-[82px] shrink-0 justify-center border-b px-5 py-0">
        <div className="flex items-center gap-3">
          <div className="flex h-11 w-11 shrink-0 items-center justify-center rounded-full bg-slate-300 text-sm font-semibold text-white">
            {employee.name?.slice(-2)}
          </div>

          <div className="min-w-0">
            <p className="truncate text-lg font-bold text-slate-900">
              {employee.name}
            </p>

            <p className="mt-1 text-sm text-slate-500">
              {employee.positionName ?? "-"} · {employee.departmentName ?? "-"}
            </p>
          </div>
        </div>
      </CardHeader>

      <div className="h-[52px] shrink-0 border-b bg-white px-5">
        <div className="flex h-full items-end gap-8">
          {DETAIL_TABS.map((tab) => {
            const selected = activeTab === tab.value;

            return (
              <button
                key={tab.value}
                type="button"
                onClick={() => setActiveTab(tab.value)}
                className={`h-full border-b-2 px-1 text-sm font-medium transition ${
                  selected
                    ? "border-slate-900 text-slate-900"
                    : "border-transparent text-slate-500 hover:text-slate-900"
                }`}
              >
                {tab.label}
              </button>
            );
          })}
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
      <CardHeader className="flex h-[58px] shrink-0 justify-center border-b px-5 py-0">
        <p className="text-base font-semibold text-slate-900">직원 상세</p>
      </CardHeader>

      <CardContent className="flex flex-1 items-center justify-center text-sm text-slate-400">
        {message}
      </CardContent>
    </Card>
  );
}
