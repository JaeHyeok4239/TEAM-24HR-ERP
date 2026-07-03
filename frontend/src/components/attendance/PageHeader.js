import { useRef } from 'react';

// 날짜 변경 및 오늘 날짜로 이동하는 헤더
export default function AttendancePageHeader({ title, currentDate, onPrev, onNext, onToday, onDateChange }) {
    const inputRef = useRef(null);
    const inputDate = currentDate.replaceAll('.', '-');

    const handleContainerClick = () => {
        inputRef.current?.showPicker();
    };
    
    return (
        <div className="flex justify-between items-center mb-4 bg-blue-100/30 -mx-4 -my-4 p-4">
            {/* 헤더 */}
            <div className="font-semibold text-lg ml-4">
                {title}
            </div>

            {/* 날짜 이동 */}
            <div className="flex gap-2 mr-6">
                <button onClick={onPrev} className="bg-white hover:bg-[#E8E8E8] text-black px-3 py-1 rounded-full font-bold cursor-pointer">&lt;</button>
                
                {/* 날짜 클릭 시 달력 열기 */}
                <div onClick={handleContainerClick} className="cursor-pointer relative">
                    <input 
                        ref={inputRef}
                        type="date" 
                        value={inputDate}
                        className="absolute opacity-0 -z-10"
                        onChange={(e) => onDateChange?.(e.target.value)}
                    />
                    <span className="bg-[#0C6EFD] hover:bg-[#0a58ca] text-white px-4 py-1 rounded font-bold block whitespace-nowrap transition-colors">
                        {currentDate}
                    </span>
                </div>

                <button onClick={onNext} className="bg-white hover:bg-[#E8E8E8] text-black px-3 py-1 rounded-full font-bold cursor-pointer">&gt;</button>
                <button onClick={onToday} className="bg-gray-200 hover:bg-gray-300 text-black px-3 py-1 rounded font-bold cursor-pointer ml-2">
                    오늘
                </button>
            </div>
        </div>
    );
};