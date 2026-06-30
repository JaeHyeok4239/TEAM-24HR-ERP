"use client";

import { useRef, useState, useEffect} from "react";
import { useDateNavigation } from "@/hooks/useDateNavigation";
import Calendar from '@/components/attendance/Calendar';
import PageHeader from '@/components/attendance/PageHeader';
import { apiRequest } from "@/lib/api";

export default function AttendanceUserPage(){
    const calendarRef = useRef(null);
    const [stats, setStats] = useState(null); // 월별 근태 조회
    // stats 없을 시 0
    const displayStats = stats || { workCount: 0, lateCount: 0, absenceCount: 0, vacationCount: 0 };

    // hook 사용
    const { currentDate, handlePrev, handleNext, handleToday, handleDatesSet } = useDateNavigation(calendarRef);

    // 날짜가 바뀔 때마다 실행
    useEffect(() => {
        if (!currentDate) return;

        const fetchAttendanceStats = async () => {
            try {
                // 날짜 포맷 맞추기
                const [y, m] = currentDate.split('.'); 
                const formattedDate = `${y}-${m.padStart(2, '0')}`;
                
                const response = await apiRequest(`/api/attendance/summary?yearMonth=${formattedDate}`);
                const data = await response.json();
                
                setStats(data);
            } catch (error) {
                console.error("통계 조회 실패:", error);
            }
        };
        fetchAttendanceStats();
    }, [currentDate]);

    return(
        <main className="h-[calc(100vh-2rem)] flex flex-col p-4 overflow-hidden">
            {/* 헤더 */}
            <PageHeader 
                title="내 근태 현황"  
                currentDate={currentDate} 
                onPrev={handlePrev}
                onNext={handleNext}
                onToday={handleToday}
            />

            {/* 월별 통계 */}
            <div className="my-4 p-4 bg-white border border-gray-100 rounded-lg shadow-sm flex gap-6 text-sm font-bold text-gray-700">
                <span>출근 <span className="text-green-500">{displayStats.workCount}</span>회</span>
                <span>지각 <span className="text-yellow-500">{displayStats.lateCount}</span>회</span>
                <span>결근 <span className="text-red-500">{displayStats.absentCount}</span>회</span>
                <span>휴가 <span className="text-purple-500">{displayStats.leaveCount}</span>회</span>
            </div>

            {/* 달력 */}
            <div className="flex-1 w-full overflow-hidden rounded-lg shadow-sm">
                <Calendar ref={calendarRef} onDatesSet={handleDatesSet} />
            </div>
        </main>
    )
}