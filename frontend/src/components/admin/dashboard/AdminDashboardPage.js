import EmptyDashboardSlot from "./EmptyDashboardSlot";
import HrEmployeeFlowCard from "./HrEmployeeFlowCard";

export default function AdminDashboardPage() {
  return (
    <div className="flex h-full min-h-0 flex-col p-4">
      <div className="grid min-h-0 flex-1 grid-rows-[minmax(0,1fr)_minmax(0,1fr)] gap-4">
        <div className="grid min-h-0 grid-cols-1 gap-4 xl:grid-cols-2">
          <HrEmployeeFlowCard />
          <EmptyDashboardSlot title="상단 영역 2" />
        </div>

        <div className="grid min-h-0 grid-cols-1 gap-4 md:grid-cols-2">
          <EmptyDashboardSlot title="하단 영역 1" />
          <EmptyDashboardSlot title="하단 영역 2" />
        </div>
      </div>
    </div>
  );
}