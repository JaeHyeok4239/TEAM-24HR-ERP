"use client";

import { useState } from "react";

import { createHrEmployeeRequest } from "@/services/hrEmployeeService";

import { Button } from "@/components/ui/button";
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogHeader,
  DialogTitle,
} from "@/components/ui/dialog";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { X } from "lucide-react";

const INITIAL_FORM = {
  employmentType: "REGULAR",
  loginId: "",
  password: "",
  name: "",
  email: "",
  departmentId: "",
  positionId: "",
};

export default function EmployeeCreateDialog({
  open,
  onOpenChange,
  departmentOptions = [],
  positionOptions = [],
  onCreated,
}) {
  const [form, setForm] = useState(INITIAL_FORM);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [errorMessage, setErrorMessage] = useState("");

  const isDailyEmployee = form.employmentType === "DAILY";

  const resetForm = () => {
    setForm(INITIAL_FORM);
    setErrorMessage("");
    setIsSubmitting(false);
  };

  const closeDialog = () => {
    resetForm();
    onOpenChange(false);
  };

  const handleDialogOpenChange = (nextOpen) => {
    if (nextOpen) {
      onOpenChange(true);
    }

    // nextOpen이 false인 경우는 무시한다.
    // 바깥 클릭, ESC, 기본 닫힘 이벤트로는 모달을 닫지 않기 위함.
  };


  const handleChange = (field, value) => {
    setForm((prev) => {
      const nextForm = {
        ...prev,
        [field]: value,
      };

      if (field === "employmentType" && value === "DAILY") {
        nextForm.departmentId = "";
        nextForm.positionId = "";
      }

      return nextForm;
    });
  };

  const validateForm = () => {
    if (!form.loginId.trim()) {
      return "로그인 ID를 입력해주세요.";
    }

    if (!form.password.trim()) {
      return "임시 비밀번호를 입력해주세요.";
    }

    if (!form.name.trim()) {
      return "이름을 입력해주세요.";
    }

    if (!form.email.trim()) {
      return "이메일을 입력해주세요.";
    }

    if (form.employmentType === "REGULAR") {
      if (!form.departmentId) {
        return "정규직은 부서를 선택해야 합니다.";
      }

      if (!form.positionId) {
        return "정규직은 직급을 선택해야 합니다.";
      }
    }

    return "";
  };

  const handleSubmit = async (event) => {
    event.preventDefault();

    const validationMessage = validateForm();

    if (validationMessage) {
      setErrorMessage(validationMessage);
      return;
    }

    try {
      setIsSubmitting(true);
      setErrorMessage("");

      const createdEmployee = await createHrEmployeeRequest({
        loginId: form.loginId.trim(),
        password: form.password.trim(),
        name: form.name.trim(),
        email: form.email.trim(),
        departmentId: form.departmentId,
        positionId: form.positionId,
        employmentType: form.employmentType,
      });

      onCreated?.(createdEmployee);
      closeDialog();
    } catch (error) {
      setErrorMessage(error.message || "직원 등록에 실패했습니다.");
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <Dialog open={open} onOpenChange={handleDialogOpenChange}>
      <DialogContent
        className="max-h-[88vh] overflow-hidden border-slate-200 bg-white p-0 shadow-2xl sm:max-w-[680px] [&>button]:hidden"
        onInteractOutside={(event) => event.preventDefault()}
        onPointerDownOutside={(event) => event.preventDefault()}
        onFocusOutside={(event) => event.preventDefault()}
        onEscapeKeyDown={(event) => event.preventDefault()}
      >
        <DialogHeader className="relative border-b bg-white px-6 py-5 pr-14">
          <button
            type="button"
            onClick={closeDialog}
            className="absolute right-5 top-5 rounded-md p-1 text-slate-500 transition hover:bg-slate-100 hover:text-slate-900"
            aria-label="닫기"
          >
            <X size={18} />
          </button>

          <DialogTitle className="text-xl font-bold text-slate-900">
            새 직원 등록
          </DialogTitle>

          <DialogDescription className="mt-1 text-sm text-slate-500">
            직원 계정을 생성합니다. 등록된 직원에게는 기본 USER 권한이 부여됩니다.
          </DialogDescription>
        </DialogHeader>

        <form onSubmit={handleSubmit} className="flex max-h-[calc(88vh-92px)] flex-col">
          <div className="flex-1 overflow-y-auto px-6 py-6">
            {errorMessage && (
              <div className="mb-5 rounded-md border border-red-200 bg-red-50 px-3 py-2 text-sm text-red-700">
                {errorMessage}
              </div>
            )}

            <div className="space-y-7">
              <section>
                <SectionTitle
                  title="고용형태"
                  description="일용직은 부서와 직급을 선택하지 않습니다."
                />

                <div className="mt-3 w-[220px]">
                  <NativeSelect
                    value={form.employmentType}
                    onChange={(value) => handleChange("employmentType", value)}
                  >
                    <option value="REGULAR">정규직</option>
                    <option value="DAILY">일용직</option>
                  </NativeSelect>
                </div>
              </section>

              <section>
                <SectionTitle title="계정 정보" />

                <div className="mt-4 grid grid-cols-2 gap-x-5 gap-y-5">
                  <Field label="로그인 ID" required>
                    <Input
                      value={form.loginId}
                      onChange={(event) =>
                        handleChange("loginId", event.target.value)
                      }
                      placeholder="예: honggd"
                      className="h-11 bg-white"
                    />
                  </Field>

                  <Field label="임시 비밀번호" required>
                    <Input
                      type="password"
                      value={form.password}
                      onChange={(event) =>
                        handleChange("password", event.target.value)
                      }
                      placeholder="예: 1234"
                      className="h-11 bg-white"
                    />
                  </Field>

                  <Field label="이름" required>
                    <Input
                      value={form.name}
                      onChange={(event) =>
                        handleChange("name", event.target.value)
                      }
                      placeholder="예: 홍길동"
                      className="h-11 bg-white"
                    />
                  </Field>

                  <Field label="이메일" required>
                    <Input
                      type="email"
                      value={form.email}
                      onChange={(event) =>
                        handleChange("email", event.target.value)
                      }
                      placeholder="예: hong@example.com"
                      className="h-11 bg-white"
                    />
                  </Field>
                </div>
              </section>

              <section className="rounded-lg border border-slate-200 bg-slate-50 p-5">
                <SectionTitle
                  title="소속 정보"
                  description="정규직 직원만 부서와 직급을 선택합니다."
                />

                <div className="mt-4 grid grid-cols-2 gap-x-5 gap-y-5">
                  <Field label="부서" required={!isDailyEmployee}>
                    <NativeSelect
                      value={form.departmentId}
                      disabled={isDailyEmployee}
                      onChange={(value) => handleChange("departmentId", value)}
                    >
                      <option value="">
                        {isDailyEmployee
                          ? "일용직은 선택하지 않음"
                          : "부서 선택"}
                      </option>

                      {departmentOptions.map((department) => (
                        <option
                          key={department.departmentId}
                          value={String(department.departmentId)}
                        >
                          {department.departmentName}
                        </option>
                      ))}
                    </NativeSelect>
                  </Field>

                  <Field label="직급" required={!isDailyEmployee}>
                    <NativeSelect
                      value={form.positionId}
                      disabled={isDailyEmployee}
                      onChange={(value) => handleChange("positionId", value)}
                    >
                      <option value="">
                        {isDailyEmployee
                          ? "일용직은 선택하지 않음"
                          : "직급 선택"}
                      </option>

                      {positionOptions.map((position) => (
                        <option
                          key={position.positionId}
                          value={String(position.positionId)}
                        >
                          {position.positionName}
                        </option>
                      ))}
                    </NativeSelect>
                  </Field>
                </div>
              </section>
            </div>
          </div>

          <div className="flex shrink-0 items-center justify-end gap-2 border-t bg-slate-50 px-6 py-4">
            <Button
              type="button"
              variant="outline"
              onClick={closeDialog}
              disabled={isSubmitting}
            >
              취소
            </Button>

            <Button type="submit" disabled={isSubmitting}>
              {isSubmitting ? "등록 중..." : "등록"}
            </Button>
          </div>
        </form>
      </DialogContent>
    </Dialog>
  );
}

function SectionTitle({ title, description }) {
  return (
    <div>
      <h3 className="text-sm font-semibold text-slate-900">
        {title}
      </h3>

      {description && (
        <p className="mt-1 text-xs text-slate-500">
          {description}
        </p>
      )}
    </div>
  );
}

function Field({ label, required = false, children }) {
  return (
    <div className="space-y-2">
      <Label className="text-sm font-medium text-slate-700">
        {label}
        {required && <span className="ml-1 text-red-500">*</span>}
      </Label>

      {children}
    </div>
  );
}

function NativeSelect({ value, onChange, disabled = false, children }) {
  return (
    <select
      value={value}
      disabled={disabled}
      onChange={(event) => onChange(event.target.value)}
      className={`h-11 w-full rounded-md border border-slate-200 bg-white px-3 text-sm text-slate-900 outline-none transition focus:border-slate-400 focus:ring-2 focus:ring-slate-200 ${disabled ? "cursor-not-allowed bg-slate-100 text-slate-400" : ""
        }`}
    >
      {children}
    </select>
  );
}