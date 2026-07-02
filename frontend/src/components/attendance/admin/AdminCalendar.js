import Calendar from '@/components/attendance/Calendar'; // 기존 Calendar 컴포넌트 재사용
import { useEffect, useState } from 'react';
import { getMonthlyAttendanceStats } from "@/services/attendanceService";

export default function AttendanceCalendar({ selectedEmployee, currentDate, calendarRef, onDatesSet, type }) {
    const [attendanceData, setAttendanceData] = useState(null);

    // 직원 혹은 날짜 변경될 때마다 실행
    useEffect(() => {
        if (!selectedEmployee || !currentDate) return;

        const fetchEmployeeMonthlyData = async () => {
            try {
                // 날짜 포맷 맞추기
                const [y, m] = currentDate.split('.');
                const formattedDate = `${y}-${m.padStart(2, '0')}`;
                
                const data = await getMonthlyAttendanceStats(formattedDate, selectedEmployee.employeeId);
                console.log("백엔드 응답 데이터:", data);
                setAttendanceData(data);
            } catch (error) {
                console.error("데이터 로딩 실패:", error);
            }
        };
        fetchEmployeeMonthlyData();
    }, [selectedEmployee, currentDate]);

    // 직원 선택 하지 않았을 때
    if (!selectedEmployee) {
        return (
            <div className="flex flex-col items-center justify-center h-full text-gray-500 rounded-xl border-slate-200 bg-white shadow-sm">
                <p className="font-bold text-xl">직원을 선택해주세요.</p><br/>
                <p className="text-center">직원을 선택하면 해당 직원의<br/>월별 근태 현황을 파악할 수 있습니다.</p>
            </div>
        );
    }
    // 직원 선택 시
    return (
        <div className="flex flex-col h-full">
            {/* 직원 이름+근태 현황 헤더 */}
            <div className="flex items-center justify-between mb-4 px-2">
                <h3 className="font-bold text-lg text-slate-800">
                    {selectedEmployee.name} · {currentDate.split('.')[0]}년 {currentDate.split('.')[1]}월 근태 현황
                </h3>
                
                {/* 1명 월별 근태 조회 */}
                {attendanceData && (
                    <div className="flex gap-4 text-sm font-bold text-gray-700">
                        <span>출근 <span className="text-green-500">{attendanceData.workCount}</span>회</span>
                        {/* 정규직 */}
                        {type === 'regular' && (
                            <>
                                <span>지각 <span className="text-yellow-500">{attendanceData.lateCount}</span>회</span>
                                <span>결근 <span className="text-red-500">{attendanceData.absentCount}</span>회</span>
                                <span>휴가 <span className="text-purple-500">{attendanceData.leaveCount}</span>회</span>
                            </>
                        )}
                    </div>
                )}
            </div>

            {/* 달력 */}
            <div className="flex-1 overflow-hidden">
                <Calendar 
                    ref={calendarRef} 
                    onDatesSet={onDatesSet} 
                />
            </div>
        </div>
    );
}