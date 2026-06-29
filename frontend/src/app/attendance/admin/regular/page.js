"use client";

import PageHeader from '@/components/attendance/PageHeader';

export default function AttendanceRegularPage() {
    return (
        <main className="h-screen p-4">
            <PageHeader title="정규직 근태 관리" />
            
            <div className="mt-4">
                <p>정규직 근태 관리</p>
            </div>
        </main>
    );
}