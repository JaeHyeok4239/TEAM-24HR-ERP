import { Check, X, Clock } from "lucide-react";

export default function ApprovalStepper({ approvalHistories }) {
  if (!approvalHistories || approvalHistories.length === 0) {
    return <p className="text-sm text-gray-400">결재 정보 없음</p>;
  }

  const statusConfig = {
    APR: {
      label: "승인",
      icon: <Check size={16} />,
      circle: "bg-green-500 border-green-500 text-white",
      line: "bg-green-500",
      label_color: "text-green-600",
    },
    REJ: {
      label: "반려",
      icon: <X size={16} />,
      circle: "bg-red-500 border-red-500 text-white",
      line: "bg-gray-200",
      label_color: "text-red-500",
    },
    PND: {
      label: "대기",
      icon: <Clock size={16} />,
      circle: "border-gray-300 text-gray-400 bg-white",
      line: "bg-gray-200",
      label_color: "text-gray-400",
    },
  };

  return (
    <div className="flex flex-col p-4 items-center">
      {approvalHistories.map((line, i) => {
        const config = statusConfig[line.approvalStatus] ?? statusConfig.PND;

        return (
          <div key={line.historyId} className="flex items-start gap-4">
            {/* 아이콘 + 연결선 */}
            <div className="flex flex-col items-center">
              <div
                className={`w-9 h-9 rounded-full border-2 flex items-center justify-center z-10
                ${config.circle}`}
              >
                {config.icon}
              </div>
              {/* 연결선 */}
              {i < approvalHistories.length - 1 && (
                <div className={`w-0.5 h-8 ${config.line}`} />
              )}
            </div>

            {/* 텍스트 */}
            <div className="pb-6">
              <p className="text-sm font-medium text-gray-800">{line.approver}</p>
              <p className={`text-xs mt-0.5 ${config.label_color}`}>
                {config.label}
              </p>
              {line.approvedAt && (
                <p className="text-xs text-gray-400 mt-0.5">{line.approvedAt}</p>
              )}
            </div>
          </div>
        );
      })}
    </div>
  );
}