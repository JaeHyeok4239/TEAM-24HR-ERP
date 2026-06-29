"use client";

import { useRef, useState, useEffect } from "react";
import Calendar from '@/components/attendance/Calendar';
import PageHeader from '@/components/attendance/PageHeader';
import { useCalendar } from "@/hooks/useCalendar";

export default function AttendanceUserPage(){
    const calendarRef = useRef(null);
    // const [currentDate, setCurrentDate] = useState(""); // 날짜 텍스트 상태
    const [stats, setStats] = useState(null); // 월별 통계 데이터 상태
    // stats 없을 시 0
    const displayStats = stats || { workCount: 0, lateCount: 0, absenceCount: 0, vacationCount: 0 };

    const { currentDate, handleDatesSet } = useCalendar();

    const fetchAttendanceStats = async (yearMonth) => {
        try {
            // 날짜 포맷 변환
            const [y, m] = yearMonth.split('.');
            const formattedDate = `${y}-${m.padStart(2, '0')}`;
            
            // targetEmployeeId 생략
            const response = await fetch(`/api/attendance/summary?yearMonth=${formattedDate}`);
            
            if (!response.ok) throw new Error("데이터를 가져올 수 없습니다.");
            const data = await response.json();
            
            // 백엔드 응답 구조에 맞게 상태 저장
            setStats(data);
        } catch (error) {
            console.error("통계 조회 실패:", error);
        }
    };

    // 날짜가 바뀔 때마다 실행
    useEffect(() => {
        if (currentDate) {
            fetchAttendanceStats(currentDate);
        }
    }, [currentDate]);

    return(
        <main className="h-[calc(100vh-2rem)] flex flex-col p-4 overflow-hidden">
            {/* 헤더 */}
            <PageHeader 
                title="내 근태 현황" 
                calendarRef={calendarRef} 
                currentDate={currentDate} 
            />

            {/* 월별 통계 */}
            <div className="my-4 p-4 bg-white border border-gray-100 rounded-lg shadow-sm flex gap-6 text-sm font-bold text-gray-700">
                <span>출근 <span className="text-green-500">{displayStats.workCount}</span>회</span>
                <span>지각 <span className="text-yellow-500">{displayStats.lateCount}</span>회</span>
                <span>결근 <span className="text-red-500">{displayStats.absenceCount}</span>회</span>
                <span>휴가 <span className="text-purple-500">{displayStats.vacationCount}</span>회</span>
            </div>

            {/* 달력 */}
            <div className="flex-1 w-full overflow-hidden rounded-lg shadow-sm">
                <Calendar ref={calendarRef} onDatesSet={handleDatesSet} />
            </div>
        </main>
    )
}