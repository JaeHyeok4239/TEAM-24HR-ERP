import { EmptyBox, GradeBadge } from "./EvaluationCommon";

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
            <th className="px-4 py-3 text-right">연차</th>
            <th className="px-4 py-3 text-right">누적점수</th>
            <th className="px-4 py-3 text-center">최근등급</th>
            <th className="px-4 py-3 text-center">구분</th>
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

              <td className="px-4 py-3 text-right text-slate-600">
                {candidate.yearsOfService ?? 0}년
              </td>

              <td className="px-4 py-3 text-right font-bold text-slate-900">
                {candidate.totalScore}
              </td>

              <td className="px-4 py-3 text-center">
                <GradeBadge grade={candidate.latestGrade} />
              </td>

              <td className="px-4 py-3 text-center">
                {candidate.promotionType === "EARLY" ? (
                  <div>
                    <span className="rounded-full bg-purple-50 px-2.5 py-1 text-xs font-bold text-purple-700">
                      조기진급
                    </span>
                    <div className="mt-1 text-xs text-slate-400">
                      S등급 {candidate.sGradeCount}회
                    </div>
                  </div>
                ) : (
                  <span className="rounded-full bg-blue-50 px-2.5 py-1 text-xs font-bold text-blue-700">
                    일반진급
                  </span>
                )}
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}
