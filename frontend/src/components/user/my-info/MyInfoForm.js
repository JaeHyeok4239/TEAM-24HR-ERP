"use client";

import { useState } from "react";
import { BadgeCheck, Home, Mail, MapPin, Phone, UserRound } from "lucide-react";

import KakaoPostcodeButton from "@/components/common/KakaoPostcodeButton";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { updateMyInfoRequest } from "@/services/userService";
import { useAuthStore } from "@/store/authStore";

const createInitialForm = (userInfo) => ({
  email: userInfo.email ?? "",
  phone: userInfo.phone ?? "",
  zipcode: userInfo.zipcode ?? "",
  address: userInfo.address ?? "",
  addressDetail: userInfo.addressDetail ?? "",
});

const createEmptyErrors = () => ({
  email: "",
  phone: "",
});

const createEmptyNotice = () => ({
  type: "",
  message: "",
});

const toNullable = (value) => {
  const trimmedValue = value.trim();

  return trimmedValue === "" ? null : trimmedValue;
};

const validateEmail = (email) => {
  const trimmedEmail = email.trim();

  if (!trimmedEmail) {
    return "";
  }

  const emailPattern = /^[^\s@]+@[^\s@]+.[^\s@]+$/;

  return emailPattern.test(trimmedEmail)
    ? ""
    : "올바른 이메일 형식으로 입력해주세요.";
};

const validatePhone = (phone) => {
  const trimmedPhone = phone.trim();

  if (!trimmedPhone) {
    return "";
  }

  const phonePattern = /^010-\d{4}-\d{4}$/;

  return phonePattern.test(trimmedPhone)
    ? ""
    : "전화번호는 010-1234-5678 형식으로 입력해주세요.";
};

const formatDate = (value) => {
  if (!value) {
    return "-";
  }

  return String(value).split("T")[0];
};

export default function MyInfoForm() {
  const userInfo = useAuthStore((state) => state.userInfo);

  if (!userInfo) {
    return (
      <div className="flex min-h-[240px] items-center justify-center rounded-md border border-slate-200 bg-white">
        <p className="text-sm text-slate-500">
          사용자 정보를 불러오는 중입니다.
        </p>
      </div>
    );
  }

  return <MyInfoFormContent userInfo={userInfo} />;
}

function MyInfoFormContent({ userInfo }) {
  const setUserInfo = useAuthStore((state) => state.setUserInfo);

  const [form, setForm] = useState(() => createInitialForm(userInfo));
  const [errors, setErrors] = useState(createEmptyErrors);
  const [notice, setNotice] = useState(createEmptyNotice);
  const [isSubmitting, setIsSubmitting] = useState(false);

  const originalForm = createInitialForm(userInfo);

  const isDirty = Object.keys(originalForm).some(
    (key) => form[key] !== originalForm[key],
  );

  const handleChange = (event) => {
    const { name, value } = event.target;

    setForm((previous) => ({
      ...previous,
      [name]: value,
    }));

    setNotice(createEmptyNotice());

    if (name === "email") {
      setErrors((previous) => ({
        ...previous,
        email: validateEmail(value),
      }));
    }

    if (name === "phone") {
      setErrors((previous) => ({
        ...previous,
        phone: validatePhone(value),
      }));
    }
  };

  const handlePostcodeComplete = ({ zipcode, address }) => {
    setForm((previous) => ({
      ...previous,
      zipcode,
      address,
    }));

    setNotice(createEmptyNotice());
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

    if (emailError || phoneError) {
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

      const requestData = {
        email: toNullable(form.email),
        phone: toNullable(form.phone),
        zipcode: toNullable(form.zipcode),
        address: toNullable(form.address),
        addressDetail: toNullable(form.addressDetail),
      };

      const updatedUserInfo = await updateMyInfoRequest(requestData);

      setUserInfo(updatedUserInfo);
      setForm(createInitialForm(updatedUserInfo));
      setErrors(createEmptyErrors());

      setNotice({
        type: "success",
        message: "내 정보가 정상적으로 수정되었습니다.",
      });
    } catch (error) {
      console.error("내 정보 수정 실패:", error);

      setNotice({
        type: "error",
        message: "내 정보 수정에 실패했습니다. 잠시 후 다시 시도해주세요.",
      });
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <form onSubmit={handleSubmit} noValidate>
      <div className="overflow-hidden rounded-md border border-slate-200 bg-white shadow-sm">
        <div
          className="grid"
          style={{
            gridTemplateColumns: "600px minmax(0, 1fr)",
          }}
        >
          <aside className="border-r border-slate-200 bg-white p-6">
            <ProfileSection userInfo={userInfo} />
            <div className="mt-6">
              <BasicInfoSection userInfo={userInfo} />
            </div>
          </aside>
          <section className="min-w-0 bg-white p-6">
            <div className="space-y-5">
              <ContactSection
                form={form}
                errors={errors}
                onChange={handleChange}
                disabled={isSubmitting}
              />

              <AddressSection
                form={form}
                onChange={handleChange}
                onPostcodeComplete={handlePostcodeComplete}
                disabled={isSubmitting}
              />

              <Notice notice={notice} />
            </div>
          </section>
        </div>

        <div className="flex justify-end gap-2 border-t border-slate-200 bg-slate-50 px-6 py-4">
          <Button
            type="button"
            variant="outline"
            onClick={handleCancel}
            disabled={isSubmitting || !isDirty}
            className="h-9 border-slate-300 bg-white px-5 text-sm text-slate-700 hover:bg-slate-100 hover:text-slate-900 disabled:cursor-not-allowed disabled:border-slate-200 disabled:bg-slate-100 disabled:text-slate-400"
          >
            취소
          </Button>

          <Button
            type="submit"
            disabled={isSubmitting || !isDirty}
            className="h-9 bg-[#1a2f4e] px-6 text-sm font-semibold text-white hover:bg-[#25476e] disabled:cursor-not-allowed disabled:bg-slate-300 disabled:text-slate-500"
          >
            {isSubmitting ? "저장 중..." : "저장"}
          </Button>
        </div>
      </div>
    </form>
  );
}

function ProfileSection({ userInfo }) {
  return (
    <section>
      <div className="flex items-center gap-4">
        <div className="flex h-14 w-14 shrink-0 items-center justify-center rounded-full bg-[#1a2f4e] text-white">
          <UserRound size={26} strokeWidth={1.8} />
        </div>
        <div className="min-w-0">
          <p className="truncate text-xl font-bold text-slate-950">
            {userInfo.name ?? "사용자"}
          </p>

          <p className="mt-1 text-sm font-medium text-slate-500">
            @{userInfo.loginId ?? "-"}
          </p>
        </div>
      </div>
    </section>
  );
}

function BasicInfoSection({ userInfo }) {
  return (
    <section>
      <div className="mb-4 flex items-center justify-between">
        <div>
          <h2 className="text-base font-bold text-slate-950">기본 정보 </h2>
          <p className="mt-1 text-xs text-slate-400">
            관리자만 변경할 수 있는 인사 정보입니다.
          </p>
        </div>
        <div className="flex h-9 w-9 items-center justify-center rounded-md bg-[#eef7ff] text-[#2563eb]">
          <BadgeCheck size={19} strokeWidth={1.8} />
        </div>
      </div>
      <dl className="overflow-hidden rounded-md border border-slate-200 bg-white">
        <ReadOnlyItem label="사번" value={userInfo.employeeNo} />
        <ReadOnlyItem label="부서" value={userInfo.departmentName} />
        <ReadOnlyItem label="직급" value={userInfo.positionName} />
        <ReadOnlyItem label="입사일" value={formatDate(userInfo.hireDate)} />
      </dl>
    </section>
  );
}

function ReadOnlyItem({ label, value }) {
  return (
    <div className="grid grid-cols-[92px_1fr] border-b border-slate-100 last:border-b-0">
      <dt className="bg-slate-50 px-4 py-3 text-sm font-semibold text-slate-500">
        {label}
      </dt>
      <dd className="px-4 py-3 text-sm font-medium text-slate-900">
        {value || "-"}
      </dd>
    </div>
  );
}

function ContactSection({ form, errors, onChange, disabled }) {
  return (
    <section className="rounded-md border border-slate-200 bg-white p-5">
      <SectionHeader
        title="연락처 정보"
        description="이메일과 전화번호를 수정할 수 있습니다."
        icon={<Phone size={19} strokeWidth={1.8} />}
        iconClassName="bg-[#e2fcff] text-[#1a2f4e]"
      />

      <div className="grid gap-4 md:grid-cols-2">
        <EditableField
          icon={<Mail size={15} />}
          label="이메일"
          name="email"
          type="email"
          autoComplete="email"
          placeholder="name@example.com"
          maxLength={100}
          value={form.email}
          onChange={onChange}
          error={errors.email}
          disabled={disabled}
        />

        <EditableField
          icon={<Phone size={15} />}
          label="전화번호"
          name="phone"
          type="tel"
          autoComplete="tel"
          placeholder="010-1234-5678"
          maxLength={20}
          value={form.phone}
          onChange={onChange}
          error={errors.phone}
          disabled={disabled}
        />
      </div>
    </section>
  );
}

function AddressSection({ form, onChange, onPostcodeComplete, disabled }) {
  return (
    <section className="rounded-md border border-slate-200 bg-white p-5">
      <SectionHeader
        title="주소 정보"
        description="우편번호와 주소는 검색으로 입력하고 상세주소는 직접 입력합니다."
        icon={<MapPin size={19} strokeWidth={1.8} />}
        iconClassName="bg-[#eef7ff] text-[#2563eb]"
      />

      <div className="space-y-4">
        <div className="space-y-2">
          <Label
            htmlFor="zipcode"
            className="flex items-center gap-2 text-sm font-semibold text-slate-700"
          >
            <span className="text-slate-400">
              <MapPin size={15} />
            </span>
            우편번호
          </Label>

          <div className="flex gap-2">
            <Input
              id="zipcode"
              name="zipcode"
              value={form.zipcode}
              placeholder="우편번호"
              readOnly
              disabled={disabled}
              className="h-9 max-w-[180px] bg-slate-50 text-sm text-slate-700"
            />

            <KakaoPostcodeButton
              onComplete={onPostcodeComplete}
              disabled={disabled}
            />
          </div>
        </div>

        <div className="space-y-2">
          <Label
            htmlFor="address"
            className="flex items-center gap-2 text-sm font-semibold text-slate-700"
          >
            <span className="text-slate-400">
              <Home size={15} />
            </span>
            주소
          </Label>

          <Input
            id="address"
            name="address"
            value={form.address}
            placeholder="주소 검색을 통해 입력해주세요."
            readOnly
            disabled={disabled}
            className="h-9 bg-slate-50 text-sm text-slate-700"
          />
        </div>

        <div className="space-y-2">
          <Label
            htmlFor="addressDetail"
            className="text-sm font-semibold text-slate-700"
          >
            상세주소
          </Label>

          <Input
            id="addressDetail"
            name="addressDetail"
            value={form.addressDetail}
            placeholder="상세주소를 입력해주세요."
            maxLength={80}
            autoComplete="address-line2"
            onChange={onChange}
            disabled={disabled}
            className="h-9 bg-white text-sm focus-visible:ring-[#a7f3ff]"
          />
        </div>
      </div>
    </section>
  );
}

function SectionHeader({ title, description, icon, iconClassName }) {
  return (
    <div className="mb-5 flex items-center justify-between border-b border-slate-100 pb-4">
      <div>
        <h2 className="text-base font-bold text-slate-950">{title} </h2>
        <p className="mt-1 text-xs text-slate-400">{description}</p>
      </div>
      <div
        className={[
          "flex h-9 w-9 items-center justify-center rounded-md",
          iconClassName,
        ].join(" ")}
      >
        {icon}
      </div>
    </div>
  );
}

function EditableField({
  icon,
  label,
  name,
  type = "text",
  autoComplete,
  placeholder,
  maxLength,
  value,
  onChange,
  error,
  disabled,
}) {
  const errorId = name + "-error";

  const inputClassName = [
    "h-9 bg-white text-sm",
    error
      ? "border-red-400 focus-visible:ring-red-200"
      : "border-slate-200 focus-visible:ring-[#a7f3ff]",
  ].join(" ");

  return (
    <div className="space-y-2">
      <Label
        htmlFor={name}
        className="flex items-center gap-2 text-sm font-semibold text-slate-700"
      >
        <span className="text-slate-400">{icon}</span>
        {label}
      </Label>
      <Input
        id={name}
        type={type}
        name={name}
        autoComplete={autoComplete}
        value={value}
        placeholder={placeholder}
        maxLength={maxLength}
        onChange={onChange}
        disabled={disabled}
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

function Notice({ notice }) {
  if (!notice.message) {
    return null;
  }

  const isSuccess = notice.type === "success";

  const noticeClassName = [
    "rounded-md border px-4 py-3 text-sm font-medium",
    isSuccess
      ? "border-emerald-200 bg-emerald-50 text-emerald-700"
      : "border-red-200 bg-red-50 text-red-700",
  ].join(" ");

  return (
    <div role="alert" aria-live="polite" className={noticeClassName}>
      {notice.message}
    </div>
  );
}
