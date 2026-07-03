"use client";

import { useState } from "react";
import { useRouter } from "next/navigation";
import FullCalendar from "@fullcalendar/react";
import dayGridPlugin from "@fullcalendar/daygrid";
import { apiRequest } from "@/lib/api";

const TYPE_COLOR = {
  COMPANY: "#ef4444",
  DEPT: "#8b5cf6",
  PERSONAL: "#3b82f6",
};

const TYPE_LABEL = {
  COMPANY: "전사",
  DEPT: "부서",
  PERSONAL: "개인",
};

export default function ScheduleMiniCalendar() {
  const router = useRouter();
  const [events, setEvents] = useState([]);
  const [holidays, setHolidays] = useState(new Set());
  const [selectedDate, setSelectedDate] = useState(null);

  const fetchSchedules = async (startDt, endDt) => {
    try {
      const res = await apiRequest(`/api/schedule?startDt=${startDt}&endDt=${endDt}`);
      const data = await res.json();
      setEvents(
        data.map((s) => ({
          id: String(s.scheduleId),
          title: s.title,
          start: s.startTime ? `${s.startDt}T${s.startTime}` : s.startDt,
          end: s.endTime ? `${s.endDt}T${s.endTime}` : s.endDt,
          allDay: !s.startTime,
          color: TYPE_COLOR[s.scheduleType] ?? TYPE_COLOR.PERSONAL,
          extendedProps: {
            scheduleType: s.scheduleType,
            startTime: s.startTime,
            endTime: s.endTime,
            location: s.location,
          },
        }))
      );
    } catch {
      console.error("일정 조회 실패");
    }
  };

  const fetchHolidays = async (year) => {
    try {
      const res = await apiRequest(`/api/schedule/holidays?year=${year}`);
      const data = await res.json();
      setHolidays(new Set(data.map((h) => h.holidayDate)));
    } catch {
      console.error("공휴일 조회 실패");
    }
  };

  const handleDatesSet = (info) => {
    const start = info.startStr.slice(0, 10);
    const end = info.endStr.slice(0, 10);
    fetchSchedules(start, end);
    fetchHolidays(new Date(info.startStr).getFullYear());
  };

  const handleDateClick = (info) => {
    setSelectedDate(info.dateStr);
  };

  const dayEvents = selectedDate
    ? events.filter((e) => e.start.slice(0, 10) === selectedDate)
    : [];

  return (
    <div className="h-full flex flex-col gap-2">
      <div className="flex items-center justify-between">
        <h2 className="text-sm font-semibold text-slate-700">일정</h2>
        <button
          onClick={() => router.push("/work/schedule")}
          className="text-xs text-blue-600 hover:underline"
        >
          전체 일정 보기 →
        </button>
      </div>

      <div className="flex-1 min-h-0 mini-schedule-calendar">
        <FullCalendar
          plugins={[dayGridPlugin]}
          initialView="dayGridMonth"
          locale="ko"
          headerToolbar={{ left: "prev,next today", center: "title", right: "" }}
          buttonText={{ today: "오늘" }}
          height="100%"
          events={events}
          dayMaxEvents={4}
          datesSet={handleDatesSet}
          dateClick={handleDateClick}
          eventContent={(arg) => ({
            html: `<span style="background-color:${arg.event.backgroundColor}" class="inline-block w-1.5 h-1.5 rounded-full mx-[1px]" title="${arg.event.title}"></span>`,
          })}
          dayCellDidMount={(arg) => {
            const dateStr = arg.date.toISOString().slice(0, 10);
            if (holidays.has(dateStr)) {
              const el = arg.el.querySelector(".fc-daygrid-day-number");
              if (el) el.style.color = "#e53e3e";
            }
          }}
        />
      </div>

      {selectedDate && (
        <div className="border-t border-slate-100 pt-2 text-xs text-slate-600 max-h-20 overflow-y-auto">
          <p className="font-medium text-slate-500 mb-1">{selectedDate}</p>
          {dayEvents.length === 0 ? (
            <p className="text-slate-400">등록된 일정이 없습니다.</p>
          ) : (
            dayEvents.map((e) => (
              <div key={e.id} className="flex items-center gap-1.5 py-0.5">
                <span
                  className="w-1.5 h-1.5 rounded-full flex-shrink-0"
                  style={{ backgroundColor: e.color }}
                />
                <span className="truncate">{e.title}</span>
                <span className="text-slate-400 flex-shrink-0">
                  {TYPE_LABEL[e.extendedProps.scheduleType]}
                </span>
              </div>
            ))
          )}
        </div>
      )}
    </div>
  );
}
