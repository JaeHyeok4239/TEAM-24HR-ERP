"use client";

import { useRef, useState, useEffect } from "react";
import { useDateNavigation } from "@/hooks/useDateNavigation";
import { getHrEmployeesRequest } from "@/services/hrEmployeeService";
import PageHeader from '@/components/attendance/PageHeader';
import SummaryDaily from "@/components/attendance/admin/SummaryDaily";
import AttendanceEmployeeList from "@/components/attendance/admin/AttendanceEmployeeList";

export default function AttendanceRegularPage() {
    const [employees, setEmployees] = useState([]);
    const [isLoading, setIsLoading] = useState(true);
    const calendarRef = useRef(null);
    const { currentDate } = useDateNavigation();

    // getHrEmployeesRequest API 호출
    useEffect(() => {
        const fetchEmployees = async () => {
            setIsLoading(true);
            try {
                // 정규직 직원만 조회하도록 지정
                const data = await getHrEmployeesRequest({ employmentType: 'REGULAR' });
                setEmployees(data);
            } catch (error) {
                console.error("직원 목록을 불러오는데 실패했습니다:", error);
            } finally {
                setIsLoading(false);
            }
        };

        fetchEmployees();
    }, []); // 빈 배열을 넣어 컴포넌트 마운트 시 1회 실행

    return (
        <main className="h-screen p-4">
            {/* 헤더 */}
            <PageHeader 
                title="정규직 근태 관리" 
                currentDate={currentDate}
                onPrev={() => calendarRef.current?.getApi().prev()}
                onNext={() => calendarRef.current?.getApi().next()}
                onToday={() => calendarRef.current?.getApi().today()}
            />
            
            {/* 일별 근태 조회 */}
            <div className="mt-4">
                <SummaryDaily date={currentDate} type="regular" />
            </div>

            {/* 필터링 버튼 */}

            {/* 직원 목록/상세 달력 레이아웃 */}
            <div className="grid grid-cols-[550px_1fr] gap-4 mt-2 h-[calc(100vh-250px)]">
                <AttendanceEmployeeList 
                    employees={employees}
                    isLoading={isLoading}
                    onSelect={(emp) => setSelectedEmployee(emp)}
                    type="regular"
                />
                {/* <AttendanceDetailPanel 
                    employee={selectedEmployee} 
                /> */}
            </div>
        </main>
    );
}