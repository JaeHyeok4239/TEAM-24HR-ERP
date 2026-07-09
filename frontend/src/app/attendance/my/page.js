"use client";

import { useRef, useState, useEffect} from "react";
import { apiRequest } from "@/lib/api";
import { useDateNavigation } from "@/hooks/useDateNavigation";
import Calendar from '@/components/attendance/Calendar';
import PageHeader from '@/components/attendance/PageHeader';
import { getMeRequest, getAttendanceDetailRequest, getStatusLabel, getStatusColor } from "@/services/attendanceService";
import DetailPanel from "@/components/attendance/DetailPanel";
import dayjs from 'dayjs';

export default function AttendanceUserPage(){
    const calendarRef = useRef(null);
    const [stats, setStats] = useState(null); // 월별 근태 조회
    const [events, setEvents] = useState([]); // 달력 이벤트 상태 추가
    const [myEmployeeId, setMyEmployeeId] = useState(null);

    // 내 정보 가져오기
    useEffect(() => {
        const fetchMyInfo = async () => {
            try {
                const meData = await getMeRequest();
                setMyEmployeeId(meData.employeeId);
            } catch (error) {
                console.error("내 정보 불러오기 실패:", error);
            }
        };
        fetchMyInfo();
    }, []);

    // 패널용
    const [panelOpen, setPanelOpen] = useState(false);
    const [selectedDate, setSelectedDate] = useState(null);
    const [selectedData, setSelectedData] = useState({});

    const { currentDate, handlePrev, handleNext, handleToday, handleDatesSet, updateDate, isNextDisabled } = useDateNavigation(calendarRef);

    const handleDateClick = async (info) => {
        if (!myEmployeeId) return;
        
        // 날짜 유효성 검사
        if (dayjs(info.dateStr).isAfter(dayjs(), 'day')) {
            console.log("오늘 이후 날짜는 선택할 수 없습니다.");
            return; 
        }   

        try {
            const detailData = await getAttendanceDetailRequest(info.dateStr, myEmployeeId);
            
            setSelectedData(detailData);
            setSelectedDate(info.dateStr);
            setPanelOpen(true);
            
            if (calendarRef.current) {
                calendarRef.current.getApi().gotoDate(info.dateStr);
            }
        } catch (error) {
            console.error("상세 정보 조회 실패:", error);
        }
    };

    // stats 없을 시 0
    const displayStats = stats || { workCount: 0, lateCount: 0, absenceCount: 0, vacationCount: 0 };

    // 월별 통계 및 달력 뱃지 데이터 가져오기
    useEffect(() => {
        const fetchMyInfoAndAttendance = async () => {
            try {
                // 날짜 포맷 맞추기
                const [y, m] = currentDate.split('.'); 
                const formattedDate = `${y}-${m.padStart(2, '0')}`;
                
                // 월별 통계 데이터(employeeId 없이 호출)
                const statsResponse = await apiRequest(`/api/attendance/monthly/summary?yearMonth=${formattedDate}`);
                const statsData = await statsResponse.json();
                setStats(statsData);

                // 달력 뱃지 데이터 
                const calendarResponse = await apiRequest(`/api/attendance/monthly/calendar/me?yearMonth=${formattedDate}`);
                const calendarData = await calendarResponse.json();

                // 풀캘린더 형식에 맞게 데이터 변환
                const formattedEvents = Array.isArray(calendarData) 
                  ? calendarData.map(item => ({
                    start: item.date,
                    classNames: ['custom-event-style'],
                    extendedProps: {
                        status: item.status,
                        isCheckoutMissing: item.isCheckoutMissing
                    }
                })) 
                : [];
                setEvents(formattedEvents);
            } catch (error) {
                console.error("통계 조회 실패:", error);
            }
        };

        if (currentDate) {
            fetchMyInfoAndAttendance();
        }
    }, [currentDate]);

    // FullCalendar 이벤트 렌더링 함수
    const renderEventContent = (eventInfo) => {
        const { status, checkoutMissing } = eventInfo.event.extendedProps; // 필드명 변경
        console.log(`[My Page] Date: ${eventInfo.event.startStr}, Status: ${status}, checkoutMissing: ${checkoutMissing}`); // 디버깅용
        const primaryLabel = getStatusLabel(status);
        const statusColor = getStatusColor(status);
        const textColor = (status === 'READY' || !status) ? '#111827' : 'white';

        return (
            <div className="fc-event-main-custom">
                <div style={{ backgroundColor: statusColor, color: textColor }} className="primary-badge">
                    {primaryLabel}
                </div>
                {checkoutMissing && ( // boolean 값 직접 사용
                    <div className="secondary-badge">
                        미퇴근
                    </div>
                )}
            </div>
        );
    };

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
                eventContent={renderEventContent}
                dateClick={handleDateClick}
                />
            </div>

            <DetailPanel 
                isOpen={panelOpen}
                onClose={() => setPanelOpen(false)}
                date={selectedDate}
                userType="me"
                data={selectedData}
            />
        </main>
    )
}