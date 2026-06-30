"use client";

import { useState } from "react";
import { ShieldCheck } from "lucide-react";

import { updateHrEmployeeRolesRequest } from "@/services/hrEmployeeService";
import { useAuthStore } from "@/store/authStore";

import DetailSection from "./DetailSection";
import { ROLE_ITEMS } from "./employeeDetailConstants";

const DEFAULT_ROLE_CODE = "USER";
const EDIT_ALLOWED_ROLE_CODES = ["ADMIN", "HR_LEAD"];

const createRoleForm = (employee) => ({
  roleCodes: normalizeRoleCodes(employee?.roles ?? [DEFAULT_ROLE_CODE]),
  changeReason: "",
});

const hasAnyRole = (roles, allowedRoles) => {
  return roles?.some((role) => allowedRoles.includes(role));
};

export default function RoleInfoSection({ employee, onEmployeeUpdated }) {
  const userRoles = useAuthStore((state) => state.userInfo?.roles ?? []);
  const canEditRoleInfo = hasAnyRole(userRoles, EDIT_ALLOWED_ROLE_CODES);

  const [editEmployeeId, setEditEmployeeId] = useState(null);
  const [isSaving, setIsSaving] = useState(false);
  const [form, setForm] = useState(createRoleForm(null));

  const isEditMode = canEditRoleInfo && editEmployeeId === employee?.employeeId;

  const employeeRoles = normalizeRoleCodes(
    employee?.roles ?? [DEFAULT_ROLE_CODE],
  );

  const visibleRoleCodes = isEditMode ? form.roleCodes : employeeRoles;

  const handleStartEdit = () => {
    if (!employee?.employeeId) {
      alert("직원 정보가 없습니다.");
      return;
    }

    if (!canEditRoleInfo) {
      alert("접근 권한 수정 권한이 없습니다.");
      return;
    }

    setForm(createRoleForm(employee));
    setEditEmployeeId(employee.employeeId);
  };

  const handleCancel = () => {
    setForm(createRoleForm(employee));
    setEditEmployeeId(null);
  };

  const handleRoleChange = (roleCode) => {
    if (!isEditMode) {
      return;
    }

    if (roleCode === DEFAULT_ROLE_CODE) {
      return;
    }

    setForm((prev) => {
      const checked = prev.roleCodes.includes(roleCode);

      const nextRoleCodes = checked
        ? prev.roleCodes.filter((code) => code !== roleCode)
        : [...prev.roleCodes, roleCode];

      return {
        ...prev,
        roleCodes: normalizeRoleCodes(nextRoleCodes),
      };
    });
  };

  const handleReasonChange = (event) => {
    const { value } = event.target;

    setForm((prev) => ({
      ...prev,
      changeReason: value,
    }));
  };

  const handleSave = async () => {
    if (!canEditRoleInfo) {
      alert("접근 권한 수정 권한이 없습니다.");
      return;
    }

    if (!employee?.employeeId) {
      alert("직원 정보가 없습니다.");
      return;
    }

    if (!form.changeReason.trim()) {
      alert("변경 사유를 입력해주세요.");
      return;
    }

    try {
      setIsSaving(true);

      const updatedEmployee = await updateHrEmployeeRolesRequest(
        employee.employeeId,
        {
          roleCodes: normalizeRoleCodes(form.roleCodes),
          changeReason: form.changeReason.trim(),
        },
      );

      onEmployeeUpdated?.(updatedEmployee);
      setEditEmployeeId(null);
      setForm(createRoleForm(updatedEmployee));

      alert("접근 권한이 수정되었습니다.");
    } catch (error) {
      console.error(error);
      alert(error.message || "접근 권한 수정 중 오류가 발생했습니다.");
    } finally {
      setIsSaving(false);
    }
  };

  return (
    <DetailSection
      title="접근 권한"
      icon={<ShieldCheck size={16} />}
      action={
        canEditRoleInfo ? (
          isEditMode ? (
            <div className="flex gap-2">
              <button
                type="button"
                onClick={handleSave}
                disabled={isSaving}
                className="h-9 rounded-md bg-[#1a2f4e] px-4 text-sm font-semibold text-white hover:bg-[#25476e] disabled:cursor-not-allowed disabled:opacity-50"
              >
                {isSaving ? "저장 중..." : "저장"}
              </button>

              <button
                type="button"
                onClick={handleCancel}
                disabled={isSaving}
                className="h-9 rounded-md border border-slate-300 bg-white px-4 text-sm font-medium text-slate-700 hover:bg-slate-50 disabled:cursor-not-allowed disabled:opacity-50"
              >
                취소
              </button>
            </div>
          ) : (
            <button
              type="button"
              onClick={handleStartEdit}
              className="h-9 rounded-md border border-slate-300 bg-white px-4 text-sm font-medium text-slate-700 hover:bg-slate-50"
            >
              수정
            </button>
          )
        ) : null
      }
    >
      <div className="grid grid-cols-3 gap-3">
        {ROLE_ITEMS.map((role) => {
          const checked = visibleRoleCodes.includes(role.code);
          const disabled = !isEditMode || role.code === DEFAULT_ROLE_CODE;

          return (
            <label
              key={role.code}
              className={[
                "flex min-h-[74px] gap-3 rounded-md border px-3 py-3 text-sm transition",
                checked
                  ? "border-[#1a2f4e] bg-[#eef7ff]"
                  : "border-slate-200 bg-white text-slate-500",
                disabled
                  ? "cursor-default"
                  : "cursor-pointer hover:border-[#1a2f4e] hover:bg-[#f6fbff]",
              ].join(" ")}
            >
              <input
                type="checkbox"
                checked={checked}
                disabled={disabled}
                onChange={() => handleRoleChange(role.code)}
                className="mt-0.5 h-4 w-4 shrink-0 accent-[#1a2f4e] disabled:cursor-not-allowed"
              />

              <span className="min-w-0">
                <span
                  className={[
                    "block font-semibold",
                    checked ? "text-[#1a2f4e]" : "text-slate-800",
                  ].join(" ")}
                >
                  {role.label}
                </span>

                <span className="mt-1 block text-xs leading-4 text-slate-500">
                  {role.description}
                </span>
              </span>
            </label>
          );
        })}
      </div>

      {isEditMode ? (
        <div className="mt-4">
          <label className="mb-2 block text-xs font-medium text-slate-500">
            변경 사유
          </label>

          <textarea
            value={form.changeReason}
            onChange={handleReasonChange}
            rows={3}
            placeholder="예: 근태 관리 업무 담당으로 권한 부여"
            className="w-full resize-none rounded-md border border-slate-300 bg-white px-3 py-2 text-sm text-slate-800 outline-none focus:border-[#1a2f4e] focus:ring-2 focus:ring-[#a7f3ff]/40"
          />

          <p className="mt-2 text-xs text-slate-500">
            일반 사용자 권한은 기본 권한이라 해제할 수 없습니다.
          </p>
        </div>
      ) : (
        <p className="mt-3 text-xs text-slate-500">
          접근 권한은 시스템 메뉴와 기능 접근 범위를 결정합니다.
        </p>
      )}
    </DetailSection>
  );
}

function normalizeRoleCodes(roleCodes) {
  const uniqueRoleCodes = new Set();

  uniqueRoleCodes.add(DEFAULT_ROLE_CODE);

  roleCodes.forEach((roleCode) => {
    if (!roleCode) {
      return;
    }

    uniqueRoleCodes.add(roleCode);
  });

  return Array.from(uniqueRoleCodes);
}