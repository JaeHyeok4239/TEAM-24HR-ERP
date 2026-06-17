// 지도
'use client';
import { useEffect, useRef } from 'react';
import L from 'leaflet';
import 'leaflet/dist/leaflet.css';
import { HugeiconsIcon } from "@hugeicons/react"

export default function AttendanceMap() {
    const mapRef = useRef(null);

  useEffect(() => {
    // 지도 생성(서울 좌표)
    const map = L.map(mapRef.current).setView([37.5665, 126.9780], 18);

    // 오픈스트리트맵 타일 추가
    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
      attribution: '© OpenStreetMap contributors'
    }).addTo(map);

    // 아이콘 설정(SVG 문자열 직접 사용)
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
    L.marker([37.5665, 126.9780], { icon: buildingIcon })
      .addTo(map)
      .bindTooltip("회사", { 
        permanent: true,   // true - 항상 / false - 마우스 올릴 때만
        direction: 'bottom', // 핀 아래쪽에 표시
        className: 'text-label', // CSS 클래스로 글자 디자인 변경 가능
        opacity: 100
    });

    // 컴포넌트가 사라질 때 지도 메모리 정리
    return () => map.remove();
  }, []);

  return (
    <div className="w-full h-full p-3 bg-white border border-slate-200 rounded-xl">
      <div ref={mapRef} className="w-full h-64 rounded-xl" />
    </div>
  );
}