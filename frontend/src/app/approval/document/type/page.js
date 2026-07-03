import DocumentTypeList from "@/components/approval/DocumentTypeList";
import Header from "@/components/common/Header";

export default function DocumentTypePage() {
    return (
        <div className="flex flex-col h-screen overflow-hidden">
             <Header title="문서 종류"/>
              <DocumentTypeList />
            </div>
    );
}