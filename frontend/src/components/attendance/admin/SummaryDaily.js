import React, { useState, useEffect } from 'react';
import { apiRequest } from '@/lib/api'; 

const SummaryDaily = ({ date, type = 'regular' }) => {
    const [summaryData, setSummaryData] = useState(null);
    const [error, setError] = useState(null);

    // api 호출
    useEffect(() => {
        const fetchSummary = async (targetDate) => {
            try {
                const response = await apiRequest(`/api/attendance/summary-daily-all?date=${targetDate}`, {
                    method: "GET",
                });
                const data = await response.json();
                
                setSummaryData(data);
                setError(null);
            } catch (err) {
                console.error("에러 발생:", err);
                setError(err.message || "데이터를 불러오는 중 오류가 발생했습니다.");
            }
        };

        if (date) {
            fetchSummary(date);
        }
    }, [date]); // date가 변경될 때마다 실행

    if (error) return <div>에러 발생: {error}</div>;
    if (!summaryData) return <div>로딩 중</div>;

    // 직원 타입에 따라 분기 처리
    const summary = summaryData[type] || summaryData.regular;

    const totalCount = summary.work + summary.late + summary.absent + summary.leave + summary.ready;

    return (
        <div className="summary-container" style={{ display: 'flex', gap: '20px', marginBottom: '30px' }}>
            <SummaryCard title="전체 직원" count={totalCount} textColor="text-blue-500" />
            <SummaryCard title="출근" count={summary.work} textColor="text-green-500" />
            {/* 정규직일 때만 나오는 필드들 */}
            {type === 'regular' && (
                <>
                    <SummaryCard title="지각" count={summary.late ?? 0} textColor="text-yellow-500" />
                    <SummaryCard title="결근" count={summary.absent ?? 0} textColor="text-red-500" />
                    <SummaryCard title="휴가" count={summary.leave ?? 0} textColor="text-purple-500" />
                </>
            )}
            <SummaryCard title="미출근" count={summary.ready} textColor="text-gray-500" />
        </div>
    );
};

// 카드 배경
const SummaryCard = ({ title, count, textColor }) => (
    <div className="bg-white border border-gray-100 rounded-lg shadow-sm p-5 flex-1 text-center">
        <h3 className="text-sm text-gray-500 font-bold">{title}</h3>
        <p className={`text-2xl font-bold mt-2 ${textColor}`}>
            {/* count가 없거나 NaN이면 0 표시 */}
            {count ?? 0}
        </p>
    </div>
);

export default SummaryDaily;