'use client';

import { useEffect, useRef, useState } from 'react';
import { apiRequest } from '@/lib/api';
import L from 'leaflet';
import 'leaflet/dist/leaflet.css';

// 지도
export default function AttendanceMap({ userLocation }) {
  const mapRef = useRef(null);
  const [hqLocation, setHqLocation] = useState(null);

  // 근무지 목록 가져오기
  useEffect(() => {
    apiRequest('/api/attendance/workplaces')
        .then(res => res.json())
        .then(data => {
            const hq = data.find(w => w.name === 'HQ');
            if (hq) setHqLocation(hq);
        })
        .catch(err => {
            console.error("근무지 정보를 불러오는데 실패했습니다:", err);
        });
  }, []);

  // 지도 그리기
  useEffect(() => {
    if (!hqLocation || !mapRef.current) return;
    const map = L.map(mapRef.current).setView([hqLocation.latitude, hqLocation.longitude], 18);

    // 오픈스트리트맵 타일 추가
    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
      attribution: '© OpenStreetMap contributors'
    }).addTo(map);

    // HQ 아이콘 설정(SVG 문자열 직접 사용)
    const buildingIcon = L.divIcon({
      html: `
        <div style="background-color: #3182ce; border-radius: 50%; padding: 4px; display: flex; align-items: center; justify-content: center; width: 32px; height: 32px;">
          <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="white" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <path d="M3 21h18M5 21V7l7-4 7 4v14M9 21v-6a2 2 0 0 1 2-2h2a2 2 0 0 1 2 2v6" />
          </svg>
        </div>
      `,
      className: 'custom-icon',
      iconSize: [32, 32],
      iconAnchor: [16, 32],
    });

    // 마커 생성
    L.marker([hqLocation.latitude, hqLocation.longitude], { icon: buildingIcon })
      .addTo(map)
      .bindTooltip("본사", { 
        permanent: true,   // true - 항상 / false - 마우스 올릴 때만
        direction: 'bottom', // 핀 아래쪽에 표시
        className: 'text-label', // CSS 클래스로 글자 디자인 변경 가능
        opacity: 100
    });

    // 내 위치 마커
    if (userLocation) {
      console.log("마커 작성:", userLocation);
      const userIcon = L.divIcon({
        html: `<div style="background-color: #25d16d; border-radius: 50%; width: 16px; height: 16px; border: 2px solid white; "></div>`,
        className: 'user-icon',
        iconSize: [24, 24],
      });
      
      L.marker([userLocation.latitude, userLocation.longitude], { icon: userIcon })
        .addTo(map)
        .bindTooltip("내 위치", { 
          permanent: true, 
          direction: 'bottom', 
          className: 'text-label' 
        });
    }

    // 컴포넌트가 사라질 때 지도 메모리 정리
    return () => map.remove();
  }, [hqLocation, userLocation]);

  return (
    <div className="w-full h-full p-3 bg-white border border-slate-200 rounded-xl">
      <div ref={mapRef} className="w-full h-64 rounded-xl" />
    </div>
  );
}