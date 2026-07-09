"use client";

import { useRef, useState, useEffect } from "react";
import { useDateNavigation } from "@/hooks/useDateNavigation";
import { getHrEmployeesRequest } from "@/services/hrEmployeeService";
import { apiRequest } from "@/lib/api";
import PageHeader from '@/components/attendance/PageHeader';
import SummaryDaily from "@/components/attendance/admin/SummaryDaily";
import AttendanceEmployeeList from "@/components/attendance/admin/AttendanceEmployeeList";
import AdminCalendar from "@/components/attendance/admin/AdminCalendar";
import { getMonthlyAttendanceStats, getMonthlyCalendarEvents, getStatusLabel, getStatusColor } from "@/services/attendanceService";

export default function AttendanceRegularPage() {
    const [employees, setEmployees] = useState([]);
    const [isLoading, setIsLoading] = useState(true);
    const calendarRef = useRef(null);
    const [selectedEmployee, setSelectedEmployee] = useState(null); // 직원 선택
    const [events, setEvents] = useState([]); // events 상태 추가
    const [selectedStats, setSelectedStats] = useState(null);
    const { currentDate, handlePrev, handleNext, handleToday, handleDatesSet, updateDate, isNextDisabled } = useDateNavigation(calendarRef);

    // FullCalendar 이벤트 렌더링 함수
    const renderEventContent = (eventInfo) => {
        const { status, checkoutMissing } = eventInfo.event.extendedProps; // 필드명 변경
        console.log(`[Regular Page] Date: ${eventInfo.event.startStr}, Status: ${status}, checkoutMissing: ${checkoutMissing}`); // 디버깅용
        const primaryLabel = getStatusLabel(status);
        const primaryColor = getStatusColor(status);

        return (
            <div className="fc-event-main-custom">
                <div style={{ backgroundColor: primaryColor }} className="primary-badge">
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
    

    // getHrEmployeesRequest API, active monthly 근태 조회 호출
    useEffect(() => {
        const fetchData = async () => {
            setIsLoading(true);
            try {
                // 직원 목록 가져오기
                const empData = await getHrEmployeesRequest({ employmentType: 'REGULAR' });
                
                // 월별 근태 조회 가져오기
                const statsData = await apiRequest(`/api/attendance/summary-monthly-all?yearMonth=${currentDate.substring(0, 7)}`)
                    .then(res => res.json());

                // 데이터 합치기
                const mergedEmployees = empData.map(emp => {
                    const stats = statsData.employeeStatsList.find(s => s.employeeId === emp.employeeId);
                    return {
                        ...emp,
                        workCount: stats?.workCount || 0,
                        lateCount: stats?.lateCount || 0,
                        absentCount: stats?.absentCount || 0,
                        leaveCount: stats?.leaveCount || 0
                    };
                });

                setEmployees(mergedEmployees);
            } catch (error) {
                console.error("데이터 로딩 실패:", error);
            } finally {
                setIsLoading(false);
            }
        };

        fetchData();
    }, [currentDate]);

    // 달력 뱃지
    useEffect(() => {
        const fetchSelectedEmployeeData = async () => {
            // 직원 선택이 해제되었을 때 초기화
            if (!selectedEmployee || !currentDate) {
                setEvents([]);
                setSelectedStats(null);
                return;
            }

            try {
                const [y, m] = currentDate.split('.');
                const formattedDate = `${y}-${m.padStart(2, '0')}`;
                
                const stats = await getMonthlyAttendanceStats(formattedDate, selectedEmployee.employeeId);
                setSelectedStats(stats);

                const calendarData = await getMonthlyCalendarEvents(formattedDate, selectedEmployee.employeeId);

                const formattedEvents = calendarData.map(item => ({
                    start: item.date,
                    classNames: ['custom-event-style'],
                    extendedProps: {
                        status: item.status,
                        isCheckoutMissing: item.isCheckoutMissing
                    }
                }));

                setEvents(formattedEvents);
            } catch (error) {
                console.error("데이터 로딩 실패:", error);
            }
        };
        fetchSelectedEmployeeData();
    }, [selectedEmployee, currentDate]);

    return (
        <main className="h-screen p-4">
            {/* 헤더 */}
            <PageHeader 
                title="정규직 근태 관리" 
                currentDate={currentDate}
                onPrev={handlePrev}
                onNext={handleNext}
                onToday={handleToday}
                onDateChange={(date) => updateDate(date.replaceAll('-', '.'))}
                isNextDisabled={isNextDisabled}
            />
            
            {/* 일별 근태 조회 */}
            <div className="mt-4">
                <SummaryDaily date={currentDate} type="regular" />
            </div>

            {/* 필터링 버튼 시간 되면 구현...*/}

            {/* 직원 목록/상세 달력 레이아웃 */}
            <div className="grid grid-cols-[550px_1fr] gap-4 mt-2 h-[calc(100vh-250px)]">
                <AttendanceEmployeeList 
                    employees={employees}
                    isLoading={isLoading}
                    onSelect={(emp) => setSelectedEmployee(emp)}
                    type="regular"
                />
                <AdminCalendar 
                    selectedEmployee={selectedEmployee}
                    currentDate={currentDate}
                    type="regular"
                    events={events}
                    stats={selectedStats}
                    onDatesSet={handleDatesSet}
                    onDateSelect={(date) => updateDate(date.replaceAll('-', '.'))}
                    eventContent={renderEventContent}
                    ref={calendarRef}
                />
            </div>
        </main>
    );
}