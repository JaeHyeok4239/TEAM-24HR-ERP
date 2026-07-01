// 직원 목록 이번 달 근태 요약 컴포넌트(0/0/0/0 이런 식으로 표기)
export const AttendanceSummaryCell = ({ work, late, absent, leave }) => {
  return (
    <div className="flex gap-1 font-bold text-sm">
        {/* 값이 없다면 0으로 표기 */}
        <span className="text-green-500">{work ?? 0}</span>/
        <span className="text-orange-400">{late ?? 0}</span>/
        <span className="text-red-500">{absent ?? 0}</span>/
        <span className="text-purple-500">{leave ?? 0}</span>
    </div>
  );
};