import MyDocument from "@/components/approval/DocumentList";
import DocumentDetail from "@/components/approval/DocumentDetail";
import Header from "@/components/common/Header";
export default function DocumentPage() {
  return (
    <div className="flex flex-col">
      <Header title="문서함" />
      <MyDocument />
    </div>
  );
}
