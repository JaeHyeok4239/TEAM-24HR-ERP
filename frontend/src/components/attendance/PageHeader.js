// 날짜 변경 및 오늘 날짜로 이동하는 헤더
export default function AttendancePageHeader({ title, calendarRef, currentDate }) {
    // 헤더 내부에서 달력 제어 함수 정의
    const handlePrev = () => calendarRef.current?.getApi().prev();
    const handleNext = () => calendarRef.current?.getApi().next();
    const handleToday = () => calendarRef.current?.getApi().today();

    return (
        <div className="flex justify-between items-center mb-4 bg-blue-100/30 -mx-4 -my-4 p-4">
            {/* 헤더 */}
            <div className="font-semibold text-lg ml-4">
                {title}
            </div>
            {/* 날짜 이동 및 오늘 날짜 */}
            <div className="flex gap-2 mr-6">
                <button onClick={handlePrev} className="bg-white hover:bg-[#E8E8E8] text-black px-3 py-1 rounded-full font-bold">&lt;</button>
                <span onClick={handleToday} className="bg-[#0C6EFD] hover:bg-[#0a58ca] text-white px-4 py-1 rounded font-bold cursor-pointer">
                    {currentDate}
                </span>
                <button onClick={handleNext} className="bg-white hover:bg-[#E8E8E8] text-black px-3 py-1 rounded-full font-bold">&gt;</button>
            </div>
        </div>
    );
}