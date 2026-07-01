import MyDocument from "@/components/approval/DocumentList";
import AttendancePage from "@/components/dashboard/AttendancePage";

export default function MainPage() {
  return (
    <div className="p-4 flex flex-col gap-4 h-full">
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-4 h-full">
        <div className="lg:col-span-2 bg-white rounded-xl border border-slate-200 p-4">
          <AttendancePage />
        </div>
        <div className="bg-white rounded-xl border border-slate-200 p-4">
          {/* 일정 캘린더 */}
        </div>
        {/* MyDocument는 아래로 전체 폭 */}
        <div className="lg:col-span-3 bg-white rounded-xl border border-slate-200 p-4">
          <MyDocument />
        </div>
      </div>
    </div>
  );
}
