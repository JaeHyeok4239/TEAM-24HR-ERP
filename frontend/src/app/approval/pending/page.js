import ApprovalBox from "@/components/approval/ApprovalBox";
import Header from "@/components/common/Header";
// 결재 페이지 예시
export default function PendingPage() {
  return (
    <div className="flex flex-col">
      <Header title="결재 대기함" />
      <ApprovalBox />
    </div>
  );
}
