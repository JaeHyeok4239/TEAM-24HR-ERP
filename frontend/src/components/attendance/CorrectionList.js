import React from 'react';
import dayjs from 'dayjs';

const CorrectionList = ({ corrections }) => {

    const formatTime = (dateTime) => {
        if (!dateTime) return '-';
        return dayjs(dateTime).format('HH:mm');
    };

    return (
        <div>
            <h3 className="text-sm font-bold text-gray-700 mb-3 uppercase tracking-wider">정정 이력</h3>
            
            {(!corrections || corrections.length === 0) ? (
                // 정정 내역이 없을 때 출력
                <div className="border border-dashed border-gray-300 rounded-lg py-6 text-center text-sm text-gray-400">
                    정정 내역이 없습니다.
                </div>
            ) : (
            // 정정 내역이 있을 때 출력
            <div className="border border-gray-200 rounded-lg overflow-hidden">
                <table className="w-full text-sm text-left">
                    <thead className="bg-gray-50 text-gray-500 border-b">
                        <tr className="text-center">
                            <th className="px-4 py-3 font-semibold">일시</th>
                            <th className="px-4 py-3 font-semibold">수정 전</th>
                            <th className="px-4 py-3 font-semibold">수정 후</th>
                            <th className="px-4 py-3 font-semibold">담당자</th>
                            <th className="px-4 py-3 font-semibold">사유</th>
                        </tr>
                    </thead>
                    <tbody className="divide-y divide-gray-100">
                        {corrections.map((h, i) => (
                            <tr key={i} className="hover:bg-gray-50 text-center">
                                <td className="px-4 py-3 text-gray-600">{dayjs(h.createdAt).format('YYYY-MM-DD')}</td>
                                <td className="px-4 py-3 text-gray-800 font-medium">{formatTime(h.beforeTime)}</td>
                                <td className="px-4 py-3 text-gray-600">{formatTime(h.afterTime)}</td>
                                <td className="px-4 py-3 text-gray-600">{`${h.managerTeam} ${h.managerPosition}`}</td>
                                <td className="px-4 py-3 text-gray-600 text-left min-w-[120px]">{h.correctionReason || '-'}</td>
                            </tr>
                        ))}
                        </tbody>
                    </table>
                </div>
            )}
        </div>
    );
};

export default CorrectionList;