import { EmptyBox } from "./EvaluationCommon";

export default function PromotionCandidateTable({
  candidates,
  isLoading,
}) {
  if (isLoading) {
    return <EmptyBox message="진급심사 대상자를 불러오는 중입니다." />;
  }

  if (candidates.length === 0) {
    return <EmptyBox message="진급심사 대상자가 없습니다." />;
  }

  return (
    <div className="mt-4 overflow-hidden rounded-lg border border-slate-200">
      <table className="w-full border-collapse text-sm">
        <thead className="bg-slate-50 text-xs font-semibold text-slate-500">
          <tr>
            <th className="px-4 py-3 text-left">직원</th>
            <th className="px-4 py-3 text-left">부서</th>
            <th className="px-4 py-3 text-left">현재 직급</th>
            <th className="px-4 py-3 text-left">진급 대상</th>
            <th className="px-4 py-3 text-right">누적점수</th>
            <th className="px-4 py-3 text-right">필요점수</th>
            <th className="px-4 py-3 text-right">평가횟수</th>
          </tr>
        </thead>

        <tbody className="divide-y divide-slate-100">
          {candidates.map((candidate) => (
            <tr key={candidate.employeeId} className="bg-white">
              <td className="px-4 py-3">
                <div className="font-semibold text-slate-900">
                  {candidate.employeeName}
                </div>

                <div className="mt-0.5 text-xs text-slate-400">
                  {candidate.employeeNo}
                </div>
              </td>

              <td className="px-4 py-3 text-slate-600">
                {candidate.departmentName ?? "-"}
              </td>

              <td className="px-4 py-3 text-slate-600">
                {candidate.currentPositionName ?? "-"}
              </td>

              <td className="px-4 py-3 font-semibold text-blue-700">
                {candidate.targetPositionName ?? "-"}
              </td>

              <td className="px-4 py-3 text-right font-bold text-slate-900">
                {candidate.totalScore}
              </td>

              <td className="px-4 py-3 text-right">
                {candidate.requiredScore}
              </td>

              <td className="px-4 py-3 text-right">
                {candidate.evaluationCount} / {candidate.minEvaluationCount}
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}