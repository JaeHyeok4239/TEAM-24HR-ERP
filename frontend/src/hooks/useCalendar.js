import { useState } from "react";

export const useCalendar = () => {
  // 초기값으로 현재 연도와 월을 "YYYY.MM" 형태로 설정
  const [currentDate, setCurrentDate] = useState(() => {
    const now = new Date();
    const year = now.getFullYear();
    const month = String(now.getMonth() + 1).padStart(2, '0');
    return `${year}.${month}`;
  });

  // Calendar 컴포넌트의 onDatesSet 이벤트에서 호출될 핸들러
  // FullCalendar 라이브러리 등을 사용할 때 전달되는 dateInfo 객체를 처리
  const handleDatesSet = (dateInfo) => {
    if (dateInfo && dateInfo.view) {
      const date = dateInfo.view.currentStart;
      const year = date.getFullYear();
      const month = String(date.getMonth() + 1).padStart(2, '0');
      setCurrentDate(`${year}.${month}`);
    }
  };

  return { currentDate, handleDatesSet };
};