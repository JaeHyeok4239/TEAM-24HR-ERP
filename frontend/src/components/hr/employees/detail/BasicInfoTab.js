"use client"

import { useState } from "react";
import { Search, UserRound } from "lucide-react";

import KakaoPostcodeButton from "@/components/common/KakaoPostcodeButton";
import { updateHrEmployeeBasicInfoRequest } from "@/services/hrEmployeeService";

import DetailSection from "./DetailSection";
import { EditableField, ReadOnlyField } from "./DetailFields";

const createBasicInfoForm = (employee) => ({
  name: employee?.name ?? "",
  phone: employee?.phone ?? "",
  email: employee?.email ?? "",
  zipcode: employee?.zipcode ?? "",
  address: employee?.address ?? "",
  addressDetail: employee?.addressDetail ?? "",
});

export default function BasicInfoTab({ employee, onEmployeeUpdated }) {
  const [basicEditEmployeeId, setBasicEditEmployeeId] = useState(null);
  const [isBasicSaving, setIsBasicSaving] = useState(false);
  const [basicInfoForm, setBasicInfoForm] = useState(createBasicInfoForm(null));

  const isEditMode = basicEditEmployeeId === employee?.employeeId;
  const visibleForm = isEditMode
    ? basicInfoForm
    : createBasicInfoForm(employee);

  const handleStartEdit = () => {
    if (!employee) {
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
        isEditMode ? (
          <div className="flex gap-2">
            <button
              type="button"
              onClick={handleSave}
              disabled={isBasicSaving}
              className="rounded-md bg-slate-900 px-3 py-1.5 text-sm font-medium text-white disabled:opacity-50"
            >
              {isBasicSaving ? "저장 중..." : "저장"}
            </button>
            <button
              type="button"
              onClick={handleCancel}
              disabled={isBasicSaving}
              className="rounded-md border border-slate-300 bg-white px-3 py-1.5 text-sm font-medium text-slate-700 disabled:opacity-50"
            >
              취소
            </button>
          </div>
        ) : (
          <button
            type="button"
            onClick={handleStartEdit}
            className="rounded-md border border-slate-300 bg-white px-3 py-1.5 text-sm font-medium text-slate-700 hover:bg-slate-50"
          >
            수정
          </button>
        )
      }
    >
      <div className="grid grid-cols-3 gap-4">
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
      <span className="mb-1 block text-sm font-medium text-slate-700">
        우편번호
      </span>
      <div className="flex gap-2">
        <input
          type="text"
          name="zipcode"
          value={value}
          onChange={onChange}
          readOnly={readOnly}
          className="w-full rounded-md border border-slate-300 px-3 py-2 text-sm outline-none focus:border-slate-500 read-only:bg-slate-50"
        />

        {!readOnly && (
          <KakaoPostcodeButton
            onComplete={onPostcodeComplete}
            disabled={disabled}
            className="flex h-9 w-9 shrink-0 items-center justify-center rounded-md border border-slate-300 bg-white p-0 text-slate-600 hover:bg-slate-50 disabled:cursor-not-allowed disabled:opacity-50"
          >
            <Search size={16} />
          </KakaoPostcodeButton>
        )}
      </div>
    </label>
  );
}
