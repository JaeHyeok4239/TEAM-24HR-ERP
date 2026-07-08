"use client";

import { useState, useEffect } from 'react';
import { getDailyWorkersRequest, saveDailyAttendanceBatchRequest, applyDailyCorrectionRequest } from '@/services/attendanceService';

// 1분 단위 선택이 가능한 커스텀 시간 선택 컴포넌트
const TimeSelector = ({ value, onChange, disabled }) => {
    const [period, setPeriod] = useState(value?.split(' ')[0] || '-');
    const [hour, setHour] = useState(value?.split(' ')[1]?.split(':')[0] || '--');
    const [minute, setMinute] = useState(value?.split(' ')[1]?.split(':')[1] || '--');

    // 버튼 수정, 추가 모드
    const [isEditing, setIsEditing] = useState(false);

    const updateTime = (p, h, m) => {
        if (p === '-' || h === '--' || m === '--') {
            onChange('');
        } else {
            onChange(`${p} ${h}:${m}`);
        }
    };

    return (
        <div className="flex gap-1 items-center text-xs">
            <select 
                disabled={disabled}
                value={period} 
                onChange={(e) => { setPeriod(e.target.value); updateTime(e.target.value, hour, minute); 
                }} className="border rounded p-1"
            >
                <option value="-">-</option>
                <option value="오전">오전</option>
                <option value="오후">오후</option>
            </select>

            <select 
                disabled={disabled}
                value={hour} 
                onChange={(e) => { setHour(e.target.value); updateTime(period, e.target.value, minute); }} className="border rounded p-1"
            >
                <option value="--">--</option>
                {Array.from({ length: 12 }, (_, i) => String(i + 1).padStart(2, '0')).map(h => (
                    <option key={h} value={h}>{h}</option>
                ))}
            </select>

            <span className="font-bold">:</span>

            <select 
                disabled={disabled}
                value={minute} 
                onChange={(e) => { setMinute(e.target.value); updateTime(period, hour, e.target.value); }} className="border rounded p-1"
            >
                <option value="--">--</option>
                {Array.from({ length: 60 }, (_, i) => String(i).padStart(2, '0')).map(m => (
                    <option key={m} value={m}>{m}</option>
                ))}
            </select>
        </div>
    );
};

export default function AttendanceDailyInputPanel({ isOpen, onClose }) {
    const [rows, setRows] = useState([]);
    const [isEditing, setIsEditing] = useState(false);

    // 데이터 존재 여부 판단
    const hasData = rows.some(row => row.startTime !== '' || row.endTime !== '' || row.location !== '');

    // 일용직 명단 불러오기
    useEffect(() => {
        if (isOpen) {
            getDailyWorkersRequest()
                .then(data => {
                    const mappedData = data.map(item => ({
                        id: item.employeeId, 
                        logId: item.logId || null,
                        name: item.name,
                        isPresent: false,
                        location: item.location || '',
                        startTime: item.startTime || '', 
                        endTime: item.endTime || ''
                    }));
                    setRows(mappedData);
                })
                .catch(err => console.error("일용직 명단 로드 실패:", err));
        }
    }, [isOpen]);

    // 저장 로직
    const handleSave = async () => {
        try {
            // 1. 신규 저장할 데이터 필터링 (logId가 없는 것)
            const toSave = rows
                .filter(row => !row.logId && (row.startTime || row.endTime || row.location))
                .map(row => ({
                    employeeId: row.id,
                    workplaceCode: row.location,
                    checkInDateTime: row.startTime ? `2026-06-03 ${row.startTime}:00` : null, // 예시 날짜
                    checkOutDateTime: row.endTime ? `2026-06-03 ${row.endTime}:00` : null
                }));

            // 2. 수정할 데이터 필터링 (logId가 있는 것)
            const toUpdate = rows
                .filter(row => row.logId);

            // 신규 저장 API 호출
            if (toSave.length > 0) {
                await saveDailyAttendanceBatchRequest(toSave);
            }

            // 수정(정정) API 호출
            for (const row of toUpdate) {
                // 백엔드 applyDailyCorrection에 맞게 DTO 구성
                const correctionData = [
                    { correctionType: 'IN', afterTime: `2026-06-03 ${row.startTime}:00`, correctionReason: '관리자 수정' },
                    { correctionType: 'OUT', afterTime: `2026-06-03 ${row.endTime}:00`, correctionReason: '관리자 수정' }
                ];
                await applyDailyCorrectionRequest(row.logId, correctionData);
            }

            alert("저장 및 수정이 완료되었습니다.");
            setIsEditing(false);
            // 필요시 데이터 새로고침
        } catch (error) {
            console.error("저장 실패:", error);
            alert("저장 중 오류가 발생했습니다.");
        }
    };

    const handleUpdate = (id, field, value) => {
        setRows(prev => prev.map(row => 
            row.id === id ? { ...row, [field]: value } : row
        ));
    };

    if (!isOpen) return null;

    return (
        <div className="fixed inset-0 z-[9999] flex justify-end bg-black/30">
            <div className="w-[850px] bg-white h-full shadow-2xl flex flex-col p-8 overflow-y-auto">
                <div className="flex justify-between items-center mb-8">
                    <h2 className="text-2xl font-bold">2026.06.03 (수) 근태 기록</h2>
                    <div className="flex gap-3">
                        <button 
                            onClick={() => isEditing ? handleSave() : setIsEditing(true)} 
                            className="px-8 py-2 bg-blue-600 text-white rounded-lg font-bold"
                        >
                            {isEditing ? '저장' : (hasData ? '수정' : '추가')}
                        </button>
                        <button onClick={onClose} className="text-3xl font-light hover:text-red-500 px-2">×</button>
                    </div>
                </div>
                
                <div className="border rounded-lg overflow-hidden mb-6 shadow-sm">
                    <table className="w-full text-sm">
                        <thead className="bg-slate-50 border-b">
                            <tr>
                                {/* 강제 줄바꿈 방지 */}
                                <th className="p-4 text-center text-gray-600 whitespace-nowrap">출근 여부</th>
                                <th className="p-4 text-left text-gray-600 whitespace-nowrap">이름</th>
                                <th className="p-4 text-left text-gray-600 whitespace-nowrap">근무지</th>
                                <th className="p-4 text-left text-gray-600 whitespace-nowrap">출근 시간</th>
                                <th className="p-4 text-left text-gray-600 whitespace-nowrap">퇴근 시간</th>
                            </tr>
                        </thead>
                        <tbody>
                            {rows.map(row => (
                                <tr key={row.id} className="border-b hover:bg-slate-50">
                                    <td className="p-4 text-center">
                                        <input type="checkbox" disabled checked={row.isPresent} className="w-5 h-5 cursor-not-allowed" />
                                    </td>
                                    <td className="p-4 font-bold whitespace-nowrap">{row.name}</td>
                                    <td className="p-4">
                                        <select 
                                            disabled={!isEditing}
                                            value={row.location} 
                                            onChange={(e) => handleUpdate(row.id, 'location', e.target.value)} 
                                            className="border p-2 rounded w-full bg-white min-w-[120px]"
                                        >
                                            <option value="">선택하지 않음</option>
                                            <option value="근무지1">근무지1</option>
                                            <option value="근무지2">근무지2</option>
                                        </select>
                                    </td>
                                    <td className="p-4 min-w-[180px]">
                                        <TimeSelector disabled={!isEditing} value={row.startTime} onChange={(val) => handleUpdate(row.id, 'startTime', val)} />
                                    </td>
                                    <td className="p-4 min-w-[180px]">
                                        <TimeSelector disabled={!isEditing} value={row.endTime} onChange={(val) => handleUpdate(row.id, 'endTime', val)} />
                                    </td>
                                </tr>
                            ))}
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    );
}