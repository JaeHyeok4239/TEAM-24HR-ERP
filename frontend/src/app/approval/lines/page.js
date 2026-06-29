import ApprovalLineList from "@/components/approval/ApprovalLineList";
import Header from "@/components/common/Header";
// 결재 페이지 예시
export default function LinePage() {
  return (
    <div className="flex flex-col">
      <Header title="결재선 관리" />
      <ApprovalLineList />
    </div>
  );
}
