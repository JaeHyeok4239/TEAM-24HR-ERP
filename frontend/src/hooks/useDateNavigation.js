// src/hooks/useDateNavigation.js
import { useState } from 'react';
import dayjs from 'dayjs';

export const useDateNavigation = (calendarRef) => {
    const [currentDate, setCurrentDate] = useState(dayjs().format("YYYY.MM.DD"));

    // 달력 이동 시 날짜(DD)를 유지하며 월만 변경
    const handlePrev = () => {
        const newDate = dayjs(currentDate.replaceAll('.', '-')).subtract(1, 'month').format("YYYY.MM.DD");
        setCurrentDate(newDate);
        calendarRef.current?.getApi().gotoDate(newDate.replaceAll('.', '-'));
    };

    const handleNext = () => {
        const newDate = dayjs(currentDate.replaceAll('.', '-')).add(1, 'month').format("YYYY.MM.DD");
        setCurrentDate(newDate);
        calendarRef.current?.getApi().gotoDate(newDate.replaceAll('.', '-'));
    };

    const handleToday = () => {
        const today = dayjs().format("YYYY.MM.DD");
        setCurrentDate(today);
        calendarRef.current?.getApi().today();
    };

    // 캘린더 내부 이벤트(드래그 등)로 인한 뷰 변경 시 동기화
    const handleDatesSet = (dateInfo) => {
        // 이미 버튼/클릭으로 상태가 변경되었다면 datesSet에서 또 상태를 바꾸지 않음
    };

    // 직접 날짜 업데이트(날짜 클릭 시 호출)
    const updateDate = (newDateStr) => {
        const formatted = dayjs(newDateStr).format("YYYY.MM.DD");
        setCurrentDate(formatted);
        calendarRef.current?.getApi().gotoDate(newDateStr);
    };

    return { currentDate, handlePrev, handleNext, handleToday, handleDatesSet, updateDate };
};