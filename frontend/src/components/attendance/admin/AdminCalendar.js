"use client";

import { useState, forwardRef } from 'react';
import Calendar from '@/components/attendance/Calendar';
import DetailPanel from '../DetailPanel';

const AdminCalendar = forwardRef(({ 
    selectedEmployee, 
    currentDate, 
    type, 
    events, 
    stats, 
    dateClick,
    onDatesSet, 
    onDateSelect,
    eventContent
}, ref) => {
    const [panelOpen, setPanelOpen] = useState(false);
    const [selectedDate, setSelectedDate] = useState(null);
    const [selectedData, setSelectedData] = useState({});

    const handleDateClick = (info) => {
        if (onDateSelect) {
            onDateSelect(info.dateStr);
        }

        if (onDateSelect) {
            onDateSelect(info.dateStr);
        }

        setSelectedData({
            note: '까먹고 너무 늦게 눌렀다고 함'
        });

        dateClick(info);
        setSelectedDate(info.dateStr);
        setPanelOpen(true);
    };

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
                {stats && (
                    <div className="flex gap-4 text-sm font-bold text-gray-700">
                        <span>출근 <span className="text-green-500">{stats.workCount}</span>회</span>
                        {/* 정규직 */}
                        {type === 'regular' && (
                            <>
                                <span>지각 <span className="text-yellow-500">{stats.lateCount}</span>회</span>
                                <span>결근 <span className="text-red-500">{stats.absentCount}</span>회</span>
                                <span>휴가 <span className="text-purple-500">{stats.leaveCount}</span>회</span>
                            </>
                        )}
                    </div>
                )}
            </div>

            {/* 달력 */}
            <div className="flex-1 overflow-hidden">
                <Calendar 
                    key={selectedEmployee.employeeId}
                    ref={ref} 
                    onDatesSet={onDatesSet}
                    events={events} 
                    dateClick={handleDateClick}
                    eventContent={eventContent}
                />
            </div>
        </div>
    );
});

AdminCalendar.displayName = "AdminCalendar";
export default AdminCalendar;