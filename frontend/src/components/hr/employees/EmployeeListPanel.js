"use client";

import { Search } from "lucide-react";

import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Input } from "@/components/ui/input";

const STATUS_LABELS = {
  ACTIVE: "재직",
  LEAVE: "휴직",
  RESIGNED: "퇴사",
  INACTIVE: "비활성",
  LOCKED: "잠김",
};

export default function EmployeeListPanel({
  employees,
  selectedEmployeeId,
  keyword,
  isLoading,
  onKeywordChange,
  onKeywordKeyDown,
  onSearch,
  onSelectEmployee,
}) {
  return (
    <Card className="flex h-full flex-col overflow-hidden rounded-xl border-slate-200 bg-white shadow-sm">
      <CardHeader className="flex h-[56px] shrink-0 justify-center border-b px-4 py-0">
        <CardTitle className="text-left text-base font-semibold text-slate-900">
          직원 목록
        </CardTitle>
      </CardHeader>

      <div className="shrink-0 border-b bg-white px-3 py-3">
        <div className="flex gap-2">
          <div className="relative flex-1">
            <Search
              size={15}
              className="absolute left-3 top-1/2 -translate-y-1/2 text-slate-400"
            />

            <Input
              value={keyword}
              onChange={(event) => onKeywordChange(event.target.value)}
              onKeyDown={onKeywordKeyDown}
              placeholder="이름, 사번, 로그인 ID 검색"
              className="h-9 pl-9 text-sm"
            />
          </div>

          <Button type="button" variant="outline" size="sm" onClick={onSearch}>
            검색
          </Button>
        </div>
      </div>

      <CardContent className="min-h-0 flex-1 p-0">
        {isLoading ? (
          <div className="flex h-full items-center justify-center text-sm text-slate-400">
            직원 목록을 불러오는 중...
          </div>
        ) : employees.length === 0 ? (
          <div className="flex h-full items-center justify-center text-sm text-slate-400">
            조회된 직원이 없습니다.
          </div>
        ) : (
          <div className="h-full overflow-y-auto">
            {employees.map((employee) => {
              const selected = selectedEmployeeId === employee.employeeId;

              return (
                <button
                  key={employee.employeeId}
                  type="button"
                  onClick={() => onSelectEmployee(employee.employeeId)}
                  className={`flex w-full items-start gap-3 border-b px-4 py-3 text-left transition hover:bg-slate-50 ${
                    selected ? "bg-slate-100" : "bg-white"
                  }`}
                >
                  <div className="flex h-9 w-9 shrink-0 items-center justify-center rounded-full bg-slate-300 text-xs font-semibold text-white">
                    {employee.name?.slice(-2)}
                  </div>

                  <div className="min-w-0 flex-1">
                    <div className="flex items-center gap-2">
                      <p className="truncate text-sm font-semibold text-slate-900">
                        {employee.name}
                      </p>

                      <Badge variant="outline">
                        {STATUS_LABELS[employee.status] ?? employee.status}
                      </Badge>
                    </div>

                    <p className="mt-1 truncate text-xs text-slate-500">
                      {employee.positionName ?? "-"} ·{" "}
                      {employee.departmentName ?? "-"}
                    </p>

                    <p className="mt-1 truncate text-xs text-slate-400">
                      {employee.email ?? "-"}
                    </p>
                  </div>
                </button>
              );
            })}
          </div>
        )}
      </CardContent>
    </Card>
  );
}