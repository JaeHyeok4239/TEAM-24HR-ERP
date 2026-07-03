import React from 'react';

const DetailPanel = ({ isOpen, onClose, date, userType, data }) => {
    if (!isOpen) return null;

    const correctionHistory = [
        { date: '2026-07-02', before: '09:35', after: '09:00', processor: '인사팀 팀장' }
    ];

    return (
        <>
            <div className="fixed top-0 right-0 h-full w-[500px] bg-white z-[9999] shadow-2xl flex flex-col border-l border-gray-200 animate-in slide-in-from-right duration-300">
                {/* 헤더 */}
                <div className="flex justify-between items-center px-6 py-4 border-b border-gray-100 bg-white">
                    <h2 className="text-xl font-bold text-gray-800">{date} 상세 정보</h2>
                    <h5 className="font-bold text-green-500">출근</h5>
                    <button onClick={onClose} className="text-gray-400 hover:text-gray-600 text-3xl font-light">&times;</button>
                </div>

                {/* 본문 */}
                <div className="flex-1 overflow-y-auto p-6 space-y-8">
                    
                    <div className="grid grid-cols-2 gap-4">
                        {[
                            { label: '출근 시간', value: '09:38' },
                            { label: '퇴근 시간', value: '18:05' },
                            { label: '총 출근 시간', value: '9시간' },
                            { label: '기본 근무 시간', value: '8시간' },
                            { label: '초과 근무 시간', value: '1시간', span: 'col-span-2' }
                        ].map((item, idx) => (
                            <div key={idx} className={`${item.span || ''} bg-gray-50 p-4 rounded-lg border border-gray-100`}>
                                <p className="text-xs text-gray-500 font-bold uppercase mb-1">{item.label}</p>
                                <p className="text-lg font-bold text-gray-800">{item.value || '-'}</p>
                            </div>
                        ))}
                    </div>

                    {/* 정정 이력 */}
                    <div>
                        <h3 className="text-sm font-bold text-gray-700 mb-3 uppercase tracking-wider">정정 이력</h3>
                        <div className="border border-gray-200 rounded-lg overflow-hidden">
                            <table className="w-full text-sm text-left">
                                <thead className="bg-gray-50 text-gray-500 border-b">
                                    <tr className="text-center">
                                        <th className="px-4 py-3 font-semibold">일시</th>
                                        <th className="px-4 py-3 font-semibold">수정 전</th>
                                        <th className="px-4 py-3 font-semibold">수정 후</th>
                                        <th className="px-4 py-3 font-semibold">담당자</th>
                                    </tr>
                                </thead>
                                <tbody className="divide-y divide-gray-100">
                                    {correctionHistory.map((h, i) => (
                                        <tr key={i} className="hover:bg-gray-50 text-center">
                                            <td className="px-4 py-3 text-gray-600">{h.date}</td>
                                            <td className="px-4 py-3 text-gray-800 font-medium">{h.before}</td>
                                            <td className="px-4 py-3 text-gray-600">{h.after}</td>
                                            <td className="px-4 py-3 text-gray-600">{h.processor}</td>
                                        </tr>
                                    ))}
                                </tbody>
                            </table>
                        </div>
                    </div>

                    {/* 비고 */}
                    {(userType === 'regular' || userType === 'daily') && (
                        <div>
                            <label className="block text-sm font-bold text-gray-700 mb-2 uppercase">비고</label>
                            <textarea 
                                className="w-full h-24 p-3 border border-gray-200 rounded-lg bg-gray-50 text-sm resize-none outline-none focus:ring-1 focus:ring-blue-400"
                                defaultValue={data?.note || ""}
                            />
                        </div>
                    )}
                </div>
            </div>
        </>
    );
};

export default DetailPanel;