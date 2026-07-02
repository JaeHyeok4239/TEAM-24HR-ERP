import ApprovalBox from "@/components/approval/ApprovalBox";
import MyDocument from "@/components/approval/DocumentList";
import AttendancePage from "@/components/dashboard/AttendancePage";
import ScheduleMiniCalendar from "@/components/dashboard/ScheduleMiniCalendar";

export default function MainPage() {
  return (
<<<<<<< HEAD
    <div className="p-4 flex flex-col gap-4 xl:h-full md:h-max min-h-0">
      {/* 상단 2열 - 남은 공간의 비율 지정 */}
      <div className="grid grid-cols-1 xl:grid-cols-2 gap-4 flex-[2] min-h-0">
        <div className="bg-white rounded-xl border border-slate-200 p-4 flex flex-col gap-3 min-h-0 overflow-y-auto">
=======
    <div className="p-4 flex flex-col gap-4 h-full">
      {/* 상단 2열 */}
      <div className="grid grid-cols-1 xl:grid-cols-2 gap-4 h-1/2">
        <div className="bg-white rounded-xl border border-slate-200 p-4 flex flex-col gap-3">
        {/* 지도 및 출퇴근 */}
        <div className="w-full h-100">
>>>>>>> refs/remotes/origin/develop
          <AttendancePage />
        </div>
        <div className="bg-white rounded-xl border border-slate-200 p-4 min-h-[420px]">
          <ScheduleMiniCalendar />
        </div>
      </div>

      <div className="grid grid-cols-1 xl:grid-cols-2 gap-4 flex-[2] min-h-0">
        <div className="bg-white rounded-xl border border-slate-200 p-4 flex flex-col gap-3 min-h-0 overflow-y-auto">
          <MyDocument />
        </div>
        <div className="bg-white rounded-xl border border-slate-200 p-4 min-h-0">
          <ApprovalBox />
        </div>
      </div>
    </div>
  );
}
