import { useState } from 'react';
import dayjs from 'dayjs';

// 헤더 날짜 이동 버튼 클릭 시
export const useDateNavigation = (calendarRef) => {
    const [currentDate, setCurrentDate] = useState(dayjs().format("YYYY.MM"));

    // 버튼 핸들러
    const handlePrev = () => {
        calendarRef.current?.getApi().prev();
    };

    const handleNext = () => {
        calendarRef.current?.getApi().next();
    };

    const handleToday = () => {
        calendarRef.current?.getApi().today();
    };

    // 날짜 변경 시 상태 업데이트 함수
    const handleDatesSet = (dateInfo) => {
        const newDate = dayjs(dateInfo.view.currentStart).format("YYYY.MM");
        // 날짜가 다를 때만 업데이트해 무한 루프 방지
        if (newDate !== currentDate) {
            setCurrentDate(newDate);
        }
    };

    return { currentDate, handlePrev, handleNext, handleToday, handleDatesSet };
};