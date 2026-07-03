import { EmptyBox, EvaluationStatusBadge, GradeBadge } from "./EvaluationCommon";

export default function TargetTable({
  targets,
  isLoading,
  selectedPeriod,
  onOpenEvaluation,
}) {
  if (!selectedPeriod) {
    return <EmptyBox message="평가기간을 선택해주세요." />;
  }

  if (isLoading) {
    return <EmptyBox message="평가 대상자를 불러오는 중입니다." />;
  }

  if (targets.length === 0) {
    return <EmptyBox message="생성된 평가 대상자가 없습니다." />;
  }

  return (
    <div className="mt-4 overflow-hidden rounded-lg border border-slate-200">
      <table className="w-full border-collapse text-sm">
        <thead className="bg-slate-50 text-xs font-semibold text-slate-500">
          <tr>
            <th className="px-4 py-3 text-left">직원</th>
            <th className="px-4 py-3 text-left">부서</th>
            <th className="px-4 py-3 text-left">직급</th>
            <th className="px-4 py-3 text-left">평가상태</th>
            <th className="px-4 py-3 text-right">총점</th>
            <th className="px-4 py-3 text-center">등급</th>
            <th className="px-4 py-3 text-left">평가자</th>
          </tr>
        </thead>

        <tbody className="divide-y divide-slate-100">
          {targets.map((target) => (
            <tr
              key={target.employeeEvaluationId}
              role="button"
              tabIndex={0}
              onClick={() => onOpenEvaluation(target.employeeEvaluationId)}
              onKeyDown={(event) => {
                if (event.key === "Enter") {
                  onOpenEvaluation(target.employeeEvaluationId);
                }
              }}
              className="cursor-pointer bg-white transition hover:bg-slate-50"
            >
              <td className="px-4 py-3">
                <div className="font-semibold text-slate-900">
                  {target.employeeName}
                </div>
                <div className="mt-0.5 text-xs text-slate-400">
                  {target.employeeNo}
                </div>
              </td>

              <td className="px-4 py-3 text-slate-600">
                {target.departmentName ?? "-"}
              </td>

              <td className="px-4 py-3 text-slate-600">
                {target.positionName ?? "-"}
              </td>

              <td className="px-4 py-3">
                <EvaluationStatusBadge status={target.status} />
              </td>

              <td className="px-4 py-3 text-right font-semibold text-slate-900">
                {target.totalScore ?? "-"}
              </td>

              <td className="px-4 py-3 text-center">
                <GradeBadge grade={target.grade} />
              </td>

              <td className="px-4 py-3 text-slate-600">
                {target.evaluatorName ?? "-"}
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}