import { Building2, ChevronRight, Users } from "lucide-react";

import { Badge } from "@/components/ui/badge";
import { Card, CardContent } from "@/components/ui/card";

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
      <CardContent className="min-h-0 flex-1 overflow-y-auto p-3">
        {isLoading ? (
          <div className="flex h-full items-center justify-center text-sm text-slate-400">
            부서 정보를 불러오는 중...
          </div>
        ) : (
          <div className="space-y-5">
            <DepartmentFilterButton
              selected={isAllRegularSelected}
              label="전체 직원"
              count={totalRegularActiveEmployeeCount}
              icon={<Users size={15} />}
              onClick={onSelectAllEmployees}
            />

            <div className="space-y-2">
              <SectionTitle icon={<Building2 size={14} />} label="조직도" />

              <div className="space-y-1">
                {departments.map((department, index) => (
                  <div
                    key={department.departmentId}
                    className={
                      index > 0 ? "mt-2 border-t border-slate-100 pt-2" : ""
                    }
                  >
                    <DepartmentTreeNode
                      department={department}
                      selectedDepartmentId={selectedDepartmentId}
                      selectedEmploymentType={selectedEmploymentType}
                      onSelectDepartment={onSelectDepartment}
                    />
                  </div>
                ))}
              </div>
            </div>

            <div className="pt-4">
              <SectionTitle icon={<Users size={14} />} label="고용형태" />

              <div className="mt-3">
                <DepartmentFilterButton
                  selected={isDailySelected}
                  label="일용직"
                  count={totalDailyActiveEmployeeCount}
                  icon={<Users size={15} />}
                  onClick={onSelectDailyEmployees}
                />
              </div>
            </div>
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
        className={[
          "flex h-9 w-full items-center justify-between rounded-md border-b border-slate-300 pr-3 text-left text-sm transition-colors",
          selected
            ? "bg-[#eef7ff] text-[#1a2f4e]"
            : "text-slate-700 hover:bg-slate-50",
        ].join(" ")}
        style={{ paddingLeft: `${10 + depth * 18}px` }}
      >
        <span className="flex min-w-0 items-center gap-1.5">
          {hasChildren ? (
            <ChevronRight
              size={13}
              className={[
                "shrink-0",
                selected ? "text-[#1a2f4e]" : "text-slate-400",
              ].join(" ")}
            />
          ) : (
            <span className="w-3.5 shrink-0" />
          )}

          <span
            className={[
              "truncate",
              depth === 0 ? "font-semibold" : "font-medium",
            ].join(" ")}
          >
            {department.departmentName}
          </span>
        </span>

        <CountBadge
          selected={selected}
          count={department.activeEmployeeCount}
        />
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

function DepartmentFilterButton({ selected, label, count, icon, onClick }) {
  return (
    <button
      type="button"
      onClick={onClick}
      className={[
        "flex h-10 w-full items-center justify-between rounded-md px-3 text-left text-sm transition-colors",
        selected
          ? "bg-[#111827] text-white shadow-sm"
          : "border border-slate-100 bg-slate-50 text-slate-700 hover:bg-slate-100",
      ].join(" ")}
    >
      <span className="flex items-center gap-2 font-semibold">
        <span className={selected ? "text-white" : "text-slate-500"}>
          {icon}
        </span>
        {label}
      </span>

      <Badge
        variant="secondary"
        className={[
          "ml-2 h-6 shrink-0 rounded-full px-2.5 text-xs font-bold",
          selected
            ? "bg-white text-[#111827] hover:bg-white"
            : "bg-white text-slate-700",
        ].join(" ")}
      >
        {count}
      </Badge>
    </button>
  );
}

function SectionTitle({ icon, label }) {
  return (
    <div className="flex items-center gap-1.5 px-1 text-xs font-bold text-slate-400">
      {icon}
      <span>{label}</span>
    </div>
  );
}

function CountBadge({ selected, count }) {
  return (
    <Badge
      variant="outline"
      className={[
        "ml-2 h-5 shrink-0 rounded-full border-2 px-2 text-xs font-semibold",
        selected
          ? "border-[#1a2f4e] bg-white text-[#1a2f4e]"
          : "border-slate-300 bg-white text-slate-600",
      ].join(" ")}
    >
      {count}
    </Badge>
  );
}
