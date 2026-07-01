import AttendancePage from '@/components/dashboard/AttendancePage';
import ScheduleMiniCalendar from '@/components/dashboard/ScheduleMiniCalendar';

export default function MainPage() {
  return (
    <div className="p-4 flex flex-col gap-4 h-full">
      {/* 상단 2열 */}
      <div className="grid grid-cols-1 xl:grid-cols-2 gap-4 h-1/2">
        <div className="bg-white rounded-xl border border-slate-200 p-4 flex flex-col gap-3">
        {/* 지도 영역 */}
        <div className="w-full h-75">
          <AttendancePage />
        </div>
      </div>
        <div className="grid gap-4">
          <div className="bg-white rounded-xl border border-slate-200 p-4">
            <ScheduleMiniCalendar />
          </div>
        </div>
        
      </div>

      {/* 하단 2열 */}
      <div className="grid grid-cols-1 md:grid-cols-2 gap-4 h-1/2">
        <div className="bg-white rounded-xl border border-slate-200 p-4">
          <h1>하단 영역 1</h1>
        </div>
        <div className="bg-white rounded-xl border border-slate-200 p-4">
          <h1>하단 영역 2</h1>
        </div>
      </div>
    </div>
  );
}