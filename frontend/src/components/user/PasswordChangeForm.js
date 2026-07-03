"use client";

import { useRouter } from "next/navigation";
import { useState } from "react";
import {
  AlertTriangle,
  CheckCircle2,
  KeyRound,
  LockKeyhole,
  ShieldCheck,
} from "lucide-react";

import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { changePasswordRequest } from "@/services/userService";
import { useAuthStore } from "@/store/authStore";

const createInitialForm = () => ({
  currentPassword: "",
  newPassword: "",
  confirmPassword: "",
});

const createEmptyErrors = () => ({
  currentPassword: "",
  newPassword: "",
  confirmPassword: "",
});

const createEmptyNotice = () => ({
  type: "",
  message: "",
});

const validateCurrentPassword = (password) => {
  if (!password.trim()) {
    return "현재 비밀번호를 입력해주세요.";
  }

  return "";
};

const validateNewPassword = (password, currentPassword) => {
  if (!password) {
    return "새 비밀번호를 입력해주세요.";
  }

  if (password.length < 8 || password.length > 20) {
    return "새 비밀번호는 8자 이상 20자 이하로 입력해주세요.";
  }

  const passwordPattern =
    /^(?=.*[A-Za-z])(?=.*\d)(?=.*[@$!%*#?&])[A-Za-z\d@$!%*#?&]+$/;

  if (!passwordPattern.test(password)) {
    return "영문, 숫자, 특수문자를 모두 포함해주세요.";
  }

  if (password === currentPassword) {
    return "새 비밀번호는 현재 비밀번호와 다르게 입력해주세요.";
  }

  return "";
};

const validateConfirmPassword = (confirmPassword, newPassword) => {
  if (!confirmPassword) {
    return "새 비밀번호를 다시 입력해주세요.";
  }

  if (confirmPassword !== newPassword) {
    return "새 비밀번호가 일치하지 않습니다.";
  }

  return "";
};

export default function PasswordChangeForm() {
  const router = useRouter();

  const logout = useAuthStore((state) => state.logout);
  const userInfo = useAuthStore((state) => state.userInfo);
  const isFirstLogin = userInfo?.isFirstLogin === "Y";

  const [form, setForm] = useState(createInitialForm);
  const [errors, setErrors] = useState(createEmptyErrors);
  const [notice, setNotice] = useState(createEmptyNotice);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [isCompleted, setIsCompleted] = useState(false);

  const handleChange = (event) => {
    const { name, value } = event.target;

    const nextForm = {
      ...form,
      [name]: value,
    };

    setForm(nextForm);
    setNotice(createEmptyNotice());

    if (name === "currentPassword") {
      setErrors((previous) => ({
        ...previous,
        currentPassword: "",
        newPassword: nextForm.newPassword
          ? validateNewPassword(nextForm.newPassword, nextForm.currentPassword)
          : previous.newPassword,
      }));
    }

    if (name === "newPassword") {
      setErrors((previous) => ({
        ...previous,
        newPassword: validateNewPassword(
          nextForm.newPassword,
          nextForm.currentPassword,
        ),
        confirmPassword: nextForm.confirmPassword
          ? validateConfirmPassword(
              nextForm.confirmPassword,
              nextForm.newPassword,
            )
          : previous.confirmPassword,
      }));
    }

    if (name === "confirmPassword") {
      setErrors((previous) => ({
        ...previous,
        confirmPassword: validateConfirmPassword(
          nextForm.confirmPassword,
          nextForm.newPassword,
        ),
      }));
    }
  };

  const handleCancel = () => {
    setForm(createInitialForm());
    setErrors(createEmptyErrors());
    setNotice(createEmptyNotice());
  };

  const handleSubmit = async (event) => {
    event.preventDefault();

    const nextErrors = {
      currentPassword: validateCurrentPassword(form.currentPassword),
      newPassword: validateNewPassword(form.newPassword, form.currentPassword),
      confirmPassword: validateConfirmPassword(
        form.confirmPassword,
        form.newPassword,
      ),
    };

    setErrors(nextErrors);

    const hasError = Object.values(nextErrors).some((error) => Boolean(error));

    if (hasError) {
      setNotice({
        type: "error",
        message:
          "형식이 잘못 입력된 정보가 있습니다. 입력 항목을 확인해주세요.",
      });

      return;
    }

    try {
      setIsSubmitting(true);
      setNotice(createEmptyNotice());

      await changePasswordRequest({
        currentPassword: form.currentPassword,
        newPassword: form.newPassword,
      });

      setForm(createInitialForm());
      setErrors(createEmptyErrors());
      setIsCompleted(true);

      setNotice({
        type: "success",
        message: "비밀번호가 변경되었습니다. 다시 로그인해주세요.",
      });

      setTimeout(() => {
        logout();
        router.replace("/");
      }, 1200);
    } catch (error) {
      setNotice({
        type: "error",
        message:
          "비밀번호 변경에 실패했습니다. 현재 비밀번호와 입력 형식을 확인해주세요.",
      });
    } finally {
      setIsSubmitting(false);
    }
  };

  const isFormEmpty =
    !form.currentPassword || !form.newPassword || !form.confirmPassword;

  return (
    <div className="flex min-h-[calc(100vh-128px)] w-full items-center justify-center">
      <form
        onSubmit={handleSubmit}
        noValidate
        className="w-full overflow-hidden rounded-lg border border-slate-200 bg-white shadow-sm"
      >
        <div className="grid grid-cols-1 xl:grid-cols-[340px_minmax(0,1fr)]">
          <aside className="border-b border-slate-200 bg-[#1a2f4e] p-6 text-white xl:border-b-0 xl:border-r">
            <div className="flex h-full flex-col justify-between gap-8">
              <div>
                <div className="flex h-12 w-12 items-center justify-center rounded-lg bg-white/10 text-cyan-100">
                  <LockKeyhole size={25} strokeWidth={1.8} />
                </div>

                <h2 className="mt-5 text-xl font-bold">계정 보안 설정</h2>

                <p className="mt-3 text-sm leading-6 text-cyan-100/70">
                  안전한 계정 사용을 위해 주기적으로 비밀번호를 변경해주세요.
                  변경 후에는 보안을 위해 다시 로그인합니다.
                </p>
              </div>

              <div className="rounded-lg border border-white/10 bg-white/5 p-4">
                <div className="flex items-center gap-2 text-sm font-semibold text-cyan-100">
                  <ShieldCheck size={17} />
                  비밀번호 조건
                </div>

                <ul className="mt-3 space-y-2 text-sm text-cyan-100/70">
                  <PasswordRule>8자 이상 20자 이하</PasswordRule>
                  <PasswordRule>영문, 숫자, 특수문자 포함</PasswordRule>
                  <PasswordRule>현재 비밀번호와 다르게 입력</PasswordRule>
                  <PasswordRule>
                    사용 가능 특수문자: @ $ ! % * # ? &
                  </PasswordRule>
                </ul>
              </div>
            </div>
          </aside>

          <section className="min-w-0 bg-white p-6">
            <div className="mb-6 border-b border-slate-100 pb-5">
              <div className="flex items-center justify-between gap-4">
                <div>
                  <h2 className="text-lg font-bold text-slate-950">
                    비밀번호 변경
                  </h2>

                  <p className="mt-1 text-sm text-slate-500">
                    현재 비밀번호를 확인한 뒤 새 비밀번호를 설정합니다.
                  </p>
                </div>

                <div className="flex h-10 w-10 shrink-0 items-center justify-center rounded-md bg-[#eef7ff] text-[#1a2f4e]">
                  <KeyRound size={20} strokeWidth={1.8} />
                </div>
              </div>
            </div>

            {isFirstLogin && (
              <div className="mb-5 rounded-md border border-amber-200 bg-amber-50 px-4 py-3 text-sm text-amber-800">
                <div className="flex gap-2">
                  <AlertTriangle size={17} className="mt-0.5 shrink-0" />

                  <div>
                    <p className="font-semibold">
                      최초 로그인 비밀번호 변경이 필요합니다.
                    </p>

                    <p className="mt-1 leading-5">
                      임시 비밀번호로 로그인한 상태입니다. 보안을 위해 새
                      비밀번호로 변경해야 시스템을 사용할 수 있습니다.
                    </p>
                  </div>
                </div>
              </div>
            )}

            {notice.message && (
              <Notice type={notice.type} message={notice.message} />
            )}

            <div className="grid gap-5">
              <PasswordField
                label="현재 비밀번호"
                name="currentPassword"
                autoComplete="current-password"
                placeholder="현재 비밀번호를 입력해주세요."
                value={form.currentPassword}
                onChange={handleChange}
                error={errors.currentPassword}
              />

              <PasswordField
                label="새 비밀번호"
                name="newPassword"
                autoComplete="new-password"
                placeholder="새 비밀번호를 입력해주세요."
                value={form.newPassword}
                onChange={handleChange}
                error={errors.newPassword}
              />

              <PasswordField
                label="새 비밀번호 확인"
                name="confirmPassword"
                autoComplete="new-password"
                placeholder="새 비밀번호를 다시 입력해주세요."
                value={form.confirmPassword}
                onChange={handleChange}
                error={errors.confirmPassword}
              />
            </div>
          </section>
        </div>

        <div className="flex justify-end gap-2 border-t border-slate-200 bg-slate-50 px-6 py-4">
          {!isFirstLogin && (
            <Button
              type="button"
              variant="outline"
              onClick={handleCancel}
              disabled={isSubmitting || isCompleted}
              className="h-9 border-slate-300 bg-white px-5 text-sm text-slate-700 hover:bg-slate-100 hover:text-slate-900 disabled:cursor-not-allowed disabled:border-slate-200 disabled:bg-slate-100 disabled:text-slate-400"
            >
              취소
            </Button>
          )}

          <Button
            type="submit"
            disabled={isSubmitting || isCompleted || isFormEmpty}
            className="h-9 bg-[#1a2f4e] px-6 text-sm font-semibold text-white hover:bg-[#25476e] disabled:cursor-not-allowed disabled:bg-slate-300 disabled:text-slate-500"
          >
            {isSubmitting
              ? "변경 중..."
              : isCompleted
                ? "변경 완료"
                : "비밀번호 변경"}
          </Button>
        </div>
      </form>
    </div>
  );
}

function PasswordRule({ children }) {
  return (
    <li className="flex gap-2">
      <CheckCircle2 size={15} className="mt-0.5 shrink-0 text-cyan-100/80" />
      <span>{children}</span>
    </li>
  );
}

function PasswordField({
  label,
  name,
  autoComplete,
  placeholder,
  value,
  onChange,
  error,
}) {
  const errorId = `${name}-error`;

  const inputClassName = [
    "h-10 bg-white text-sm",
    error
      ? "border-red-400 focus-visible:ring-red-200"
      : "border-slate-200 focus-visible:ring-[#a7f3ff]",
  ].join(" ");

  return (
    <div className="space-y-2">
      <Label htmlFor={name} className="text-sm font-semibold text-slate-700">
        {label}
      </Label>

      <Input
        id={name}
        type="password"
        name={name}
        autoComplete={autoComplete}
        placeholder={placeholder}
        maxLength={20}
        value={value}
        onChange={onChange}
        aria-invalid={Boolean(error)}
        aria-describedby={error ? errorId : undefined}
        className={inputClassName}
      />

      {error && (
        <p id={errorId} className="text-xs font-medium text-red-500">
          {error}
        </p>
      )}
    </div>
  );
}

function Notice({ type, message }) {
  const isSuccess = type === "success";

  const noticeClassName = [
    "mb-5 rounded-md border px-4 py-3 text-sm font-medium",
    isSuccess
      ? "border-emerald-200 bg-emerald-50 text-emerald-700"
      : "border-red-200 bg-red-50 text-red-700",
  ].join(" ");

  return (
    <div role="alert" aria-live="polite" className={noticeClassName}>
      {message}
    </div>
  );
}
