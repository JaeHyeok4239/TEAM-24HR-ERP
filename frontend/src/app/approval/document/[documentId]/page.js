"use client";

import DocumentDetail from "@/components/approval/DocumentDetail";
import Header from "@/components/common/Header";
import { Button } from "@/components/ui/button";
import { ChevronLeft } from "lucide-react";
import Link from "next/link";
import { use } from "react";

export default function DocumentDetailPage({ params }) {
  const { documentId } = use(params);

  return (
    <div className="flex flex-col h-screen overflow-hidden">
      <div className="flex justify-between items-center p-4 bg-blue-100/30">
        <span className="font-semibold">문서 상세</span>
        <Link
          href="/approval/document"
          className="mr-12 flex items-center text-sm text-gray-500 hover:text-gray-700"
        >
          <ChevronLeft size={16} className="mr-1" />
          목록으로
        </Link>
      </div>
      <DocumentDetail documentId={documentId} />
    </div>
  );
}
