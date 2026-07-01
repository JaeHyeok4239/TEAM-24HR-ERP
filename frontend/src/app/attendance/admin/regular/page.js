"use client";

import { useRef } from "react";
import { useDateNavigation } from "@/hooks/useDateNavigation";
import PageHeader from '@/components/attendance/PageHeader';

export default function AttendanceRegularPage() {
    const calendarRef = useRef(null);
    const { currentDate } = useDateNavigation();

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
            
            {/* body */}
            <div className="mt-4">
                <p>정규직 근태 관리</p>
            </div>
        </main>
    );
}