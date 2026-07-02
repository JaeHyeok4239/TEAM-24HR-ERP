import MyDocument from "@/components/approval/DocumentList";
import AttendancePage from "@/components/dashboard/AttendancePage";
import ScheduleMiniCalendar from "@/components/dashboard/ScheduleMiniCalendar";

export default function MainPage() {
  return (
    <div className="p-4 flex flex-col gap-4 h-full">
      {/* 상단 2열 */}
      <div className="grid grid-cols-1 xl:grid-cols-2 gap-4 h-1/2">
        <div className="bg-white rounded-xl border border-slate-200 p-4 flex flex-col gap-3">
        {/* 지도 및 출퇴근 */}
        <div className="w-full h-100">
          <AttendancePage />
        </div>
        </div>
        <div className="bg-white rounded-xl border border-slate-200 p-4">
          <ScheduleMiniCalendar />
        </div>
        {/* MyDocument는 아래로 전체 폭 */}
        <div className="lg:col-span-3 bg-white rounded-xl border border-slate-200 p-4">
          <MyDocument />
        </div>
      </div>
    </div>
  );
}
