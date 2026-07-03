'use client';

import { useState, useEffect } from 'react';
import AttendanceMap from './AttendanceMap';
import AttendanceAction from './AttendanceAction';

// Action, Map 부모
export default function AttendancePage() {
  const [userLocation, setUserLocation] = useState(null);
  const [result, setResult] = useState('N');

  useEffect(() => {
    if (navigator.geolocation) {
      navigator.geolocation.getCurrentPosition(
        (position) => {        
          setUserLocation({
            latitude: position.coords.latitude,
            longitude: position.coords.longitude
          });

          // result 'Y'로 변경
          setResult('Y'); 
        },
        (error) => {
          // 팝업에서 차단을 눌렀거나 에러 발생 시
          console.error("위치 획득 실패:", error);
          setResult('N');
          alert("위치 권한이 거부되었습니다. 지도를 보려면 브라우저 설정에서 권한을 허용해주세요.");
        }
      );
    }
  }, []);

  return (
    <div className="flex flex-col gap-4">
      
      <AttendanceMap userLocation={userLocation} />
      <AttendanceAction userLocation={userLocation} />
    </div>
  );
}