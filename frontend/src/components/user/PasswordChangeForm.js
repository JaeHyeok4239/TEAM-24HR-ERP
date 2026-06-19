'use client';

import { useRouter } from 'next/navigation';
import { useState } from 'react';

import { changePasswordRequest } from '@/services/userService';
import { useAuthStore } from '@/store/authStore';

const createInitialForm = () => ({
  currentPassword: '',
  newPassword: '',
  confirmPassword: '',
});

const createEmptyErrors = () => ({
  currentPassword: '',
  newPassword: '',
  confirmPassword: '',
});

const createEmptyNotice = () => ({
  type: '',
  message: '',
});

const validateCurrentPassword = (password) => {
  if (!password.trim()) {
    return '현재 비밀번호를 입력해주세요.';
  }

  return '';
};

const validateNewPassword = (password, currentPassword) => {
  if (!password) {
    return '새 비밀번호를 입력해주세요.';
  }

  if (password.length < 8 || password.length > 20) {
    return '새 비밀번호는 8자 이상 20자 이하로 입력해주세요.';
  }

  const passwordPattern =
    /^(?=.*[A-Za-z])(?=.*\d)(?=.*[@$!%*#?&])[A-Za-z\d@$!%*#?&]+$/;

  if (!passwordPattern.test(password)) {
    return '영문, 숫자, 특수문자를 모두 포함해주세요.';
  }

  if (password === currentPassword) {
    return '새 비밀번호는 현재 비밀번호와 다르게 입력해주세요.';
  }

  return '';
};

const validateConfirmPassword = (
  confirmPassword,
  newPassword
) => {
  if (!confirmPassword) {
    return '새 비밀번호를 다시 입력해주세요.';
  }

  if (confirmPassword !== newPassword) {
    return '새 비밀번호가 일치하지 않습니다.';
  }

  return '';
};

export default function PasswordChangeForm() {
  const router = useRouter();

  const logout = useAuthStore((state) => state.logout);

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

    if (name === 'currentPassword') {
      setErrors((previous) => ({
        ...previous,
        currentPassword: '',
        newPassword: nextForm.newPassword
          ? validateNewPassword(
              nextForm.newPassword,
              nextForm.currentPassword
            )
          : previous.newPassword,
      }));
    }

    if (name === 'newPassword') {
      setErrors((previous) => ({
        ...previous,
        newPassword: validateNewPassword(
          nextForm.newPassword,
          nextForm.currentPassword
        ),
        confirmPassword: nextForm.confirmPassword
          ? validateConfirmPassword(
              nextForm.confirmPassword,
              nextForm.newPassword
            )
          : previous.confirmPassword,
      }));
    }

    if (name === 'confirmPassword') {
      setErrors((previous) => ({
        ...previous,
        confirmPassword: validateConfirmPassword(
          nextForm.confirmPassword,
          nextForm.newPassword
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
      currentPassword: validateCurrentPassword(
        form.currentPassword
      ),
      newPassword: validateNewPassword(
        form.newPassword,
        form.currentPassword
      ),
      confirmPassword: validateConfirmPassword(
        form.confirmPassword,
        form.newPassword
      ),
    };

    setErrors(nextErrors);

    const hasError = Object.values(nextErrors).some(
      (error) => Boolean(error)
    );

    if (hasError) {
      setNotice({
        type: 'error',
        message:
          '형식이 잘못 입력된 정보가 있습니다. 입력 항목을 확인해주세요.',
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
        type: 'success',
        message:
          '비밀번호가 변경되었습니다. 다시 로그인해주세요.',
      });

      setTimeout(() => {
        logout();
        router.replace('/');
      }, 1200);
    } catch (error) {
      console.error('비밀번호 변경 실패:', error);

      setNotice({
        type: 'error',
        message:
          '비밀번호 변경에 실패했습니다. 현재 비밀번호와 입력 형식을 확인해주세요.',
      });
    } finally {
      setIsSubmitting(false);
    }
  };

  const isFormEmpty =
    !form.currentPassword ||
    !form.newPassword ||
    !form.confirmPassword;

  return (
    <div className="p-6">
      <form
        onSubmit={handleSubmit}
        noValidate
        className="max-w-xl rounded-md border bg-white p-6"
      >
        <div className="mb-6 border-b border-slate-200 pb-4">
          <p className="mt-1 text-sm text-slate-500">
            안전한 계정 사용을 위해 새로운 비밀번호를
            입력해주세요.
          </p>
        </div>

        {notice.message && (
          <div
            role="alert"
            aria-live="polite"
            className={`mb-6 rounded-md border px-4 py-3 text-sm ${
              notice.type === 'success'
                ? 'border-green-200 bg-green-50 text-green-700'
                : 'border-red-200 bg-red-50 text-red-700'
            }`}
          >
            {notice.message}
          </div>
        )}

        <div className="space-y-5">
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

        <div className="mt-4 rounded-md bg-slate-50 p-4 text-sm text-slate-500">
          <p>비밀번호는 다음 조건을 만족해야 합니다.</p>

          <p className="mt-1">
            영문, 숫자, 특수문자를 포함한 8자 이상 20자 이하
          </p>

          <p className="mt-1">
            사용 가능한 특수문자: @ $ ! % * # ? &
          </p>
        </div>

        <div className="mt-6 flex justify-end gap-2">
          <button
            type="button"
            onClick={handleCancel}
            disabled={isSubmitting || isCompleted}
            className="rounded-md bg-slate-100 px-4 py-2 text-sm text-slate-600 hover:bg-slate-200 disabled:cursor-not-allowed disabled:opacity-60"
          >
            취소
          </button>

          <button
            type="submit"
            disabled={
              isSubmitting || isCompleted || isFormEmpty
            }
            className="rounded-md bg-blue-500 px-4 py-2 text-sm text-white hover:bg-blue-600 disabled:cursor-not-allowed disabled:opacity-60"
          >
            {isSubmitting
              ? '변경 중...'
              : isCompleted
                ? '변경 완료'
                : '비밀번호 변경'}
          </button>
        </div>
      </form>
    </div>
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

  return (
    <label className="block">
      <span className="mb-1 block text-sm font-medium text-slate-700">
        {label}
      </span>

      <input
        type="password"
        name={name}
        autoComplete={autoComplete}
        placeholder={placeholder}
        maxLength={20}
        value={value}
        onChange={onChange}
        aria-invalid={Boolean(error)}
        aria-describedby={error ? errorId : undefined}
        className={`w-full rounded-md border px-3 py-2 text-sm outline-none ${
          error
            ? 'border-red-500 focus:border-red-500'
            : 'border-slate-300 focus:border-blue-500'
        }`}
      />

      {error && (
        <p
          id={errorId}
          className="mt-1 text-xs text-red-500"
        >
          {error}
        </p>
      )}
    </label>
  );
}