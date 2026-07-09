import React from 'react';
import { getStatusLabel, getStatusColor } from '@/services/attendanceService';
import CorrectionList from './CorrectionList';
import dayjs from 'dayjs';

const DetailPanel = ({ isOpen, onClose, date, userType, data }) => {
    if (!isOpen) return null;

    // 데이터 로딩 처리
    if (!data || Object.keys(data).length === 0) {
        return (
            <div className="fixed top-0 right-0 h-full w-[500px] bg-white z-[9999] shadow-2xl flex items-center justify-center">
                <p className="text-gray-500">데이터를 불러오는 중입니다...</p>
            </div>
        );
    }

    // 시간 포맷팅(HH:mm)
    const formatTime = (dateTime) => {
        if (!dateTime) return '-';
        return dayjs(dateTime).format('HH:mm');
    };

    // 분을 n시간 형식으로 변환
    const formatMinutesToHours = (minutes) => {
        if (minutes === null || minutes === undefined || minutes < 0) return '-';
        if (minutes === 0) return '0분';

        const hours = Math.floor(minutes / 60);
        const remainingMinutes = minutes % 60;

        const parts = [];
        if (hours > 0) {
            parts.push(`${hours}시간`);
        }
        // 시간이 0이거나, 분이 있을 때 분을 표시
        if (hours > 0 || remainingMinutes > 0) {
            parts.push(`${String(remainingMinutes).padStart(2, '0')}분`);
        }
        return parts.join(' ');
    };

    const statusLabel = getStatusLabel(data?.status);
    const statusColor = getStatusColor(data?.status);
    const textColor = (data?.status === 'READY' || !data?.status) ? '#111827' : 'white'; // 미출근일 경우 검정색

    const allTimeDetails = [
        { label: '출근 시간', value: formatTime(data?.checkIn) },
        { label: '퇴근 시간', value: formatTime(data?.checkOut) },
        { label: '총 출근 시간', value: formatMinutesToHours(data?.totalWorkTime) },
        { label: '기본 근무 시간', value: formatMinutesToHours(data?.basicWorkTime) },
        { label: '초과 근무 시간', value: formatMinutesToHours(data?.overtime), span: 'col-span-2' }
    ];

    // 일용직이면 앞에 3개만, 아니면(정규직) 전체 필드 사용
    const timeDetails = userType === 'daily' 
        ? allTimeDetails.slice(0, 3) 
        : allTimeDetails;

    return (
        <>
            <div className="fixed top-0 right-0 h-full w-[700px] bg-white z-[9999] shadow-2xl flex flex-col border-l border-gray-200 animate-in slide-in-from-right duration-300">
                {/* 헤더 */}
                <div className="flex justify-between items-center px-6 py-4 border-b">
                    <div className="flex items-center gap-3">
                        <h2 className="text-xl font-bold text-gray-800">{date} 상세 정보</h2>
                        {statusLabel && (
                            <span style={{ backgroundColor: statusColor, color: textColor }} className="px-2 py-1 text-xs font-bold rounded">
                                {statusLabel}
                            </span>
                        )}
                        {data?.workplaceName && (
                            <span className="bg-gray-200 text-gray-700 px-2 py-1 text-xs font-bold rounded">
                                {data.workplaceName}
                            </span>
                        )}
                    </div>
                    <button onClick={onClose} className="text-gray-400 hover:text-gray-600 text-3xl font-light">
                        &times;
                    </button>
                </div>

                {/* 본문 */}
                <div className="flex-1 overflow-y-auto p-6 space-y-8">
                    <div className="grid grid-cols-2 gap-4">
                        {timeDetails.map((item, idx) => (
                            <div key={idx} className={`${item.span || ''} bg-gray-50 p-4 rounded-lg border border-gray-100`}>
                                <p className="text-xs text-gray-500 font-bold uppercase mb-1">{item.label}</p>
                                <p className="text-lg font-bold text-gray-800">{item.value || '-'}</p>
                            </div>
                        ))}
                    </div>
                    
                    {/* 정정 이력 */}
                    <CorrectionList corrections={data?.corrections} />
                </div>
            </div>
        </>
    );
};

export default DetailPanel;