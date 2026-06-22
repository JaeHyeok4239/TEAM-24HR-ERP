import MyDocument from "@/components/approval/DocumentList";
import DocumentDetail from "@/components/approval/DocumentDetail";
import Header from "@/components/Header";
// 결재 페이지 예시
export default function DocumentPage() {
  return (
    <div>
      <Header title="문서함" />
      <MyDocument />
    </div>
  );
}
