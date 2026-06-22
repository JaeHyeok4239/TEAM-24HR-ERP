"use client";

import { useState } from "react";
import { History, ShieldCheck, UserRound } from "lucide-react";

import {
  Card,
  CardContent,
  CardHeader,
} from "@/components/ui/card";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";

const DETAIL_TABS = [
  { value: "basic", label: "기본정보" },
  { value: "hr", label: "인사정보" },
  { value: "history", label: "인사 이력" },
];

const STATUS_LABELS = {
  ACTIVE: "재직",
  LEAVE: "휴직",
  RESIGNED: "퇴사",
  RETIRED: "퇴사",
  LOCKED: "잠김",
};

const EMPLOYMENT_TYPE_LABELS = {
  REGULAR: "정규직",
  DAILY: "일용직",
};

const ROLE_ITEMS = [
  {
    code: "USER",
    label: "일반 사용자",
    description: "시스템 기본 사용 권한",
  },
  {
    code: "ADMIN",
    label: "시스템 관리자",
    description: "전체 시스템 및 권한 관리",
  },
  {
    code: "HR_OPERATOR",
    label: "인사 실무자",
    description: "직원 등록 및 기본 인사정보 관리",
  },
  {
    code: "HR_MANAGER",
    label: "인사 관리자",
    description: "권한 부여, 퇴사 처리, 민감정보 관리",
  },
  {
    code: "ATTENDANCE_MANAGER",
    label: "근태 관리자",
    description: "근태, 출퇴근, 휴가 정보 관리",
  },
  {
    code: "PAYROLL_MANAGER",
    label: "급여 관리자",
    description: "급여 산정 및 급여 정보 관리",
  },
];

export default function EmployeeDetailPanel({ employee, isLoading }) {
  const [activeTab, setActiveTab] = useState("basic");

  if (isLoading) {
    return (
      <EmptyDetailPanel message="직원 상세 정보를 불러오는 중..." />
    );
  }

  if (!employee) {
    return (
      <EmptyDetailPanel message="직원을 선택해주세요." />
    );
  }

  const employeeRoles = employee.roles ?? ["USER"];

  return (
    <Card className="flex h-full flex-col overflow-hidden rounded-xl border-slate-200 bg-white shadow-sm">
      <CardHeader className="flex h-[82px] shrink-0 justify-center border-b px-5 py-0">
        <div className="flex items-center gap-3">
          <div className="flex h-11 w-11 shrink-0 items-center justify-center rounded-full bg-slate-300 text-sm font-semibold text-white">
            {employee.name?.slice(-2)}
          </div>

          <div className="min-w-0">
            <p className="truncate text-lg font-bold text-slate-900">
              {employee.name}
            </p>

            <p className="mt-1 text-sm text-slate-500">
              {employee.positionName ?? "-"} · {employee.departmentName ?? "-"}
            </p>
          </div>
        </div>
      </CardHeader>

      <div className="h-[52px] shrink-0 border-b bg-white px-5">
        <div className="flex h-full items-end gap-8">
          {DETAIL_TABS.map((tab) => {
            const selected = activeTab === tab.value;

            return (
              <button
                key={tab.value}
                type="button"
                onClick={() => setActiveTab(tab.value)}
                className={`h-full border-b-2 px-1 text-sm font-medium transition ${selected
                  ? "border-slate-900 text-slate-900"
                  : "border-transparent text-slate-500 hover:text-slate-900"
                  }`}
              >
                {tab.label}
              </button>
            );
          })}
        </div>
      </div>

      <CardContent className="min-h-0 flex-1 overflow-y-auto bg-white p-5">
        {activeTab === "basic" && (
          <BasicInfoTab employee={employee} />
        )}

        {activeTab === "hr" && (
          <HrInfoTab employee={employee} employeeRoles={employeeRoles} />
        )}

        {activeTab === "history" && (
          <HistoryTab />
        )}
      </CardContent>
    </Card>
  );
}

function EmptyDetailPanel({ message }) {
  return (
    <Card className="flex h-full flex-col overflow-hidden rounded-xl border-slate-200 bg-white shadow-sm">
      <CardHeader className="flex h-[58px] shrink-0 justify-center border-b px-5 py-0">
        <p className="text-base font-semibold text-slate-900">직원 상세</p>
      </CardHeader>

      <CardContent className="flex flex-1 items-center justify-center text-sm text-slate-400">
        {message}
      </CardContent>
    </Card>
  );
}

function BasicInfoTab({ employee }) {
  return (
    <Section title="기본정보" icon={<UserRound size={16} />}>
      <div className="grid grid-cols-3 gap-4">
        <ReadOnlyField label="이름" value={employee.name} />
        <ReadOnlyField label="사번" value={employee.employeeNo} />
        <ReadOnlyField label="로그인 ID" value={employee.loginId} />
      </div>

      <div className="mt-4 grid grid-cols-2 gap-4">
        <ReadOnlyField label="전화번호" value={employee.phone} />
        <ReadOnlyField label="이메일" value={employee.email} />
      </div>

      <div className="mt-4 grid grid-cols-[180px_1fr] gap-4">
        <ReadOnlyField label="우편번호" value={employee.zipcode} />
        <ReadOnlyField label="주소" value={employee.address} />
      </div>

      <div className="mt-4">
        <ReadOnlyField label="상세주소" value={employee.addressDetail} />
      </div>
    </Section>
  );
}

function HrInfoTab({ employee, employeeRoles }) {
  return (
    <div className="space-y-5">
      <Section title="소속 및 상태">
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
      </Section>

      <Section title="접근 권한" icon={<ShieldCheck size={16} />}>
        <div className="grid grid-cols-3 gap-3">
          {ROLE_ITEMS.map((role) => {
            const checked = employeeRoles.includes(role.code);

            return (
              <label
                key={role.code}
                className={`flex min-h-[74px] cursor-default gap-3 rounded-md border bg-white px-3 py-3 text-sm transition ${checked
                    ? "border-slate-300"
                    : "border-slate-200 text-slate-500"
                  }`}
              >
                <input
                  type="checkbox"
                  checked={checked}
                  readOnly
                  className="mt-0.5 h-4 w-4 shrink-0 accent-slate-900"
                />

                <span className="min-w-0">
                  <span className="block font-medium text-slate-800">
                    {role.label}
                  </span>

                  <span className="mt-1 block text-xs leading-4 text-slate-500">
                    {role.description}
                  </span>
                </span>
              </label>
            );
          })}
        </div>

        <p className="mt-3 text-xs text-slate-500">
          권한 부여와 회수는 다음 단계에서 수정 기능으로 연결합니다.
        </p>
      </Section>

      <Section title="민감정보">
        <div className="grid grid-cols-2 gap-4">
          <ReadOnlyField label="은행명" value={employee.bankName} />
          <ReadOnlyField label="계좌번호" value={employee.accountNumber} />
          <ReadOnlyField label="주민등록번호" value={employee.rrn} />
          <ReadOnlyField label="예금주" value={employee.accountHolder} />
        </div>
      </Section>
    </div>
  );
}

function HistoryTab() {
  return (
    <Section title="인사 이력" icon={<History size={16} />}>
      <div className="flex h-[360px] items-center justify-center rounded-md border border-dashed border-slate-300 bg-white text-sm text-slate-400">
        인사 이력은 직원 수정 기능과 함께 연결 예정입니다.
      </div>
    </Section>
  );
}

function Section({ title, icon, children }) {
  return (
    <section className="rounded-lg border border-slate-200 bg-slate-50 p-5">
      <div className="mb-4 flex items-center gap-2">
        {icon && <span className="text-slate-500">{icon}</span>}
        <h3 className="text-sm font-semibold text-slate-900">{title}</h3>
      </div>

      {children}
    </section>
  );
}

function ReadOnlyField({ label, value }) {
  return (
    <div>
      <Label className="mb-2 block text-xs font-medium text-slate-500">
        {label}
      </Label>

      <Input
        value={value || "-"}
        readOnly
        className="h-10 border-slate-200 bg-white text-sm text-slate-800"
      />
    </div>
  );
}

function formatDate(value) {
  if (!value) {
    return "-";
  }

  return String(value).slice(0, 10);
}