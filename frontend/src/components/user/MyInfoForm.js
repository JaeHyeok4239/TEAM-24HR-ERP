'use client';

import { useState } from 'react';

import { updateMyInfoRequest } from '@/services/userService';
import { useAuthStore } from '@/store/authStore';

const createInitialForm = (userInfo) => ({
  email: userInfo.email ?? '',
  phone: userInfo.phone ?? '',
  zipcode: userInfo.zipcode ?? '',
  address: userInfo.address ?? '',
  addressDetail: userInfo.addressDetail ?? '',
});

const createEmptyErrors = () => ({
  email: '',
  phone: '',
});

const createEmptyNotice = () => ({
  type: '',
  message: '',
});

// 입력값이 비어 있으면 null, 값이 있으면 앞뒤 공백 제거
const toNullable = (value) => {
  const trimmedValue = value.trim();

  return trimmedValue === '' ? null : trimmedValue;
};

const validateEmail = (email) => {
  const trimmedEmail = email.trim();

  // 이메일은 필수값이 아니므로 빈 값 허용
  if (!trimmedEmail) {
    return '';
  }

  const emailPattern = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

  return emailPattern.test(trimmedEmail)
    ? ''
    : '올바른 이메일 형식으로 입력해주세요.';
};

const validatePhone = (phone) => {
  const trimmedPhone = phone.trim();

  // 전화번호는 필수값이 아니므로 빈 값 허용
  if (!trimmedPhone) {
    return '';
  }

  const phonePattern = /^010-\d{4}-\d{4}$/;

  return phonePattern.test(trimmedPhone)
    ? ''
    : '전화번호는 010-1234-5678 형식으로 입력해주세요.';
};

export default function MyInfoForm() {
  const userInfo = useAuthStore((state) => state.userInfo);

  if (!userInfo) {
    return (
      <p className="p-6">
        사용자 정보를 불러오는 중입니다.
      </p>
    );
  }

  return <MyInfoFormContent userInfo={userInfo} />;
}

function MyInfoFormContent({ userInfo }) {
  const setUserInfo = useAuthStore((state) => state.setUserInfo);

  const [form, setForm] = useState(() =>
    createInitialForm(userInfo)
  );

  const [errors, setErrors] = useState(createEmptyErrors);
  const [notice, setNotice] = useState(createEmptyNotice);
  const [isSubmitting, setIsSubmitting] = useState(false);

  const originalForm = createInitialForm(userInfo);

  // 기존 정보와 현재 입력값이 하나라도 다른지 확인
  const isDirty = Object.keys(originalForm).some(
    (key) => form[key] !== originalForm[key]
  );

  const handleChange = (event) => {
    const { name, value } = event.target;

    setForm((previous) => ({
      ...previous,
      [name]: value,
    }));

    // 값을 다시 입력하면 이전 성공·실패 안내 문구 제거
    setNotice(createEmptyNotice());

    if (name === 'email') {
      setErrors((previous) => ({
        ...previous,
        email: validateEmail(value),
      }));
    }

    if (name === 'phone') {
      setErrors((previous) => ({
        ...previous,
        phone: validatePhone(value),
      }));
    }
  };

  const handleCancel = () => {
    setForm(createInitialForm(userInfo));
    setErrors(createEmptyErrors());
    setNotice(createEmptyNotice());
  };

  const handleSubmit = async (event) => {
    event.preventDefault();

    const emailError = validateEmail(form.email);
    const phoneError = validatePhone(form.phone);

    setErrors({
      email: emailError,
      phone: phoneError,
    });

    // 잘못된 형식이 있으면 API 요청을 보내지 않음
    if (emailError || phoneError) {
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

      const requestData = {
        email: toNullable(form.email),
        phone: toNullable(form.phone),
        zipcode: toNullable(form.zipcode),
        address: toNullable(form.address),
        addressDetail: toNullable(form.addressDetail),
      };

      const updatedUserInfo =
        await updateMyInfoRequest(requestData);

      // 수정된 응답을 전역 상태와 현재 폼에 반영
      setUserInfo(updatedUserInfo);
      setForm(createInitialForm(updatedUserInfo));
      setErrors(createEmptyErrors());

      setNotice({
        type: 'success',
        message: '내 정보가 정상적으로 수정되었습니다.',
      });
    } catch (error) {
      console.error('내 정보 수정 실패:', error);

      setNotice({
        type: 'error',
        message:
          '내 정보 수정에 실패했습니다. 잠시 후 다시 시도해주세요.',
      });
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <form
      onSubmit={handleSubmit}
      noValidate
      className="p-6"
    >
      <div className="max-w-5xl">
        {/* 사용자 정보 및 버튼 영역 */}
        <div className="mb-6 flex items-center justify-between border-b border-slate-300 pb-4">
          <div>
            <p className="font-semibold text-slate-900">
              {userInfo.name}
            </p>

            <p className="text-sm text-slate-400">
              @{userInfo.loginId}
            </p>
          </div>

          <div className="flex gap-2">
            <button
              type="button"
              onClick={handleCancel}
              disabled={isSubmitting}
              className="rounded-md bg-slate-100 px-4 py-2 text-sm text-slate-600 hover:bg-slate-200 disabled:cursor-not-allowed disabled:opacity-60"
            >
              취소
            </button>

            <button
              type="submit"
              disabled={isSubmitting || !isDirty}
              className="rounded-md bg-blue-500 px-4 py-2 text-sm text-white hover:bg-blue-600 disabled:cursor-not-allowed disabled:opacity-60"
            >
              {isSubmitting ? '저장 중...' : '저장'}
            </button>
          </div>
        </div>

        {/* 성공·실패·검증 안내 영역 */}
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

        {/* 정보 입력 영역 */}
        <div className="grid gap-6 lg:grid-cols-2">
          <section className="rounded-md border p-5">
            <h2 className="mb-5 font-semibold">
              기본 정보
            </h2>

            <div className="space-y-4">
              <ReadOnlyField
                label="사번"
                value={userInfo.employeeNo}
              />

              <ReadOnlyField
                label="부서"
                value={userInfo.departmentName}
              />

              <ReadOnlyField
                label="직급"
                value={userInfo.positionName}
              />

              <ReadOnlyField
                label="입사일"
                value={userInfo.hireDate}
              />
            </div>
          </section>

          <div className="space-y-6">
            <section className="rounded-md border p-5">
              <h2 className="mb-5 font-semibold">
                연락처 정보
              </h2>

              <div className="space-y-4">
                <EditableField
                  label="이메일"
                  name="email"
                  type="email"
                  autoComplete="email"
                  placeholder="name@example.com"
                  maxLength={100}
                  value={form.email}
                  onChange={handleChange}
                  error={errors.email}
                />

                <EditableField
                  label="전화번호"
                  name="phone"
                  type="tel"
                  autoComplete="tel"
                  placeholder="010-1234-5678"
                  maxLength={20}
                  value={form.phone}
                  onChange={handleChange}
                  error={errors.phone}
                />
              </div>
            </section>

            <section className="rounded-md border p-5">
              <h2 className="mb-5 font-semibold">
                주소 정보
              </h2>

              <div className="space-y-4">
                <EditableField
                  label="우편번호"
                  name="zipcode"
                  inputMode="numeric"
                  autoComplete="postal-code"
                  placeholder="우편번호"
                  maxLength={10}
                  value={form.zipcode}
                  onChange={handleChange}
                />

                <EditableField
                  label="주소"
                  name="address"
                  autoComplete="street-address"
                  placeholder="주소를 입력해주세요."
                  maxLength={80}
                  value={form.address}
                  onChange={handleChange}
                />

                <EditableField
                  label="상세주소"
                  name="addressDetail"
                  autoComplete="address-line2"
                  placeholder="상세주소를 입력해주세요."
                  maxLength={80}
                  value={form.addressDetail}
                  onChange={handleChange}
                />
              </div>
            </section>
          </div>
        </div>
      </div>
    </form>
  );
}

function ReadOnlyField({ label, value }) {
  return (
    <label className="block">
      <span className="mb-1 block text-sm font-medium">
        {label}
      </span>

      <input
        type="text"
        value={value ?? ''}
        readOnly
        className="w-full rounded-md border bg-gray-100 px-3 py-2 text-sm text-gray-500"
      />
    </label>
  );
}

function EditableField({
  label,
  name,
  type = 'text',
  inputMode,
  autoComplete,
  placeholder,
  maxLength,
  value,
  onChange,
  error,
}) {
  const errorId = `${name}-error`;

  return (
    <label className="block">
      <span className="mb-1 block text-sm font-medium">
        {label}
      </span>

      <input
        type={type}
        name={name}
        inputMode={inputMode}
        autoComplete={autoComplete}
        value={value}
        placeholder={placeholder}
        maxLength={maxLength}
        onChange={onChange}
        aria-invalid={Boolean(error)}
        aria-describedby={error ? errorId : undefined}
        className={`w-full rounded-md border px-3 py-2 text-sm outline-none ${
          error
            ? 'border-red-500 focus:border-red-500'
            : 'focus:border-blue-500'
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
