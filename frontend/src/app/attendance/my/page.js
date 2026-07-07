"use client";

import { useRef, useState, useEffect} from "react";
import { useDateNavigation } from "@/hooks/useDateNavigation";
import Calendar from '@/components/attendance/Calendar';
import PageHeader from '@/components/attendance/PageHeader';
import { apiRequest } from "@/lib/api";
import { getStatusLabel, getStatusColor } from "@/services/attendanceService";
import DetailPanel from "@/components/attendance/DetailPanel";
import dayjs from 'dayjs';

export default function AttendanceUserPage(){
    const calendarRef = useRef(null);
    const [stats, setStats] = useState(null); // 월별 근태 조회
    const [events, setEvents] = useState([]); // 달력 이벤트 상태 추가

    // 패널용
    const [panelOpen, setPanelOpen] = useState(false);
    const [selectedDate, setSelectedDate] = useState(null);
    const [selectedData, setSelectedData] = useState({});

    const { currentDate, handlePrev, handleNext, handleToday, handleDatesSet, updateDate, isNextDisabled } = useDateNavigation(calendarRef);

    const handleDateClick = (info) => {
        // 날짜 유효성 검사
        if (dayjs(info.dateStr).isAfter(dayjs(), 'day')) {
            console.log("오늘 이후 날짜는 선택할 수 없습니다.");
            return; 
        }   

        updateDate(info.dateStr);
        setSelectedData({
            status: '출근',
            time: '09:00 - 18:00',
            baseTime: '8시간',
            overTime: '1시간',
            totalTime: '9시간',
        });
        setSelectedDate(info.dateStr);
        setPanelOpen(true);
    };

    // stats 없을 시 0
    const displayStats = stats || { workCount: 0, lateCount: 0, absenceCount: 0, vacationCount: 0 };

    // 월별 통계 및 달력 뱃지 데이터 가져오기
    useEffect(() => {
        if (!currentDate) return;

        const fetchAttendanceStats = async () => {
            try {
                // 날짜 포맷 맞추기
                const [y, m] = currentDate.split('.'); 
                const formattedDate = `${y}-${m.padStart(2, '0')}`;
                
                // 월별 통계 데이터
                const statsResponse = await apiRequest(`/api/attendance/monthly/summary?yearMonth=${formattedDate}`);
                const statsData = await statsResponse.json();
                setStats(statsData);

                // 달력 뱃지 데이터 
                const calendarResponse = await apiRequest(`/api/attendance/monthly/calendar/me?yearMonth=${formattedDate}`);
                const calendarData = await calendarResponse.json();

                // 풀캘린더 형식에 맞게 데이터 변환
                const formattedEvents = calendarData.map(item => ({
                    title: getStatusLabel(item.status),
                    start: item.date,
                    backgroundColor: getStatusColor(item.status),
                    borderColor: getStatusColor(item.status),
                    classNames: ['attendance-badge']
                }));
                
                setEvents(formattedEvents);
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
                onDateChange={(date) => updateDate(date)}
                isNextDisabled={isNextDisabled}
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
                <Calendar 
                ref={calendarRef} 
                onDatesSet={handleDatesSet}
                events={events}
                dateClick={handleDateClick}
                />
            </div>

            <DetailPanel 
                isOpen={panelOpen}
                onClose={() => setPanelOpen(false)}
                date={selectedDate}
                userType="regular"
                data={selectedData}
            />
        </main>
    )
}