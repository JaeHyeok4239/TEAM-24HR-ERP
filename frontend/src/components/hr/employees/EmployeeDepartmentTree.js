import { Building2, ChevronRight, Users } from "lucide-react";

import { Badge } from "@/components/ui/badge";
import {
  Card,
  CardContent,
  CardHeader,
  CardTitle,
} from "@/components/ui/card";

export default function EmployeeDepartmentTree({
  departmentTree,
  selectedDepartmentId,
  selectedEmploymentType,
  isLoading,
  onSelectAllEmployees,
  onSelectDepartment,
  onSelectDailyEmployees,
}) {
  const totalRegularActiveEmployeeCount =
    departmentTree?.totalRegularActiveEmployeeCount ?? 0;

  const totalDailyActiveEmployeeCount =
    departmentTree?.totalDailyActiveEmployeeCount ?? 0;

  const departments = departmentTree?.departments ?? [];

  const isAllRegularSelected =
    selectedEmploymentType === "REGULAR" && selectedDepartmentId === "";

  const isDailySelected = selectedEmploymentType === "DAILY";

  return (
    <Card className="flex h-full flex-col overflow-hidden rounded-xl border-slate-200 bg-white shadow-sm">
      <CardHeader className="flex h-[56px] shrink-0 justify-center border-b px-4 py-0">
        <CardTitle className="flex items-center gap-2 text-base font-semibold text-slate-900">
          <Building2 size={17} />
          부서
        </CardTitle>
      </CardHeader>

      <CardContent className="min-h-0 flex-1 overflow-y-auto p-3">
        {isLoading ? (
          <div className="flex h-full items-center justify-center text-sm text-slate-400">
            부서 정보를 불러오는 중...
          </div>
        ) : (
          <div className="space-y-1">
            <DepartmentButton
              selected={isAllRegularSelected}
              label="전체 직원"
              count={totalRegularActiveEmployeeCount}
              icon={<Users size={15} />}
              onClick={onSelectAllEmployees}
            />

            <div className="space-y-1 pt-1">
              {departments.map((department) => (
                <DepartmentTreeNode
                  key={department.departmentId}
                  department={department}
                  selectedDepartmentId={selectedDepartmentId}
                  selectedEmploymentType={selectedEmploymentType}
                  onSelectDepartment={onSelectDepartment}
                />
              ))}
            </div>

            <div className="my-3 border-t border-slate-200" />

            <DepartmentButton
              selected={isDailySelected}
              label="일용직"
              count={totalDailyActiveEmployeeCount}
              icon={<Users size={15} />}
              onClick={onSelectDailyEmployees}
            />
          </div>
        )}
      </CardContent>
    </Card>
  );
}

function DepartmentTreeNode({
  department,
  selectedDepartmentId,
  selectedEmploymentType,
  onSelectDepartment,
  depth = 0,
}) {
  const selected =
    selectedEmploymentType === "REGULAR" &&
    selectedDepartmentId === String(department.departmentId);

  const hasChildren = department.children?.length > 0;

  return (
    <div>
      <button
        type="button"
        onClick={() => onSelectDepartment(department.departmentId)}
        className={`flex h-9 w-full items-center justify-between rounded-md pr-3 text-left text-sm transition ${
          selected
            ? "bg-slate-900 text-white"
            : "text-slate-700 hover:bg-slate-100"
        }`}
        style={{ paddingLeft: `${12 + depth * 18}px` }}
      >
        <span className="flex min-w-0 items-center gap-1.5">
          {hasChildren ? (
            <ChevronRight
              size={13}
              className={`shrink-0 ${
                selected ? "text-white" : "text-slate-400"
              }`}
            />
          ) : (
            <span className="w-3.5 shrink-0" />
          )}

          <span className="truncate">{department.departmentName}</span>
        </span>

        <Badge
          variant={selected ? "secondary" : "outline"}
          className="ml-2 h-5 shrink-0 px-2 text-xs"
        >
          {department.activeEmployeeCount}
        </Badge>
      </button>

      {hasChildren && (
        <div className="mt-1 space-y-1">
          {department.children.map((child) => (
            <DepartmentTreeNode
              key={child.departmentId}
              department={child}
              selectedDepartmentId={selectedDepartmentId}
              selectedEmploymentType={selectedEmploymentType}
              onSelectDepartment={onSelectDepartment}
              depth={depth + 1}
            />
          ))}
        </div>
      )}
    </div>
  );
}

function DepartmentButton({ selected, label, count, icon, onClick }) {
  return (
    <button
      type="button"
      onClick={onClick}
      className={`flex h-9 w-full items-center justify-between rounded-md px-3 text-left text-sm transition ${
        selected
          ? "bg-slate-900 text-white"
          : "text-slate-700 hover:bg-slate-100"
      }`}
    >
      <span className="flex items-center gap-2 font-medium">
        {icon}
        {label}
      </span>

      <Badge
        variant={selected ? "secondary" : "outline"}
        className="ml-2 h-5 px-2 text-xs"
      >
        {count}
      </Badge>
    </button>
  );
}