import Header from "@/components/Header";
import ScheduleCalendar from "@/components/work/schedule/ScheduleCalendar";

export default function SchedulePage() {
  return (
    <>
      <Header title="일정 관리" />
      <ScheduleCalendar />
    </>
  );
}
