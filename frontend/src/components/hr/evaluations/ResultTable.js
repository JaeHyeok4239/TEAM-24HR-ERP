import { EmptyBox } from "./EvaluationCommon";

export default function ResultTable({
  results,
  isLoading,
  selectedEmployeeId,
  onSelectEmployee,
}) {
  if (isLoading) {
    return <EmptyBox message="평가 결과를 불러오는 중입니다." />;
  }

  if (results.length === 0) {
    return <EmptyBox message="평가 결과가 없습니다." />;
  }

  return (
    <div className="mt-4 overflow-hidden rounded-lg border border-slate-200">
      <table className="w-full border-collapse text-sm">
        <thead className="bg-slate-50 text-xs font-semibold text-slate-500">
          <tr>
            <th className="px-4 py-3 text-left">직원</th>
            <th className="px-4 py-3 text-left">부서</th>
            <th className="px-4 py-3 text-left">직급</th>
            <th className="px-4 py-3 text-right">평가횟수</th>
            <th className="px-4 py-3 text-right">누적점수</th>
            <th className="px-4 py-3 text-right">최근점수</th>
            <th className="px-4 py-3 text-left">진급심사</th>
          </tr>
        </thead>

        <tbody className="divide-y divide-slate-100">
          {results.map((result) => {
            const selected = selectedEmployeeId === result.employeeId;

            return (
              <tr
                key={result.employeeId}
                role="button"
                tabIndex={0}
                onClick={() => onSelectEmployee(result)}
                onKeyDown={(event) => {
                  if (event.key === "Enter") {
                    onSelectEmployee(result);
                  }
                }}
                className={`cursor-pointer transition ${
                  selected ? "bg-slate-100" : "bg-white hover:bg-slate-50"
                }`}
              >
                <td className="px-4 py-3">
                  <div className="font-semibold text-slate-900">
                    {result.employeeName}
                  </div>
                  <div className="mt-0.5 text-xs text-slate-400">
                    {result.employeeNo}
                  </div>
                </td>

                <td className="px-4 py-3 text-slate-600">
                  {result.departmentName ?? "-"}
                </td>

                <td className="px-4 py-3 text-slate-600">
                  {result.positionName ?? "-"}
                </td>

                <td className="px-4 py-3 text-right font-semibold">
                  {result.evaluationCount}
                </td>

                <td className="px-4 py-3 text-right font-semibold">
                  {result.totalScore}
                </td>

                <td className="px-4 py-3 text-right">
                  {result.latestScore ?? "-"}
                </td>

                <td className="px-4 py-3">
                  {result.promotionCandidate ? (
                    <span className="rounded-full bg-blue-50 px-2.5 py-1 text-xs font-bold text-blue-700">
                      대상
                    </span>
                  ) : (
                    <span className="rounded-full bg-slate-100 px-2.5 py-1 text-xs font-bold text-slate-500">
                      미대상
                    </span>
                  )}
                </td>
              </tr>
            );
          })}
        </tbody>
      </table>
    </div>
  );
}