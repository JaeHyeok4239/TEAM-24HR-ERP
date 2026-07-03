"use client";

import { useState } from 'react';

export default function AttendanceDailyInputPanel({ isOpen, onClose }) {
    // 1. useState는 무조건 컴포넌트 최상단에서 호출해야 합니다. (에러 방지)
    const [rows, setRows] = useState([
        { id: 1, name: '김철수', isPresent: false, location: '근무지1', startTime: '', endTime: '' },
        { id: 2, name: '이영희', isPresent: false, location: '근무지1', startTime: '', endTime: '' },
        { id: 3, name: '박지성', isPresent: false, location: '근무지1', startTime: '', endTime: '' },
    ]);

    // 2. 훅 실행 후에 조건부 렌더링을 처리합니다.
    if (!isOpen) return null;

    const handleUpdate = (id, field, value) => {
        setRows(prev => prev.map(row => 
            row.id === id ? { ...row, [field]: value } : row
        ));
    };

    return (
        <div className="fixed inset-0 z-[9999] flex justify-end">

            {/* 패널 본체 */}
            <div className="relative w-[600px] bg-white h-full shadow-2xl flex flex-col p-6 overflow-y-auto">
                {/* 헤더 영역 (닫기 버튼 포함) */}
                <div className="flex justify-between items-center mb-6">
                    <h2 className="text-xl font-bold">근태 기록 관리</h2>
                    <button 
                        onClick={onClose}
                        className="px-4 py-2 bg-gray-100 hover:bg-gray-200 rounded-lg text-sm font-bold text-gray-700 transition-colors"
                    >
                        닫기
                    </button>
                </div>
                
                {/* 입력 테이블 */}
                <div className="border rounded-lg overflow-hidden mb-6">
                    <table className="w-full text-sm">
                        <thead className="bg-gray-100">
                            <tr>
                                <th className="p-3 text-center">출근</th>
                                <th className="p-3 text-left">이름</th>
                                <th className="p-3 text-left">근무지</th>
                                <th className="p-3 text-left">출근시간</th>
                                <th className="p-3 text-left">퇴근시간</th>
                            </tr>
                        </thead>
                        <tbody>
                            {rows.map(row => (
                                <tr key={row.id} className="border-b">
                                    <td className="p-3 text-center">
                                        <input 
                                            type="checkbox" 
                                            checked={row.isPresent} 
                                            onChange={(e) => handleUpdate(row.id, 'isPresent', e.target.checked)} 
                                        />
                                    </td>
                                    <td className="p-3 font-bold">{row.name}</td>
                                    <td className="p-3">
                                        <select 
                                            value={row.location} 
                                            onChange={(e) => handleUpdate(row.id, 'location', e.target.value)} 
                                            className="border p-1 rounded"
                                        >
                                            <option value="-">-</option>
                                            <option value="근무지1">근무지1</option>
                                            <option value="근무지2">근무지2</option>
                                        </select>
                                    </td>
                                    <td className="p-3">
                                        <input 
                                            type="time" 
                                            value={row.startTime} 
                                            onChange={(e) => handleUpdate(row.id, 'startTime', e.target.value)} 
                                            className="border p-1 rounded" 
                                        />
                                    </td>
                                    <td className="p-3">
                                        <input 
                                            type="time" 
                                            value={row.endTime} 
                                            onChange={(e) => handleUpdate(row.id, 'endTime', e.target.value)} 
                                            className="border p-1 rounded" 
                                        />
                                    </td>
                                </tr>
                            ))}
                        </tbody>
                    </table>
                </div>

                {/* 하단 저장 버튼 */}
                <button 
                    onClick={onClose} 
                    className="w-full bg-blue-600 text-white py-3 rounded-lg font-bold hover:bg-blue-700 transition-colors"
                >
                    저장하기
                </button>
            </div>
        </div>
    );
}