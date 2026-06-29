import ApprovalDelegateList from "@/components/approval/ApprovalDelegateList";
import Header from "@/components/common/Header";
// 결재 페이지 예시
export default function DelegatePage() {
  return (
    <div className="flex flex-col">
      <Header title="대리 결재 관리" />
      <ApprovalDelegateList />
    </div>
  );
}
