import { Card, CardContent } from "@/components/ui/card";
import { AttendanceSummaryCell } from "./AttendanceSummaryCell";

// 직원 목록
// map 함수 때문에 employees가 들어오지 않았을 때를 대비해 빈 배열로 설정
export default function AttendanceEmployeeList({ employees = [], isLoading, onSelect, type }) {
  return (
    <Card className="!py-0 h-full rounded-xl border-slate-200 bg-white shadow-sm overflow-hidden flex flex-col">
      <CardContent className="p-0 h-full overflow-y-auto">
        <table className="w-full text-center border-collapse relative">
            <thead className="sticky top-0 bg-white z-10 shadow-sm">
                <tr className="border-b border-slate-100 text-sm text-slate-500">
                <th className="px-6 py-4 font-medium">이름</th>
                {/* 정규직일 때만 나오는 필드 */}
                {type === 'regular' && (
                    <>
                        <th className="px-6 py-4 font-medium">부서</th>
                        <th className="px-6 py-4 font-medium">직급</th>
                    </>
                )}
                <th className="px-6 py-4 font-medium">이번 달 근태 요약</th>
                </tr>
            </thead>
          <tbody className="divide-y divide-slate-100">
            {isLoading ? (
              <tr>
                <td colSpan="4" className="py-10 text-center text-slate-400">데이터를 불러오는 중</td>
              </tr>
            ) : employees.map((emp) => (
              <tr key={emp.employeeId} className="hover:bg-slate-50 transition-colors text-sm">
                <td className="px-6 py-4 font-semibold text-slate-900">{emp.name}</td>
                {/* 정규직일 때만 나오는 필드 */}
                {type === 'regular' && (
                    <>
                    <td className="px-6 py-4 text-slate-600">{emp.departmentName}</td>
                    <td className="px-6 py-4 text-slate-600">{emp.positionName}</td>
                    </>
                )}
                <td className="px-6 py-4 flex justify-center">
                  <AttendanceSummaryCell
                    work={emp.workCount} 
                    late={emp.lateCount} 
                    absent={emp.absentCount} 
                    leave={emp.leaveCount} 
                  />
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </CardContent>
    </Card>
  );
}