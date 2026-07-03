"use client";

import { useState } from "react";
import { UserCog } from "lucide-react";

import { updateHrEmployeeEmploymentInfoRequest } from "@/services/hrEmployeeService";
import { useAuthStore } from "@/store/authStore";

import SensitiveInfoSection from "./SensitiveInfoSection";
import DetailSection from "./DetailSection";
import { ReadOnlyField } from "./DetailFields";
import RoleInfoSection from "./RoleInfoSection";
import {
  EMPLOYMENT_TYPE_LABELS,
  STATUS_LABELS,
} from "./employeeDetailConstants";

const EDIT_ALLOWED_ROLE_CODES = ["ADMIN", "HR", "HR_LEAD"];

const STATUS_OPTIONS = [
  { value: "ACTIVE", label: "재직" },
  { value: "LEAVE", label: "휴직" },
  { value: "RESIGNED", label: "퇴사" },
  { value: "INACTIVE", label: "비활성" },
  { value: "LOCKED", label: "잠김" },
];

const EMPLOYMENT_TYPE_OPTIONS = [
  { value: "REGULAR", label: "정규직" },
  { value: "DAILY", label: "일용직" },
];

const createEmploymentInfoForm = (employee) => ({
  departmentId: employee?.departmentId ? String(employee.departmentId) : "",
  positionId: employee?.positionId ? String(employee.positionId) : "",
  employmentType: employee?.employmentType ?? "REGULAR",
  status: employee?.status ?? "ACTIVE",
  hireDate: formatDate(employee?.hireDate),
  resignationDate: formatDate(employee?.resignationDate),
  changeReason: "",
});

const hasAnyRole = (roles, allowedRoles) => {
  return roles?.some((role) => allowedRoles.includes(role));
};

export default function HrInfoTab({ employee, options, onEmployeeUpdated }) {
  const userRoles = useAuthStore((state) => state.userInfo?.roles ?? []);
  const canEditEmploymentInfo = hasAnyRole(userRoles, EDIT_ALLOWED_ROLE_CODES);

  const [editEmployeeId, setEditEmployeeId] = useState(null);
  const [isSaving, setIsSaving] = useState(false);
  const [form, setForm] = useState(createEmploymentInfoForm(null));

  const isEditMode =
    canEditEmploymentInfo && editEmployeeId === employee?.employeeId;

  const visibleForm = isEditMode ? form : createEmploymentInfoForm(employee);

  const departments = options?.departments ?? [];
  const positions = options?.positions ?? [];

  const handleStartEdit = () => {
    if (!employee?.employeeId) {
      alert("직원 정보가 없습니다.");
      return;
    }

    if (!canEditEmploymentInfo) {
      alert("직원 인사정보 수정 권한이 없습니다.");
      return;
    }

    setForm(createEmploymentInfoForm(employee));
    setEditEmployeeId(employee.employeeId);
  };

  const handleCancel = () => {
    setForm(createEmploymentInfoForm(employee));
    setEditEmployeeId(null);
  };

  const handleChange = (event) => {
    const { name, value } = event.target;

    setForm((prev) => {
      if (name === "employmentType" && value === "DAILY") {
        return {
          ...prev,
          employmentType: value,
          departmentId: "",
          positionId: "",
        };
      }

      if (name === "status" && value !== "RESIGNED") {
        return {
          ...prev,
          status: value,
          resignationDate: "",
        };
      }

      return {
        ...prev,
        [name]: value,
      };
    });
  };

  const handleSave = async () => {
    if (!canEditEmploymentInfo) {
      alert("직원 인사정보 수정 권한이 없습니다.");
      return;
    }

    if (!employee?.employeeId) {
      alert("직원 정보가 없습니다.");
      return;
    }

    if (!form.hireDate) {
      alert("입사일은 필수입니다.");
      return;
    }

    if (form.employmentType === "REGULAR") {
      if (!form.departmentId || !form.positionId) {
        alert("정규직은 부서와 직급이 필요합니다.");
        return;
      }
    }

    if (!form.changeReason.trim()) {
      alert("변경 사유를 입력해주세요.");
      return;
    }

    try {
      setIsSaving(true);

      const updatedEmployee = await updateHrEmployeeEmploymentInfoRequest(
        employee.employeeId,
        {
          departmentId:
            form.employmentType === "DAILY"
              ? null
              : toNullableNumber(form.departmentId),
          positionId:
            form.employmentType === "DAILY"
              ? null
              : toNullableNumber(form.positionId),
          employmentType: form.employmentType,
          status: form.status,
          hireDate: form.hireDate,
          resignationDate:
            form.status === "RESIGNED" && form.resignationDate
              ? form.resignationDate
              : null,
          changeReason: form.changeReason.trim(),
        },
      );

      onEmployeeUpdated?.(updatedEmployee);
      setEditEmployeeId(null);
      setForm(createEmploymentInfoForm(updatedEmployee));

      alert("인사정보가 수정되었습니다.");
    } catch (error) {
      alert(error.message || "인사정보 수정 중 오류가 발생했습니다.");
    } finally {
      setIsSaving(false);
    }
  };

  return (
    <div className="space-y-5">
      <DetailSection
        title="소속 및 상태"
        icon={<UserCog size={16} />}
        action={
          canEditEmploymentInfo ? (
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
        {isEditMode ? (
          <>
            <div className="grid grid-cols-2 gap-4">
              <SelectField
                label="부서"
                name="departmentId"
                value={visibleForm.departmentId}
                onChange={handleChange}
                disabled={visibleForm.employmentType === "DAILY"}
              >
                <option value="">부서 선택</option>
                {departments.map((department) => (
                  <option
                    key={department.departmentId}
                    value={department.departmentId}
                  >
                    {department.departmentName}
                  </option>
                ))}
              </SelectField>

              <SelectField
                label="직급"
                name="positionId"
                value={visibleForm.positionId}
                onChange={handleChange}
                disabled={visibleForm.employmentType === "DAILY"}
              >
                <option value="">직급 선택</option>
                {positions.map((position) => (
                  <option key={position.positionId} value={position.positionId}>
                    {position.positionName}
                  </option>
                ))}
              </SelectField>

              <SelectField
                label="고용형태"
                name="employmentType"
                value={visibleForm.employmentType}
                onChange={handleChange}
              >
                {EMPLOYMENT_TYPE_OPTIONS.map((type) => (
                  <option key={type.value} value={type.value}>
                    {type.label}
                  </option>
                ))}
              </SelectField>

              <SelectField
                label="재직상태"
                name="status"
                value={visibleForm.status}
                onChange={handleChange}
              >
                {STATUS_OPTIONS.map((status) => (
                  <option key={status.value} value={status.value}>
                    {status.label}
                  </option>
                ))}
              </SelectField>

              <InputField
                label="입사일"
                name="hireDate"
                type="date"
                value={visibleForm.hireDate}
                onChange={handleChange}
              />

              <InputField
                label="퇴사일"
                name="resignationDate"
                type="date"
                value={visibleForm.resignationDate}
                onChange={handleChange}
                disabled={visibleForm.status !== "RESIGNED"}
              />
            </div>

            <div className="mt-4">
              <label className="mb-2 block text-xs font-medium text-slate-500">
                변경 사유
              </label>

              <textarea
                name="changeReason"
                value={visibleForm.changeReason}
                onChange={handleChange}
                rows={3}
                placeholder="예: 인사발령으로 인한 부서 변경, 승진, 오입력 정정"
                className="w-full resize-none rounded-md border border-slate-300 bg-white px-3 py-2 text-sm text-slate-800 outline-none focus:border-[#1a2f4e] focus:ring-2 focus:ring-[#a7f3ff]/40"
              />
            </div>
          </>
        ) : (
          <div className="grid grid-cols-2 gap-4">
            <ReadOnlyField label="부서" value={employee.departmentName} />

            <ReadOnlyField label="직급" value={employee.positionName} />

            <ReadOnlyField
              label="고용형태"
              value={
                EMPLOYMENT_TYPE_LABELS[employee.employmentType] ??
                employee.employmentType
              }
            />

            <ReadOnlyField
              label="재직상태"
              value={STATUS_LABELS[employee.status] ?? employee.status}
            />

            <ReadOnlyField
              label="입사일"
              value={formatDate(employee.hireDate)}
            />

            <ReadOnlyField
              label="퇴사일"
              value={formatDate(employee.resignationDate)}
            />
          </div>
        )}
      </DetailSection>

      <RoleInfoSection
        employee={employee}
        onEmployeeUpdated={onEmployeeUpdated}
      />

      <SensitiveInfoSection
        key={employee?.employeeId}
        employee={employee}
        onEmployeeUpdated={onEmployeeUpdated}
      />
    </div>
  );
}

function SelectField({
  label,
  name,
  value,
  onChange,
  disabled = false,
  children,
}) {
  return (
    <div>
      <label className="mb-2 block text-xs font-medium text-slate-500">
        {label}
      </label>

      <select
        name={name}
        value={value}
        onChange={onChange}
        disabled={disabled}
        className="h-10 w-full rounded-md border border-slate-300 bg-white px-3 text-sm text-slate-800 outline-none focus:border-[#1a2f4e] focus:ring-2 focus:ring-[#a7f3ff]/40 disabled:cursor-not-allowed disabled:border-slate-200 disabled:bg-slate-100 disabled:text-slate-500"
      >
        {children}
      </select>
    </div>
  );
}

function InputField({
  label,
  name,
  value,
  onChange,
  type = "text",
  disabled = false,
}) {
  return (
    <div>
      <label className="mb-2 block text-xs font-medium text-slate-500">
        {label}
      </label>

      <input
        name={name}
        type={type}
        value={value}
        onChange={onChange}
        disabled={disabled}
        className="h-10 w-full rounded-md border border-slate-300 bg-white px-3 text-sm text-slate-800 outline-none focus:border-[#1a2f4e] focus:ring-2 focus:ring-[#a7f3ff]/40 disabled:cursor-not-allowed disabled:border-slate-200 disabled:bg-slate-100 disabled:text-slate-500"
      />
    </div>
  );
}

function toNullableNumber(value) {
  if (!value) {
    return null;
  }

  return Number(value);
}

function formatDate(value) {
  if (!value) {
    return "";
  }

  return String(value).slice(0, 10);
}
