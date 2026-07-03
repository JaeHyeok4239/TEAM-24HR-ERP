'use client';

import { useState } from 'react';
import { apiRequest } from "@/lib/api";

export default function AttendanceAction({ userLocation }) {
  const [attendanceTime, setAttendanceTime] = useState("");
  const [loading, setLoading] = useState(false);

  const handleAttendance = async (type) => {
    if (!userLocation) {
      alert("위치 정보를 가져오는 중입니다. 잠시만 기다려주세요.");
      return;
    }

    setLoading(true);
    try {
      const endpoint = type === "출근" ? "/api/attendance/check-in" : "/api/attendance/check-out";

      const response = await apiRequest(endpoint, {
        method: "POST",
        body: JSON.stringify({
          latitude: userLocation.latitude,
          longitude: userLocation.longitude
        })
      });

      if (response.ok) {
        const message = await response.text(); 
        alert(message); 
        
        const now = new Date();
        const timeString = `${now.getHours()}시 ${now.getMinutes()}분`;
        setAttendanceTime(`${type} 완료: ${timeString}`);
      } else {
        // 400, 500 에러 등을 받아서 처리
        const errorText = await response.text(); 
        alert(`요청 실패: ${errorText || "서버 응답 오류"}`);
      }
    } catch (error) {
      console.error(error);
      alert("서버와 통신하는 중 문제가 발생했습니다.");
    } finally {
      setLoading(false);
    }
  }

  return (
    <div className="p-4 bg-white border border-slate-200 rounded-xl">
      <p className="mb-2">{attendanceTime ? attendanceTime : "아직 출근 전입니다."}</p>
      <button 
        onClick={() => handleAttendance("출근")}
        disabled={loading || !userLocation} // 위치 없으면 버튼 비활성화
        className={`px-10 py-2 text-white rounded-xl ${loading || !userLocation ? "bg-gray-400" : "bg-blue-500"}`}
      >
        {loading ? "처리중" : "출근"}
      </button>
      <button 
        onClick={() => handleAttendance("퇴근")}
        disabled={loading || !userLocation}
        className={`px-10 py-2 text-white rounded-xl ml-2 ${loading || !userLocation ? "bg-gray-400" : "bg-blue-500"}`}
      >
        {loading ? "처리중" : "퇴근"}
      </button>
    </div>
  );
}