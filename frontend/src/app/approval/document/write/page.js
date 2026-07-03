import DocumentWrite from "@/components/approval/DocumentWrite";
import Header from "@/components/common/Header";

export default function DocumentWritePage() {
  return (
    <div className="flex flex-col">
      <Header title="문서 작성" />
      <DocumentWrite />
    </div>
  );
}
