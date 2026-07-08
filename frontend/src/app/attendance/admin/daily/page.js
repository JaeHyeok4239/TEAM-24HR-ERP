"use client";

import { useRef, useState, useEffect } from "react";
import { useDateNavigation } from "@/hooks/useDateNavigation";
import { getHrEmployeesRequest } from "@/services/hrEmployeeService";
import { apiRequest } from "@/lib/api";
import PageHeader from '@/components/attendance/PageHeader';
import SummaryDaily from "@/components/attendance/admin/SummaryDaily";
import AttendanceEmployeeList from "@/components/attendance/admin/AttendanceEmployeeList";
import AdminCalendar from "@/components/attendance/admin/AdminCalendar";
import {getMonthlyCalendarEvents } from "@/services/attendanceService";
import AttendanceDailyInputPanel from "@/components/attendance/AttendanceDailyInputPanel";
import dayjs from 'dayjs';

export default function AttendanceDailyPage() {
    const [employees, setEmployees] = useState([]);
    const [isLoading, setIsLoading] = useState(true);
    const [selectedEmployee, setSelectedEmployee] = useState(null);
    const calendarRef = useRef(null);
    const { currentDate, handlePrev, handleNext, handleToday, updateDate, isNextDisabled } = useDateNavigation(calendarRef);
    const [events, setEvents] = useState([]);
    const [selectedStats, setSelectedStats] = useState(null);
    const [isInputOpen, setIsInputOpen] = useState(false);

    const handleDateClick = (info) => {
        updateDate(info.dateStr); 
    };
    
    // getHrEmployeesRequest API, active monthly 근태 조회 호출
    useEffect(() => {
        const fetchData = async () => {
            setIsLoading(true);
            try {
                // 일용직 직원 목록 조회
                const empData = await getHrEmployeesRequest({ employmentType: 'DAILY' });
                
                // 해당 날짜의 일용직 근태 데이터 조회
                const statsData = await apiRequest(`/api/attendance/summary-monthly-all?yearMonth=${currentDate.substring(0, 7)}`)
                    .then(res => res.json());

                // 데이터 합치기
                const mergedEmployees = empData.map(emp => {
                    const stats = statsData.employeeStatsList.find(s => s.employeeId === emp.employeeId);
                    return {
                        ...emp,
                        workCount: stats?.workCount || 0,
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

                const calendarData = await getMonthlyCalendarEvents(formattedDate, selectedEmployee.employeeId);
                console.log("서버에서 받은 원본 데이터:", calendarData);

                const formattedEvents = (calendarData || [])
                    .filter(item => item.status === 'WORK' || item.status === 'OUT')
                    .map(item => ({
                        title: '출근',
                        start: item.date,
                        backgroundColor: '#10b981',
                        borderColor: '#10b981',
                        classNames: ['attendance-badge']
                    }));
                console.log("최종 전달할 events 데이터:", formattedEvents);
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
                title="일용직 근태 관리" 
                currentDate={currentDate}
                onPrev={handlePrev}
                onNext={handleNext}
                onToday={handleToday}
                onDateChange={(date) => updateDate(date)}
                isNextDisabled={isNextDisabled}
            />

            <button 
                onClick={() => setIsInputOpen(true)} 
                className="bg-blue-600 text-white px-5 py-2 rounded-lg font-bold"
            >
                근태 기록 관리
            </button>
            
            {/* 일별 근태 조회 */}
            <div className="mt-4">
                <SummaryDaily date={currentDate} type="daily" />
            </div>

            {/* 필터링 버튼 */}
            
            {/* 직원 목록/상세 달력 레이아웃 */}
            <div className="grid grid-cols-[550px_1fr] gap-4 mt-2 h-[calc(100vh-250px)]">
                <AttendanceEmployeeList 
                    employees={employees}
                    isLoading={isLoading}
                    onSelect={(emp) => setSelectedEmployee(emp)}
                    type="daily"
                />
                <AdminCalendar 
                    selectedEmployee={selectedEmployee}
                    currentDate={currentDate}
                    type="daily"
                    events={events}
                    stats={selectedStats}
                    dateClick={handleDateClick}
                />
            </div>

            <AttendanceDailyInputPanel 
                isOpen={isInputOpen} 
                onClose={() => setIsInputOpen(false)} 
                selectedDate={currentDate.replaceAll('.', '-')}
            />
        </main>
    );
}