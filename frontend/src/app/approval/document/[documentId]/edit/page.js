"use client";

import DocumentWrite from "@/components/approval/DocumentWrite";
import Header from "@/components/common/Header";
import { ChevronLeft } from "lucide-react";
import Link from "next/link";
import { use } from "react";

export default function DocumentEditPage({ params }) {
  const { documentId } = use(params);

  return (
    <div className="flex flex-col h-screen overflow-hidden">
     <Header title="문서 수정"/>
      <DocumentWrite documentId={documentId} />
    </div>
  );
}
