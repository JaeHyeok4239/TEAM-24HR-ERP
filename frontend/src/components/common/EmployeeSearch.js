"use client";

import { Search } from "lucide-react";

import { Button } from "@/components/ui/button";
import { Card, CardContent } from "@/components/ui/card";
import { Input } from "@/components/ui/input";

const STATUS_LABELS = {
  ACTIVE: "재직",
  LEAVE: "휴직",
  RESIGNED: "퇴사",
  INACTIVE: "비활성",
  LOCKED: "잠김",
};

const STATUS_STYLES = {
  ACTIVE: "bg-emerald-50 text-emerald-700",
  LEAVE: "bg-amber-50 text-amber-700",
  RESIGNED: "bg-slate-100 text-slate-500",
  INACTIVE: "bg-slate-100 text-slate-500",
  LOCKED: "bg-red-50 text-red-700",
};

const STATUS_ORDER = {
  ACTIVE: 1,
  LEAVE: 2,
  RESIGNED: 3,
  INACTIVE: 4,
  LOCKED: 5,
};

const getStatusOrder = (status) => {
  return STATUS_ORDER[status] ?? 99;
};

const getAvatarLabel = (name) => {
  if (!name) {
    return "-";
  }

  return name.slice(-2);
};

export default function EmployeeSearch({
  employees,
  selectedEmployeeId,
  keyword,
  isLoading,
  onKeywordChange,
  onKeywordKeyDown,
  onSearch,
  selectEmployee,
}) {
  const sortedEmployees = [...employees].sort((first, second) => {
    const statusCompare =
      getStatusOrder(first.status) - getStatusOrder(second.status);

    if (statusCompare !== 0) {
      return statusCompare;
    }

    return (first.name ?? "").localeCompare(second.name ?? "", "ko-KR");
  });

  return (
    <Card className="flex h-full flex-col overflow-hidden rounded-xl border-slate-200 bg-white shadow-sm">
      <div className="shrink-0 bg-white px-3 py-3">
        <div className="flex gap-2">
          <div className="relative min-w-0 flex-1">
            <Search
              size={15}
              className="absolute left-3 top-1/2 -translate-y-1/2 text-slate-400"
            />

            <Input
              value={keyword}
              onChange={(event) => onKeywordChange(event.target.value)}
              onKeyDown={onKeywordKeyDown}
              placeholder="이름, 사번, 로그인 ID"
              className="h-9 rounded-full border-slate-300 bg-slate-50 pl-9 text-sm focus-visible:ring-[#a7f3ff]"
            />
          </div>

          <Button
            type="button"
            variant="outline"
            size="sm"
            onClick={onSearch}
            className="h-9 shrink-0 rounded-full border-slate-300 bg-white px-3 text-sm font-medium text-slate-700 hover:bg-slate-50"
          >
            검색
          </Button>
        </div>
      </div>

      <CardContent className="min-h-0 flex-1 p-0">
        {isLoading ? (
          <div className="flex h-full items-center justify-center text-sm text-slate-400">
            직원 목록을 불러오는 중...
          </div>
        ) : sortedEmployees.length === 0 ? (
          <div className="flex h-full items-center justify-center text-sm text-slate-400">
            조회된 직원이 없습니다.
          </div>
        ) : (
          <div className="h-full overflow-y-auto">
            {sortedEmployees.map((employee) => {
              const selected = selectedEmployeeId === employee.employeeId;

              const statusLabel =
                STATUS_LABELS[employee.status] ?? employee.status ?? "-";

              const statusClassName =
                STATUS_STYLES[employee.status] ?? "bg-slate-100 text-slate-500";

              return (
                <button
                  key={employee.employeeId}
                  type="button"
                  onClick={() => selectEmployee(employee.employeeId)}
                  className={[
                    "flex w-full items-center gap-3 border-b border-b-slate-100 px-4 py-2.5 text-left transition-colors",
                    selected ? "bg-[#eef7ff]" : "bg-white hover:bg-slate-50",
                  ].join(" ")}
                >
                  <div
                    className={[
                      "flex h-8 w-8 shrink-0 items-center justify-center rounded-full text-[11px] font-bold",
                      selected
                        ? "bg-[#1a2f4e] text-white"
                        : "bg-slate-200 text-slate-600",
                    ].join(" ")}
                  >
                    {getAvatarLabel(employee.name)}
                  </div>

                  <div className="flex min-w-0 flex-1 items-center gap-2">
                    <p
                      className={[
                        "max-w-[72px] shrink-0 truncate text-sm font-semibold",
                        selected ? "text-[#1a2f4e]" : "text-slate-900",
                      ].join(" ")}
                      title={employee.name ?? "-"}
                    >
                      {employee.name ?? "-"}
                    </p>

                    <p
                      className="min-w-0 flex-1 truncate whitespace-nowrap text-xs font-medium text-slate-500"
                      title={`${employee.positionName ?? "-"} · ${
                        employee.departmentName ?? "-"
                      }`}
                    >
                      {employee.positionName ?? "-"} ·{" "}
                      {employee.departmentName ?? "-"}
                    </p>
                  </div>

                  <span
                    className={[
                      "shrink-0 rounded-full px-2 py-0.5 text-xs font-semibold",
                      statusClassName,
                    ].join(" ")}
                  >
                    {statusLabel}
                  </span>
                </button>
              );
            })}
          </div>
        )}
      </CardContent>
    </Card>
  );
}
