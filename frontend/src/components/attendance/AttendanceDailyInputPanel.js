"use client";

import { useState, useEffect, useCallback } from 'react';
import { getDailyManagementRequest,saveDailyAttendanceBatchRequest, 
    applyDailyCorrectionRequest } from '@/services/attendanceService';

// 출퇴근 시간 String을 24시간 형식으로 변환하는 함수
const convertTo24Hour = (timeStr) => {
    if (!timeStr || timeStr === '' || !timeStr.includes(' ')) return null;
    const [period, time] = timeStr.split(' ');
    
    if (!time) return null;

    let [hour, minute] = time.split(':');
    let h = parseInt(hour, 10);
    
    if (period === '오후' && h < 12) h += 12;
    if (period === '오전' && h === 12) h = 0;
    
    return `${String(h).padStart(2, '0')}:${minute}`;
};

// 1분 단위 선택이 가능한 커스텀 시간 선택 컴포넌트
const TimeSelector = ({ value, onChange, disabled }) => {
    const [period, setPeriod] = useState(value?.split(' ')[0] || '-');
    const [hour, setHour] = useState(value?.split(' ')[1]?.split(':')[0] || '--');
    const [minute, setMinute] = useState(value?.split(' ')[1]?.split(':')[1] || '--');

    const updateTime = (p, h, m) => {
        if (p === '-' || h === '--' || m === '--') {
            onChange('');
        } else {
            onChange(`${p} ${h}:${m}`);
        }
    };

    // 버튼 상태에 따라 버튼 색, 커서 스타일 변경
    const selectClasses = `border rounded p-1 transition-colors ${
        disabled ? 'bg-gray-100 text-gray-500 cursor-not-allowed' : 'bg-white'
    }`;

    return (
        <div className="flex gap-1 items-center text-xs">
            <select 
                disabled={disabled}
                value={period} 
                onChange={(e) => { setPeriod(e.target.value); updateTime(e.target.value, hour, minute); }} 
                className={selectClasses}
            >
                <option value="-">-</option>
                <option value="오전">오전</option>
                <option value="오후">오후</option>
            </select>

            <select 
                disabled={disabled}
                value={hour} 
                onChange={(e) => { setHour(e.target.value); updateTime(period, e.target.value, minute); }} 
                className={selectClasses}
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
                onChange={(e) => { setMinute(e.target.value); updateTime(period, hour, e.target.value); }} 
                className={selectClasses}
            >
                <option value="--">--</option>
                {Array.from({ length: 60 }, (_, i) => String(i).padStart(2, '0')).map(m => (
                    <option key={m} value={m}>{m}</option>
                ))}
            </select>
        </div>
    );
};

export default function AttendanceDailyInputPanel({ isOpen, onClose, selectedDate }) {
    const [rows, setRows] = useState([]);
    const [isEditing, setIsEditing] = useState(false);
    const [workplaces, setWorkplaces] = useState([]);

    // 버튼 색상
    const getButtonColor = () => {
        if (isEditing) return "bg-red-600 hover:bg-red-700"; // 저장
        if (hasData) return "bg-blue-600 hover:bg-blue-700"; // 수정
        return "bg-green-600 hover:bg-green-700";            // 추가
    };

    // 데이터 불러오는 로직 별도로 분리
    const fetchData = useCallback(async (date) => {
        try {
            const data = await getDailyManagementRequest(date);
            console.log("서버 데이터 확인:", data[0]);

            const convertToDisplay = (timeStr) => {
                if (!timeStr) return ''; 
                const [h, m] = timeStr.split(':'); 
                const hour = parseInt(h, 10);
                const period = hour >= 12 ? '오후' : '오전';
                const displayHour = hour > 12 ? hour - 12 : hour === 0 ? 12 : hour;
                return `${period} ${String(displayHour).padStart(2, '0')}:${m}`;
            };

            const processedRows = data.map(row => ({
                ...row,
                startTime: convertToDisplay(row.startTime),
                endTime: convertToDisplay(row.endTime),
                location: row.workplaceCode || "",
                isPresent: !!row.startTime
            })).sort((a, b) => a.name.localeCompare(b.name));

            setRows(processedRows);
        } catch (err) {
            console.error("데이터 로드 실패:", err);
        }
    }, []);

    // 창 열릴 때 데이터 로드
    useEffect(() => {
         // eslint-disable-next-line react-hooks/set-state-in-effect
        if (isOpen) fetchData(selectedDate);
    }, [isOpen, selectedDate, fetchData]);

    // 값 수정 시 상태 업데이트
    const handleUpdate = (employeeId, field, value) => {
        setRows(prev => prev.map(row => {
            if (row.employeeId !== employeeId) return row;

            const updatedRow = { ...row, [field]: value };

            // IN 값 입력 시 출근 여부 자동으로 체크로 변경
            if (field === 'startTime') {
                updatedRow.isPresent = value !== '';
            }

            return updatedRow;
        }));
    };

    // 저장 로직
    const handleSave = async () => {
        try {
            // 신규 저장 데이터
            const toSave = rows
                .filter(row => !row.logId && (row.startTime || row.endTime))
                .map(row => ({
                    employeeId: row.employeeId,
                    workplaceCode: row.location,
                    checkIn: convertTo24Hour(row.startTime),
                    checkOut: convertTo24Hour(row.endTime)
                }));

            console.log("서버로 보내는 최종 데이터:", { attendanceList: toSave });

            // 수정 데이터
            const toUpdate = rows.filter(row => row.logId);

            // 신규 저장
            if (toSave.length > 0) {
                await saveDailyAttendanceBatchRequest(toSave);
            }

            // 수정
            for (const row of toUpdate) {
                const correctionData = [];
                
                // 값이 있을 때만 정정 데이터에 추가(불필요한 수정 방지)
                if (row.startTime) {
                    correctionData.push({ 
                        correctionType: 'IN', 
                        afterTime: `${selectedDate} ${convertTo24Hour(row.startTime)}:00`, 
                        correctionReason: '관리자 수정' 
                    });
                }
                if (row.endTime) {
                    correctionData.push({ 
                        correctionType: 'OUT', 
                        afterTime: `${selectedDate} ${convertTo24Hour(row.endTime)}:00`, 
                        correctionReason: '관리자 수정' 
                    });
                }
                
                if (correctionData.length > 0) {
                    await applyDailyCorrectionRequest(row.logId, correctionData);
                }
            }

            alert("저장 및 수정이 완료되었습니다.");
            await fetchData(selectedDate);
            setIsEditing(false);
        } catch (error) {
            console.error("저장 실패:", error);
            alert("저장 중 오류가 발생했습니다.");
        }
    };

    const hasData = rows.some(row => row.logId || row.startTime || row.endTime || row.location);

    if (!isOpen) return null;

    return (
        <div className="fixed inset-0 z-[9999] flex justify-end bg-black/30">
            <div className="w-[850px] bg-white h-full shadow-2xl flex flex-col p-8 overflow-y-auto">
                <div className="flex justify-between items-center mb-8">
                    <h2 className="text-2xl font-bold">{selectedDate} 근태 기록</h2>
                    <div className="flex gap-3">
                        <button 
                            onClick={() => isEditing ? handleSave() : setIsEditing(true)} 
                            className={`px-8 py-2 text-white rounded-lg font-bold transition-colors ${getButtonColor()}`}
                        >
                            {isEditing ? '저장' : (hasData ? '수정' : '추가')}
                        </button>
                        <button onClick={onClose} className="text-3xl font-light hover:text-red-500 px-2">×</button>
                    </div>
                </div>
                
                <div className="rounded-lg overflow-y-auto mb-6 shadow-sm max-h-[800px]">
                    <table className="w-full text-sm relative">
                        <thead className="bg-slate-50 border-b top-0 z-10 sticky">
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
                            {rows.map((row, index) => (
                                <tr key={index} className="border-b hover:bg-slate-50">
                                    <td className="p-4 text-center">
                                        <input type="checkbox" disabled checked={!!row.isPresent} className="w-5 h-5 cursor-not-allowed" />
                                    </td>
                                    <td className="p-4 font-bold whitespace-nowrap">{row.name}</td>
                                    <td className="p-4">
                                        <select 
                                            disabled={!isEditing}
                                            value={row.location} 
                                            onChange={(e) => handleUpdate(row.employeeId, 'location', e.target.value)} 
                                            className={`border p-2 rounded w-full min-w-[120px] transition-colors ${isEditing ? 'bg-white' : 'bg-gray-100 text-gray-500 cursor-not-allowed'}`}
                                        >
                                            <option value="">선택하지 않음</option>
                                            <option value="TEMP01">근무지1</option>
                                            <option value="TEMP02">근무지2</option>
                                        </select>
                                    </td>
                                    <td className="p-4 min-w-[180px]">
                                        <TimeSelector disabled={!isEditing} value={row.startTime} onChange={(val) => handleUpdate(row.employeeId, 'startTime', val)} />
                                    </td>
                                    <td className="p-4 min-w-[180px]">
                                        <TimeSelector disabled={!isEditing} value={row.endTime} onChange={(val) => handleUpdate(row.employeeId, 'endTime', val)} />
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