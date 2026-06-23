export default function ApprovalStepper({ approvalHistories }) {
  if (!approvalHistories || approvalHistories.length === 0) {
    return <p className="text-sm text-gray-400">결재 정보 없음</p>;
  }

  const statusConfig = {
    APR: { label: "승인", bg: "bg-green-500 border-green-500 text-white" },
    REJ: { label: "반려", bg: "bg-red-500 border-red-500 text-white" },
    PND: { label: "대기", bg: "border-gray-300 text-gray-400" },
  };

  return (
    <div className="flex items-start py-4">
      {approvalHistories.map((line, i) => {
        const config = statusConfig[line.approvalStatus] ?? statusConfig.PND;

        return (
          <div key={line.historyId} className="flex-1 flex flex-col items-center relative">
            {/* 연결선 */}
            {i < approvalHistories.length - 1 && (
              <div className={`absolute top-[18px] left-1/2 w-full h-0.5 z-0
                ${line.approvalStatus === 'APR' ? 'bg-green-500' : 'bg-gray-200'}`} />
            )}
            {/* 원 */}
            <div className={`w-9 h-9 rounded-full border-2 flex items-center justify-center z-10 bg-white
              ${config.bg}`}>
              {line.approvalStatus === 'APR' ? '✓' :
               line.approvalStatus === 'REJ' ? '✕' : line.stepOrder}
            </div>
            {/* 상태 라벨 */}
            <span className={`text-xs mt-2 font-medium
              ${line.approvalStatus === 'APR' ? 'text-green-600' :
                line.approvalStatus === 'REJ' ? 'text-red-500' : 'text-gray-400'}`}>
              {config.label}
            </span>
            {/* 결재자 */}
            <span className="text-xs text-gray-600 mt-0.5">{line.approver}</span>
          </div>
        );
      })}
    </div>
  );
}