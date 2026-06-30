"use client";

import { useState } from "react";
import { Search, UserRound } from "lucide-react";

import KakaoPostcodeButton from "@/components/common/KakaoPostcodeButton";
import { updateHrEmployeeBasicInfoRequest } from "@/services/hrEmployeeService";
import { useAuthStore } from "@/store/authStore";

import DetailSection from "./DetailSection";
import { EditableField, ReadOnlyField } from "./DetailFields";

const EDIT_ALLOWED_ROLE_CODES = ["ADMIN", "HR", "HR_LEAD"];

const createBasicInfoForm = (employee) => ({
  name: employee?.name ?? "",
  phone: employee?.phone ?? "",
  email: employee?.email ?? "",
  zipcode: employee?.zipcode ?? "",
  address: employee?.address ?? "",
  addressDetail: employee?.addressDetail ?? "",
});

const hasAnyRole = (roles, allowedRoles) => {
  return roles?.some((role) => allowedRoles.includes(role));
};

export default function BasicInfoTab({ employee, onEmployeeUpdated }) {
  const userRoles = useAuthStore((state) => state.userInfo?.roles ?? []);
  const canEditBasicInfo = hasAnyRole(userRoles, EDIT_ALLOWED_ROLE_CODES);

  const [basicEditEmployeeId, setBasicEditEmployeeId] = useState(null);
  const [isBasicSaving, setIsBasicSaving] = useState(false);
  const [basicInfoForm, setBasicInfoForm] = useState(createBasicInfoForm(null));

  const isEditMode =
    canEditBasicInfo && basicEditEmployeeId === employee?.employeeId;

  const visibleForm = isEditMode
    ? basicInfoForm
    : createBasicInfoForm(employee);

  const handleStartEdit = () => {
    if (!employee) {
      return;
    }

    if (!canEditBasicInfo) {
      alert("직원 기본정보 수정 권한이 없습니다.");
      return;
    }

    setBasicInfoForm(createBasicInfoForm(employee));
    setBasicEditEmployeeId(employee.employeeId);
  };

  const handleChange = (event) => {
    const { name, value } = event.target;

    setBasicInfoForm((prev) => ({
      ...prev,
      [name]: value,
    }));
  };

  const handlePostcodeComplete = ({ zipcode, address }) => {
    setBasicInfoForm((prev) => ({
      ...prev,
      zipcode,
      address,
    }));
  };

  const handleCancel = () => {
    setBasicInfoForm(createBasicInfoForm(employee));
    setBasicEditEmployeeId(null);
  };

  const handleSave = async () => {
    if (!canEditBasicInfo) {
      alert("직원 기본정보 수정 권한이 없습니다.");
      return;
    }

    if (!employee?.employeeId) {
      alert("직원 정보가 없습니다.");
      return;
    }

    if (!basicInfoForm.name.trim()) {
      alert("이름은 필수입니다.");
      return;
    }

    try {
      setIsBasicSaving(true);

      const updatedEmployee = await updateHrEmployeeBasicInfoRequest(
        employee.employeeId,
        {
          name: basicInfoForm.name.trim(),
          phone: basicInfoForm.phone.trim(),
          email: basicInfoForm.email.trim(),
          zipcode: basicInfoForm.zipcode.trim(),
          address: basicInfoForm.address.trim(),
          addressDetail: basicInfoForm.addressDetail.trim(),
        },
      );

      onEmployeeUpdated?.(updatedEmployee);
      setBasicEditEmployeeId(null);
      setBasicInfoForm(createBasicInfoForm(updatedEmployee));

      alert("기본정보가 수정되었습니다.");
    } catch (error) {
      console.error(error);
      alert(error.message || "기본정보 수정 중 오류가 발생했습니다.");
    } finally {
      setIsBasicSaving(false);
    }
  };

  return (
    <DetailSection
      title="기본정보"
      icon={<UserRound size={16} />}
      action={
        canEditBasicInfo ? (
          isEditMode ? (
            <div className="flex gap-2">
              <button
                type="button"
                onClick={handleSave}
                disabled={isBasicSaving}
                className="h-9 rounded-md bg-[#1a2f4e] px-4 text-sm font-semibold text-white hover:bg-[#25476e] disabled:cursor-not-allowed disabled:opacity-50"
              >
                {isBasicSaving ? "저장 중..." : "저장"}
              </button>

              <button
                type="button"
                onClick={handleCancel}
                disabled={isBasicSaving}
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
      <div className="grid grid-cols-[1.2fr_1fr_1fr] gap-4">
        <EditableField
          label="이름"
          name="name"
          value={visibleForm.name}
          onChange={handleChange}
          readOnly={!isEditMode}
        />

        <ReadOnlyField label="사번" value={employee.employeeNo} />

        <ReadOnlyField label="로그인 ID" value={employee.loginId} />
      </div>

      <div className="mt-4 grid grid-cols-2 gap-4">
        <EditableField
          label="전화번호"
          name="phone"
          value={visibleForm.phone}
          onChange={handleChange}
          readOnly={!isEditMode}
        />

        <EditableField
          label="이메일"
          name="email"
          value={visibleForm.email}
          onChange={handleChange}
          readOnly={!isEditMode}
        />
      </div>

      <div className="mt-4 grid grid-cols-[180px_1fr] gap-4">
        <ZipcodeField
          value={visibleForm.zipcode}
          onChange={handleChange}
          onPostcodeComplete={handlePostcodeComplete}
          readOnly={!isEditMode}
          disabled={isBasicSaving}
        />

        <EditableField
          label="주소"
          name="address"
          value={visibleForm.address}
          onChange={handleChange}
          readOnly={!isEditMode}
        />
      </div>

      <div className="mt-4">
        <EditableField
          label="상세주소"
          name="addressDetail"
          value={visibleForm.addressDetail}
          onChange={handleChange}
          readOnly={!isEditMode}
        />
      </div>
    </DetailSection>
  );
}

function ZipcodeField({
  value,
  onChange,
  onPostcodeComplete,
  readOnly,
  disabled,
}) {
  return (
    <label className="block">
      <span className="mb-2 block text-xs font-medium text-slate-500">
        우편번호
      </span>

      <div className="flex gap-2">
        <input
          type="text"
          name="zipcode"
          value={value ?? ""}
          onChange={onChange}
          readOnly={readOnly}
          className={[
            "h-10 w-full rounded-md border px-3 text-sm text-slate-800 outline-none",
            readOnly
              ? "cursor-default border-slate-200 bg-slate-50 font-medium"
              : "border-slate-300 bg-white focus:border-[#1a2f4e] focus:ring-2 focus:ring-[#a7f3ff]/40",
          ].join(" ")}
        />

        {!readOnly && (
          <KakaoPostcodeButton
            onComplete={onPostcodeComplete}
            disabled={disabled}
            className="flex h-10 w-10 shrink-0 items-center justify-center rounded-md border border-slate-300 bg-white p-0 text-slate-600 hover:bg-slate-50 disabled:cursor-not-allowed disabled:opacity-50"
          >
            <Search size={16} />
          </KakaoPostcodeButton>
        )}
      </div>
    </label>
  );
}