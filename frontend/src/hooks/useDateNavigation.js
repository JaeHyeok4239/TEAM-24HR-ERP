// src/hooks/useDateNavigation.js
import { useState } from 'react';
import dayjs from 'dayjs';

export const useDateNavigation = (calendarRef) => {
    const [currentDate, setCurrentDate] = useState(dayjs().format("YYYY.MM.DD"));

    // 다음 달 이동 가능 여부 확인
    const isNextDisabled = dayjs(currentDate.replaceAll('.', '-')).add(1, 'month').isAfter(dayjs(), 'month');

    // 달력 이동 시 날짜(DD)를 유지하며 월만 변경
    const handlePrev = () => {
        const newDate = dayjs(currentDate.replaceAll('.', '-')).subtract(1, 'month').format("YYYY.MM.DD");
        setCurrentDate(newDate);
        calendarRef.current?.getApi().gotoDate(newDate.replaceAll('.', '-'));
    };

    const handleNext = () => {
        if (isNextDisabled) return;

        let nextDate = dayjs(currentDate.replaceAll('.', '-')).add(1, 'month');
        const today = dayjs();

        if (nextDate.isAfter(today, 'day')) {
            nextDate = today;
        }

        const formatted = nextDate.format("YYYY.MM.DD");
        setCurrentDate(formatted);
        calendarRef.current?.getApi().gotoDate(formatted.replaceAll('.', '-'));
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
        if (dayjs(newDateStr).isAfter(dayjs(), 'day')) {
            console.log("오늘 이후 날짜는 선택할 수 없습니다.");
            return;
        }

        const formatted = dayjs(newDateStr).format("YYYY.MM.DD");
        setCurrentDate(formatted);

        // newDateStr이 유효한 문자열인지 확인
        if (!newDateStr || typeof newDateStr !== 'string') {
            console.warn("updateDate 함수에 유효하지 않은 날짜 문자열이 전달되었습니다:", newDateStr);
            return;
        }
        // calendarRef와 getApi()가 유효한지 확인 후 gotoDate 호출
        if (calendarRef.current && calendarRef.current.getApi()) {
            calendarRef.current.getApi().gotoDate(newDateStr);
        }
    };

    return { currentDate, handlePrev, handleNext, handleToday, handleDatesSet: () => {}, updateDate, isNextDisabled };
};