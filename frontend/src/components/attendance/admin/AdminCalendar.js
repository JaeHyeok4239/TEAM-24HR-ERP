import Calendar from '@/components/attendance/Calendar'; // 기존 Calendar 컴포넌트 재사용

export default function AttendanceCalendar({ selectedEmployee, calendarRef, onDatesSet }) {
    // 직원 선택 하지 않았을 때
    if (!selectedEmployee) {
        return (
            <div className="flex items-center justify-center h-full text-gray-500 rounded-xl border-slate-200 bg-white shadow-sm">
                <p>직원을 선택해주세요.</p>
                <p>직원을 선택하면 해당 직원의\n월별 근태 현황을 파악할 수 있습니다.</p>
            </div>
        );
    }
    // !py-0 h-full rounded-xl border-slate-200 bg-white shadow-sm

    // 직원 선택 시
    return (
        <Calendar 
            ref={calendarRef} 
            onDatesSet={onDatesSet} 
        />
    );
}