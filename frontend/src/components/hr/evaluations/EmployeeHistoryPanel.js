import { HALF_TYPE_LABELS } from "./evaluationConstants";
import { EmptyBox, GradeBadge, formatDateTime } from "./EvaluationCommon";

export default function EmployeeHistoryPanel({
  selectedEmployee,
  histories,
}) {
  if (!selectedEmployee) {
    return <EmptyBox message="직원을 선택해주세요." />;
  }

  return (
    <div className="mt-4 space-y-4">
      <div className="rounded-lg bg-slate-50 px-4 py-3">
        <p className="font-bold text-slate-900">
          {selectedEmployee.employeeName}
        </p>

        <p className="mt-1 text-sm text-slate-500">
          누적 {selectedEmployee.totalScore}점 · 평가{" "}
          {selectedEmployee.evaluationCount}회 · {selectedEmployee.yearsOfService ?? 0}년차
        </p>

        {selectedEmployee.promotionType && (
          <p className="mt-1 text-sm font-semibold">
            {selectedEmployee.promotionType === "EARLY" ? (
              <span className="text-purple-600">조기진급 대상자</span>
            ) : (
              <span className="text-blue-600">진급 대상자</span>
            )}
          </p>
        )}
      </div>

      {histories.length === 0 ? (
        <EmptyBox message="확정된 평가 이력이 없습니다." />
      ) : (
        <div className="space-y-3">
          {histories.map((history) => (
            <div
              key={history.employeeEvaluationId}
              className="rounded-lg border border-slate-200 px-4 py-3"
            >
              <div className="flex items-start justify-between gap-3">
                <div>
                  <p className="font-semibold text-slate-900">
                    {history.periodName}
                  </p>

                  <p className="mt-1 text-xs text-slate-400">
                    {history.evaluationYear}년{" "}
                    {HALF_TYPE_LABELS[history.halfType] ?? history.halfType}
                  </p>
                </div>

                <div className="text-right">
                  <div className="flex items-center justify-end gap-2">
                    <GradeBadge grade={history.grade} />
                    <p className="text-lg font-bold text-slate-900">
                      {history.totalScore}점
                    </p>
                  </div>

                  <p className="text-xs text-slate-400">
                    {formatDateTime(history.confirmedAt)}
                  </p>
                </div>
              </div>

              {history.comment && (
                <p className="mt-3 rounded-md bg-slate-50 px-3 py-2 text-sm text-slate-600">
                  {history.comment}
                </p>
              )}
            </div>
          ))}
        </div>
      )}
    </div>
  );
}