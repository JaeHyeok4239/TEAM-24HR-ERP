"use client";

import { useEffect, useState } from "react";
import { History } from "lucide-react";

import { findHrEmployeeHistoriesRequest } from "@/services/hrEmployeeService";

import DetailSection from "./DetailSection";
import {
  EMPLOYMENT_TYPE_LABELS,
  ROLE_ITEMS,
  STATUS_LABELS,
} from "./employeeDetailConstants";

const HISTORY_ITEM_LABELS = {
  DEPARTMENT: "부서",
  POSITION: "직급",
  EMPLOYMENT_TYPE: "고용형태",
  STATUS: "재직상태",
  HIRE_DATE: "입사일",
  RESIGNATION_DATE: "퇴사일",
  ROLE: "접근 권한",
  SENSITIVE_INFO: "민감정보",
};

export default function HistoryTab({ employee }) {
  const [histories, setHistories] = useState([]);
  const [isLoading, setIsLoading] = useState(false);

  const employeeId = employee?.employeeId;
  const visibleHistories = employeeId
    ? [...histories].sort(
        (a, b) =>
          new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime(),
      )
    : [];

  useEffect(() => {
    if (!employeeId) {
      return;
    }

    let ignore = false;

    Promise.resolve().then(async () => {
      try {
        if (ignore) {
          return;
        }

        setIsLoading(true);

        const data = await findHrEmployeeHistoriesRequest(employeeId);

        if (!ignore) {
          setHistories(data);
        }
      } catch (error) {
        if (!ignore) {
          alert(error.message || "인사 이력 조회 중 오류가 발생했습니다.");
        }
      } finally {
        if (!ignore) {
          setIsLoading(false);
        }
      }
    });

    return () => {
      ignore = true;
    };
  }, [employeeId]);

  return (
    <div className="space-y-5">
      <DetailSection title="인사 이력" icon={<History size={16} />}>
        {isLoading ? (
          <div className="rounded-md border border-slate-200 bg-slate-50 px-4 py-8 text-center text-sm text-slate-500">
            인사 이력을 불러오는 중입니다.
          </div>
        ) : visibleHistories.length === 0 ? (
          <div className="rounded-md border border-slate-200 bg-slate-50 px-4 py-8 text-center text-sm text-slate-500">
            등록된 인사 이력이 없습니다.
          </div>
        ) : (
          <div className="space-y-3">
            {visibleHistories.map((history) => (
              <HistoryItem key={history.employeeHistoryId} history={history} />
            ))}
          </div>
        )}
      </DetailSection>
    </div>
  );
}

function HistoryItem({ history }) {
  const changeItemLabel =
    HISTORY_ITEM_LABELS[history.changeItem] ?? history.changeItem;

  return (
    <div className="rounded-lg border border-slate-200 bg-white px-4 py-4">
      <div className="flex items-start justify-between gap-4">
        <div className="min-w-0">
          <div className="flex flex-wrap items-center gap-2">
            <span className="rounded-full bg-slate-100 px-2.5 py-1 text-xs font-semibold text-slate-700">
              {changeItemLabel}
            </span>

            <span className="text-xs text-slate-400">
              {formatDateTime(history.createdAt)}
            </span>
          </div>

          <div className="mt-3 flex flex-wrap items-center gap-2 text-sm">
            <ValueBadge
              value={formatHistoryValue(
                history.changeItem,
                history.beforeValue,
              )}
              type="before"
            />

            <span className="text-xs text-slate-400">→</span>

            <ValueBadge
              value={formatHistoryValue(history.changeItem, history.afterValue)}
              type="after"
            />
          </div>
        </div>

        <div className="shrink-0 text-right text-xs text-slate-500">
          <div>변경자</div>
          <div className="mt-1 font-medium text-slate-700">
            {history.changedByName || "-"}
          </div>
        </div>
      </div>

      {history.changeReason && (
        <div className="mt-3 rounded-md bg-slate-50 px-3 py-2 text-xs leading-5 text-slate-600">
          <span className="font-medium text-slate-700">사유: </span>
          {history.changeReason}
        </div>
      )}
    </div>
  );
}

function ValueBadge({ value, type }) {
  const label = value || "없음";

  return (
    <span
      className={`max-w-[240px] truncate rounded-md px-2.5 py-1 text-xs font-medium ${
        type === "before"
          ? "bg-red-50 text-red-700"
          : "bg-blue-50 text-blue-700"
      }`}
      title={label}
    >
      {label}
    </span>
  );
}

function formatHistoryValue(changeItem, value) {
  if (!value) {
    return "없음";
  }

  if (changeItem === "EMPLOYMENT_TYPE") {
    return EMPLOYMENT_TYPE_LABELS[value] ?? value;
  }

  if (changeItem === "STATUS") {
    return STATUS_LABELS[value] ?? value;
  }

  if (changeItem === "ROLE") {
    return value
      .split(",")
      .map((roleCode) => roleCode.trim())
      .filter(Boolean)
      .map((roleCode) => {
        const role = ROLE_ITEMS.find((item) => item.code === roleCode);
        return role?.label ?? roleCode;
      })
      .join(", ");
  }

  return value;
}

function formatDateTime(value) {
  if (!value) {
    return "-";
  }

  const date = new Date(value);

  if (Number.isNaN(date.getTime())) {
    return value;
  }

  return new Intl.DateTimeFormat("ko-KR", {
    year: "numeric",
    month: "2-digit",
    day: "2-digit",
    hour: "2-digit",
    minute: "2-digit",
  }).format(date);
}
