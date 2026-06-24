import EmptyDashboardSlot from "./EmptyDashboardSlot";
import HrEmployeeFlowCard from "./HrEmployeeFlowCard";

export default function AdminDashboardPage() {
  return (
    <div className="flex h-full min-h-0 flex-col gap-4 p-4">
      <div className="shrink-0">
        <p className="text-sm font-semibold text-slate-500">관리자</p>
        <h1 className="mt-1 text-2xl font-bold text-slate-950">
          관리자 대시보드
        </h1>
        <p className="mt-1 text-sm text-slate-500">
          시스템 운영 현황과 주요 지표를 확인하는 관리자 전용 화면입니다.
        </p>
      </div>

      <div className="grid min-h-0 flex-1 grid-rows-2 gap-4">
        {/* 상단 2열 */}
        <div className="grid min-h-0 grid-cols-1 gap-4 xl:grid-cols-2">
          <HrEmployeeFlowCard />
          <EmptyDashboardSlot title="상단 영역 2" />
        </div>

        {/* 하단 2열 */}
        <div className="grid min-h-0 grid-cols-1 gap-4 md:grid-cols-2">
          <EmptyDashboardSlot title="하단 영역 1" />
          <EmptyDashboardSlot title="하단 영역 2" />
        </div>
      </div>
    </div>
  );
}