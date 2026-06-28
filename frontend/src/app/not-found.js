"use client";

import { Button } from "@/components/ui/button";
import { ShieldAlert } from "lucide-react";
import { useRouter } from "next/navigation";

export default function NotFoundPage() {
  const router = useRouter();

  return (
    <div className="min-h-screen flex flex-col items-center justify-center text-primary">
      
      <ShieldAlert />

      {/* 텍스트 */}
      <h1 className="mt-4 text-3xl font-bold mb-2">페이지를 찾을 수 없습니다</h1>
      <p className="text-gray-400 mb-6 text-center">
        요청하신 페이지가 존재하지 않거나 이동되었습니다.
      </p>

      {/* 버튼 */}
      <div className="flex gap-3">

        <Button size="lg" onClick={() => router.push("/")}>
          홈으로
        </Button>
      </div>
    </div>
  );
}