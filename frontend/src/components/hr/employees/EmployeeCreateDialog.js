"use client";

import { useState } from "react";
import { X } from "lucide-react";

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

const createInitialForm = () => ({
  employmentType: "REGULAR",
  loginId: "",
  password: "",
  name: "",
  email: "",
  departmentId: "",
  positionId: "",
});

const INPUT_CLASS_NAME =
  "h-10 border-slate-300 bg-white text-sm text-slate-900 focus-visible:ring-[#a7f3ff]";

export default function EmployeeCreateDialog({
  open,
  onOpenChange,
  departmentOptions = [],
  positionOptions = [],
  onCreated,
}) {
  const [form, setForm] = useState(createInitialForm);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [errorMessage, setErrorMessage] = useState("");

  const isDailyEmployee = form.employmentType === "DAILY";

  const resetForm = () => {
    setForm(createInitialForm());
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
    if (!form.name.trim()) {
      return "이름을 입력해주세요.";
    }

    if (!form.email.trim()) {
      return "이메일을 입력해주세요.";
    }

    if (!form.loginId.trim()) {
      return "로그인 ID를 입력해주세요.";
    }

    if (!form.password.trim()) {
      return "임시 비밀번호를 입력해주세요.";
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
        departmentId: isDailyEmployee
          ? null
          : toNullableNumber(form.departmentId),
        positionId: isDailyEmployee ? null : toNullableNumber(form.positionId),
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
        className="flex max-h-[92vh] flex-col overflow-hidden border-slate-200 bg-white p-0 shadow-2xl sm:max-w-[680px] [&>button]:hidden"
        onInteractOutside={(event) => event.preventDefault()}
        onPointerDownOutside={(event) => event.preventDefault()}
        onFocusOutside={(event) => event.preventDefault()}
        onEscapeKeyDown={(event) => event.preventDefault()}
      >
        <DialogHeader className="relative shrink-0 border-b bg-white px-6 py-4 pr-14">
          <button
            type="button"
            onClick={closeDialog}
            className="absolute right-5 top-4 rounded-md p-1 text-slate-500 transition hover:bg-slate-100 hover:text-slate-900"
            aria-label="닫기"
          >
            <X size={18} />
          </button>

          <DialogTitle className="text-xl font-bold text-slate-900">
            새 직원 등록
          </DialogTitle>

          <DialogDescription className="mt-1 text-sm text-slate-500">
            사번 자동 생성 · 기본 USER 권한 부여
          </DialogDescription>
        </DialogHeader>

        <form onSubmit={handleSubmit} className="flex min-h-0 flex-1 flex-col">
          <div className="min-h-0 flex-1 overflow-y-auto px-6 py-5">
            {errorMessage && (
              <div className="mb-4 rounded-md border border-red-200 bg-red-50 px-3 py-2 text-sm text-red-700">
                {errorMessage}
              </div>
            )}

            <div className="space-y-5">
              <section className="rounded-lg border border-slate-200 bg-slate-50 p-4">
                <SectionTitle title="고용형태" />

                <div className="mt-3">
                  <EmploymentTypeToggle
                    value={form.employmentType}
                    onChange={(value) => handleChange("employmentType", value)}
                  />
                </div>
              </section>

              <section>
                <SectionTitle title="계정 정보" />

                <div className="mt-3 grid grid-cols-2 gap-x-5 gap-y-4">
                  <Field label="이름" required>
                    <Input
                      value={form.name}
                      onChange={(event) =>
                        handleChange("name", event.target.value)
                      }
                      placeholder="예: 홍길동"
                      className={INPUT_CLASS_NAME}
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
                      className={INPUT_CLASS_NAME}
                    />
                  </Field>

                  <Field label="로그인 ID" required>
                    <Input
                      value={form.loginId}
                      onChange={(event) =>
                        handleChange("loginId", event.target.value)
                      }
                      placeholder="예: honggd"
                      className={INPUT_CLASS_NAME}
                    />
                  </Field>

                  <Field label="임시 비밀번호" required>
                    <Input
                      type="password"
                      value={form.password}
                      onChange={(event) =>
                        handleChange("password", event.target.value)
                      }
                      placeholder="예: Temp@1234"
                      className={INPUT_CLASS_NAME}
                    />
                  </Field>
                </div>
              </section>

              <section className="rounded-lg border border-slate-200 bg-slate-50 p-4">
                <SectionTitle title="소속 정보" />

                <div className="mt-3 grid grid-cols-2 gap-x-5 gap-y-4">
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

          <div className="flex shrink-0 items-center justify-end gap-2 border-t bg-slate-50 px-6 py-3">
            <Button
              type="button"
              variant="outline"
              onClick={closeDialog}
              disabled={isSubmitting}
              className="h-9 px-4 text-sm font-medium"
            >
              취소
            </Button>

            <Button
              type="submit"
              disabled={isSubmitting}
              className="h-9 bg-[#1a2f4e] px-5 text-sm font-semibold text-white hover:bg-[#25476e] disabled:cursor-not-allowed disabled:opacity-50"
            >
              {isSubmitting ? "등록 중..." : "등록"}
            </Button>
          </div>
        </form>
      </DialogContent>
    </Dialog>
  );
}

function EmploymentTypeToggle({ value, onChange }) {
  return (
    <div className="grid grid-cols-2 gap-2">
      <button
        type="button"
        onClick={() => onChange("REGULAR")}
        className={[
          "h-10 rounded-md border text-sm font-semibold transition",
          value === "REGULAR"
            ? "border-[#1a2f4e] bg-[#1a2f4e] text-white shadow-sm"
            : "border-slate-300 bg-white text-slate-700 hover:bg-slate-50",
        ].join(" ")}
      >
        정규직
      </button>

      <button
        type="button"
        onClick={() => onChange("DAILY")}
        className={[
          "h-10 rounded-md border text-sm font-semibold transition",
          value === "DAILY"
            ? "border-[#1a2f4e] bg-[#1a2f4e] text-white shadow-sm"
            : "border-slate-300 bg-white text-slate-700 hover:bg-slate-50",
        ].join(" ")}
      >
        일용직
      </button>
    </div>
  );
}

function SectionTitle({ title }) {
  return (
    <h3 className="text-sm font-semibold text-slate-900">
      {title}
    </h3>
  );
}

function Field({ label, required = false, children }) {
  return (
    <div className="space-y-1.5">
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
      className={[
        "h-10 w-full rounded-md border px-3 text-sm outline-none transition",
        disabled
          ? "cursor-not-allowed border-slate-200 bg-slate-100 text-slate-400"
          : "border-slate-300 bg-white text-slate-900 focus:border-[#1a2f4e] focus:ring-2 focus:ring-[#a7f3ff]/40",
      ].join(" ")}
    >
      {children}
    </select>
  );
}

function toNullableNumber(value) {
  if (!value) {
    return null;
  }

  return Number(value);
}