"use client";

import { useState } from "react";
import { Eye, EyeOff, LockKeyhole } from "lucide-react";

import {
  findHrEmployeeSensitiveInfoRequest,
  updateHrEmployeeSensitiveInfoRequest,
} from "@/services/hrEmployeeService";

import DetailSection from "./DetailSection";
import { ReadOnlyField } from "./DetailFields";
import { BANK_OPTIONS } from "./employeeDetailConstants";

const createSensitiveInfoForm = (sensitiveInfo) => ({
  bankName: sensitiveInfo?.bankName ?? "",
  accountNumber: sensitiveInfo?.accountNumber ?? "",
  accountHolder: sensitiveInfo?.accountHolder ?? "",
  rrn: sensitiveInfo?.rrn ?? "",
});

export default function SensitiveInfoSection({ employee, onEmployeeUpdated }) {
  const [editEmployeeId, setEditEmployeeId] = useState(null);
  const [isSaving, setIsSaving] = useState(false);
  const [isLoadingSensitiveInfo, setIsLoadingSensitiveInfo] = useState(false);
  const [isRevealed, setIsRevealed] = useState(false);
  const [revealedInfo, setRevealedInfo] = useState(null);
  const [form, setForm] = useState(createSensitiveInfoForm(null));

  const isEditMode = editEmployeeId === employee?.employeeId;

  const displayInfo =
    isRevealed && revealedInfo
      ? revealedInfo
      : {
          bankName: employee?.bankName,
          accountNumber: employee?.accountNumber,
          accountHolder: employee?.accountHolder,
          rrn: employee?.rrn,
        };

  const handleToggleReveal = async () => {
    if (!employee?.employeeId) {
      alert("직원 정보가 없습니다.");
      return;
    }

    if (isRevealed) {
      setIsRevealed(false);
      return;
    }

    try {
      setIsLoadingSensitiveInfo(true);

      const sensitiveInfo = await findHrEmployeeSensitiveInfoRequest(
        employee.employeeId,
      );

      setRevealedInfo(sensitiveInfo);
      setIsRevealed(true);
    } catch (error) {
      console.error(error);
      alert(error.message || "민감정보 조회 중 오류가 발생했습니다.");
    } finally {
      setIsLoadingSensitiveInfo(false);
    }
  };

  const handleStartEdit = async () => {
    if (!employee?.employeeId) {
      alert("직원 정보가 없습니다.");
      return;
    }

    try {
      setIsLoadingSensitiveInfo(true);

      const sensitiveInfo =
        revealedInfo ??
        (await findHrEmployeeSensitiveInfoRequest(employee.employeeId));

      setRevealedInfo(sensitiveInfo);
      setForm(createSensitiveInfoForm(sensitiveInfo));
      setIsRevealed(true);
      setEditEmployeeId(employee.employeeId);
    } catch (error) {
      console.error(error);
      alert(error.message || "민감정보 조회 중 오류가 발생했습니다.");
    } finally {
      setIsLoadingSensitiveInfo(false);
    }
  };

  const handleCancel = () => {
    setForm(createSensitiveInfoForm(revealedInfo));
    setEditEmployeeId(null);
  };

  const handleChange = (event) => {
    const { name, value } = event.target;

    setForm((prev) => ({
      ...prev,
      [name]: value,
    }));
  };

  const handleSave = async () => {
    if (!employee?.employeeId) {
      alert("직원 정보가 없습니다.");
      return;
    }

    if (!form.bankName) {
      alert("은행명을 선택해주세요.");
      return;
    }

    if (!form.accountNumber.trim()) {
      alert("계좌번호를 입력해주세요.");
      return;
    }

    if (!form.accountHolder.trim()) {
      alert("예금주를 입력해주세요.");
      return;
    }

    if (!form.rrn.trim()) {
      alert("주민등록번호를 입력해주세요.");
      return;
    }

    try {
      setIsSaving(true);

      const updatedSensitiveInfo = await updateHrEmployeeSensitiveInfoRequest(
        employee.employeeId,
        {
          bankName: form.bankName,
          accountNumber: form.accountNumber.trim(),
          accountHolder: form.accountHolder.trim(),
          rrn: form.rrn.trim(),
        },
      );

      setRevealedInfo(updatedSensitiveInfo);
      setIsRevealed(false);
      setEditEmployeeId(null);
      setForm(createSensitiveInfoForm(updatedSensitiveInfo));

      onEmployeeUpdated?.({
        ...employee,
        bankName: updatedSensitiveInfo.bankName,
        accountNumber: maskAccountNumber(updatedSensitiveInfo.accountNumber),
        accountHolder: updatedSensitiveInfo.accountHolder,
        rrn: maskRrn(updatedSensitiveInfo.rrn),
      });

      alert("민감정보가 수정되었습니다.");
    } catch (error) {
      console.error(error);
      alert(error.message || "민감정보 수정 중 오류가 발생했습니다.");
    } finally {
      setIsSaving(false);
    }
  };

  return (
    <DetailSection
      title="민감정보"
      icon={<LockKeyhole size={16} />}
      action={
        isEditMode ? (
          <div className="flex gap-2">
            <button
              type="button"
              onClick={handleSave}
              disabled={isSaving}
              className="rounded-md bg-slate-900 px-3 py-1.5 text-sm font-medium text-white disabled:opacity-50"
            >
              {isSaving ? "저장 중..." : "저장"}
            </button>

            <button
              type="button"
              onClick={handleCancel}
              disabled={isSaving}
              className="rounded-md border border-slate-300 bg-white px-3 py-1.5 text-sm font-medium text-slate-700 disabled:opacity-50"
            >
              취소
            </button>
          </div>
        ) : (
          <div className="flex gap-2">
            <button
              type="button"
              onClick={handleToggleReveal}
              disabled={isLoadingSensitiveInfo}
              className="inline-flex items-center gap-1 rounded-md border border-slate-300 bg-white px-3 py-1.5 text-sm font-medium text-slate-700 hover:bg-slate-50 disabled:opacity-50"
            >
              {isRevealed ? <EyeOff size={15} /> : <Eye size={15} />}
              {isLoadingSensitiveInfo
                ? "조회 중..."
                : isRevealed
                  ? "숨김"
                  : "보기"}
            </button>

            <button
              type="button"
              onClick={handleStartEdit}
              disabled={isLoadingSensitiveInfo}
              className="rounded-md border border-slate-300 bg-white px-3 py-1.5 text-sm font-medium text-slate-700 hover:bg-slate-50 disabled:opacity-50"
            >
              수정
            </button>
          </div>
        )
      }
    >
      {isEditMode ? (
        <div className="grid grid-cols-2 gap-4">
          <SelectField
            label="은행명"
            name="bankName"
            value={form.bankName}
            onChange={handleChange}
          >
            <option value="">은행 선택</option>
            {BANK_OPTIONS.map((bankName) => (
              <option key={bankName} value={bankName}>
                {bankName}
              </option>
            ))}
          </SelectField>

          <InputField
            label="예금주"
            name="accountHolder"
            value={form.accountHolder}
            onChange={handleChange}
            placeholder="예: 홍길동"
          />

          <InputField
            label="계좌번호"
            name="accountNumber"
            value={form.accountNumber}
            onChange={handleChange}
            placeholder="예: 123-456-789012"
          />

          <InputField
            label="주민등록번호"
            name="rrn"
            value={form.rrn}
            onChange={handleChange}
            placeholder="예: 901212-1234567"
          />
        </div>
      ) : (
        <>
          <div className="grid grid-cols-2 gap-4">
            <ReadOnlyField label="은행명" value={displayInfo.bankName} />
            <ReadOnlyField label="계좌번호" value={displayInfo.accountNumber} />
            <ReadOnlyField label="주민등록번호" value={displayInfo.rrn} />
            <ReadOnlyField label="예금주" value={displayInfo.accountHolder} />
          </div>

          <p className="mt-3 text-xs text-slate-500">
            민감정보는 기본적으로 마스킹되어 표시되며, 권한 있는 사용자만 원본을 조회할 수 있습니다.
          </p>
        </>
      )}
    </DetailSection>
  );
}

function SelectField({ label, name, value, onChange, children }) {
  return (
    <div>
      <label className="mb-2 block text-xs font-medium text-slate-500">
        {label}
      </label>

      <select
        name={name}
        value={value}
        onChange={onChange}
        className="h-10 w-full rounded-md border border-slate-300 bg-white px-3 text-sm text-slate-800 outline-none focus:border-slate-500"
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
  placeholder,
  type = "text",
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
        placeholder={placeholder}
        className="h-10 w-full rounded-md border border-slate-300 bg-white px-3 text-sm text-slate-800 outline-none focus:border-slate-500"
      />
    </div>
  );
}

function maskRrn(rrn) {
  if (!rrn) {
    return "";
  }

  const normalized = String(rrn).trim();

  if (normalized.includes("-")) {
    const [front, back] = normalized.split("-", 2);
    return `${front}-${"*".repeat(back.length)}`;
  }

  if (normalized.length <= 6) {
    return "*".repeat(normalized.length);
  }

  return `${normalized.slice(0, 6)}${"*".repeat(normalized.length - 6)}`;
}

function maskAccountNumber(accountNumber) {
  if (!accountNumber) {
    return "";
  }

  const normalized = String(accountNumber).trim();

  if (normalized.length <= 4) {
    return "*".repeat(normalized.length);
  }

  return `${"*".repeat(normalized.length - 4)}${normalized.slice(-4)}`;
}