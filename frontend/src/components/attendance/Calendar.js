"use client";

import { forwardRef } from "react";
import FullCalendar from "@fullcalendar/react";
import dayGridPlugin from "@fullcalendar/daygrid";
import timeGridPlugin from "@fullcalendar/timegrid";
import interactionPlugin from "@fullcalendar/interaction";

const Calendar = forwardRef(({ onDatesSet }, ref) => {
    return(
        <div className="h-full w-full bg-white rounded-lg shadow-sm overflow-hidden">
            <FullCalendar
                ref={ref}
                plugins={[dayGridPlugin, timeGridPlugin, interactionPlugin]}
                headerToolbar={false} // 툴바 없애고 page에서 헤더에 버튼 추가
                initialView="dayGridMonth"
                datesSet={onDatesSet}

                height="100%" // 높이 자동 조절
                expandRows={true}
                fixedWeekCount={true}
                locale="ko" // 요일 한글 처리 
                
                // 날짜칸 '일' 제거 및 패딩값 넣기
                dayCellContent={(arg) => {
                    return (
                        <div className="px-2 py-1">
                            {arg.dayNumberText.replace('일', '')}
                        </div>
                    );
                }}
            />
        </div>
    );
});
Calendar.displayName = "Calendar";
export default Calendar;