// 출퇴근 버튼
'use client';

import { useState } from 'react';

export default function AttendanceAction() {
    const [attendanceTime, setAttendanceTime] = useState("");

    // (출근/퇴근) 완료: 0시 0분
    const handleAttendance = (type) => {
        const now = new Date();
        const timeString = `${now.getHours()}시 ${now.getMinutes()}분`;
        setAttendanceTime(`${type} 완료: ${timeString}`);
    }
  
  return (
    <div className="p-4 bg-white border border-slate-200 rounded-xl">
      <p className="mb-2">{attendanceTime}</p>
      <button 
        onClick={handleAttendance}
        className="px-10 py-2 bg-blue-500 text-white rounded-xl"
      >
        출근
      </button>
      <button 
        onClick={handleAttendance}
        className="px-10 py-2 bg-blue-500 text-white rounded-xl ml-2"
      >
        퇴근
      </button>
    </div>
  );
}