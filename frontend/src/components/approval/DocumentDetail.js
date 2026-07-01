"use client";

import { useRouter } from "next/navigation";
import { apiRequest } from "@/lib/api";
import { useCallback, useEffect, useMemo, useState } from "react";
import { Card, CardContent, CardHeader } from "../ui/card";
import { stringify } from "postcss/lib/postcss";
import { Badge } from "../ui/badge";
import ApprovalStepper from "./ApprovalStepprer";
import {
  Car,
  CheckCircle,
  ChevronLeft,
  Clock,
  MessageCircleWarning,
  X,
} from "lucide-react";
import Link from "next/link";
import { Button } from "../ui/button";
import { documentSchemas } from "@/schema/DocumentSchema";
import Header from "../common/Header";
import { useAuthStore } from "@/store/authStore";
import Swal from "sweetalert2";

export default function DocumentDetail({ documentId }) {
  const [document, setDocument] = useState(null);
  const router = useRouter();
  const userInfo = useAuthStore((state) => state.userInfo);
  const loginId = userInfo?.loginId;
  const currentUserId = userInfo?.employeeId;
  const [comment, setComment] = useState("");
  const isOwner = document?.requesterId === currentUserId;

  useEffect(() => {
    const loadData = async () => {
      try {
        const response = await apiRequest(`/api/document/${documentId}`, {
          method: "GET",
        });

        const data = await response.json();

        console.log("data", data);

        console.log(loginId);

        setDocument(data);
      } catch (err) {
        throw new Error("데이터를 불러올 수 없습니다");
      }
    };
    if (documentId) {
      loadData();
    }
  }, [documentId]);

  //결재 승인
  const handleApprove = useCallback(async () => {
    const result = await Swal.fire({
      title: "결재 승인",
      text: "결재를 승인하시겠습니까?",
      icon: "success",
      showCancelButton: true,
      cancelButtonColor: "#C8D8E5",
      confirmButtonColor: "#759EBD",
      confirmButtonText: "승인",
      cancelButtonText: "취소",
    });

    if (result.isConfirmed) {
      await apiRequest(`/api/approval/${documentId}/approve`, {
        method: "POST",
        body: JSON.stringify({ action: "APR", comment }),
      });
      router.push("/approval/pending");
    }
  }, [documentId, comment]);

  const handleReject = useCallback(async () => {
    const rejectComment = prompt("반려 사유를 입력하세요");
    if (!rejectComment) return;
    await apiRequest(`/api/approval/${documentId}/approve`, {
      method: "POST",
      body: JSON.stringify({ action: "REJ", comment: rejectComment }),
    });
    router.push("/approval/pending");
  }, [documentId]);

  const schema = documentSchemas?.[document?.typeDetailTable];
  const content = document?.documentContent;

  if (!documentId) return null;

  if (!document) {
    return <>로딩중</>;
  }

  // 결재자 여부
  const canApprove = document.approvalHistories?.some(
    (h) =>
      h.approvalStatus === "PND" &&
      h.stepOrder === document.currentStep &&
      Number(h.approverId) === Number(currentUserId),
  );
  console.log(document.approvalHistories);
  console.log(canApprove);
  console.log("currentUserId", currentUserId);
  console.log("currentStep", document.currentStep);

  return (
    <>
      {/* 넓은 화면: 2열, 좁은 화면: 1열 */}
      <div className="flex-1 overflow-auto grid grid-cols-1 xl:grid-cols-2 gap-4 p-10">
        {/* 왼쪽: 문서 정보 + 첨부 파일 */}
        <div className="flex-1 flex flex-col gap-4">
          <Card className={"flex-1"}>
            <CardHeader>
              <h2 className="text-lg font-semibold">문서 정보</h2>
            </CardHeader>
            <CardContent className="grid grid-cols-2 gap-4 text-sm">
              <div>
                <span className="text-gray-500 text-xs">문서번호</span>
                <p>No.{document.documentId}</p>
              </div>
              <div>
                <span className="text-gray-500 text-xs">문서유형</span>
                <p className="text-base">{document.documentTypeName}</p>
              </div>
              <div>
                <span className="text-gray-500 text-xs">제목</span>
                <p className="text-base">{document.documentTitle}</p>
              </div>
              <div>
                <span className="text-gray-500 text-xs">기안자</span>
                <p className="text-base">{document.requester}</p>
              </div>
              <div>
                <span className="text-gray-500 text-xs">상태</span>
                <p className="text-base">{document.documentStatus}</p>
              </div>
              <div>
                <span className="text-gray-500 text-xs">기안일시</span>
                <p className="text-base font-medium">{document.requestedAt}</p>
              </div>
              <div className="col-span-2">
                <span className="text-gray-500 text-xs">문서 내용</span>
                {content && schema && (
                  <div className="mt-2 space-y-2 p-5 bg-gray-50 rounded-lg">
                    {Object.entries(schema).map(([key, meta]) => (
                      <div key={key} className="flex gap-2">
                        <span className="text-gray-400 text-sm min-w-24">
                          {meta.label}:
                        </span>
                        <span className="text-sm">
                          {Array.isArray(content?.[key])
                            ? content[key].join(", ")
                            : content?.[key]}
                        </span>
                      </div>
                    ))}
                  </div>
                )}
              </div>
              <div className="col-span-2">
                <span className="text-gray-500 text-xs">첨부 파일</span>
                <div className="mt-2 p-5 bg-gray-50 rounded-lg">
                  {document.documentFileList.length === 0 ? (
                    <p className="text-sm text-gray-400">첨부 파일 없음</p>
                  ) : (
                    <ul className="space-y-1">
                      {document.documentFileList.map((file, i) => (
                        <li key={i} className="text-sm">
                          {file.fileName}
                        </li>
                      ))}
                    </ul>
                  )}
                </div>
              </div>
            </CardContent>
          </Card>
        </div>
        {/* 오른쪽: 결재 현황 + 반려 문서 */}
        <div className="flex-1 flex flex-col gap-4">
          <Card className="flex-1 flex flex-col">
            <CardHeader>
              <h2 className="text-lg font-semibold">결재 현황</h2>
            </CardHeader>
            <CardContent className="flex-1 flex flex-col">
              {/* 스테퍼 가운데 정렬 */}
              <div className="flex-1 flex flex-col basis-2/3 items-center justify-center">
                <ApprovalStepper
                  approvalHistories={document.approvalHistories}
                />
              </div>

              {/* 상태 메시지 */}
              <div className="flex flex-col items-center basis-1/3 justify-center gap-2 text-[#72abb977] py-4 border-t">
                {document.documentStatus === "COM" && (
                  <>
                    <CheckCircle size={32} className="text-green-400" />
                    <p className="text-sm font-medium text-green-500">
                      처리 완료된 문서입니다
                    </p>
                    <p className="text-xs">담당자 : {document.processor}</p>
                    <p className="text-xs">{document.processedAt}</p>
                  </>
                )}
                {document.documentStatus === "PRC" && (
                  <>
                    <CheckCircle size={32} className="text-cyan-400" />
                    <p className="text-sm font-medium text-cyan-500">
                      처리 대기 중인 문서입니다
                    </p>
                  </>
                )}

                {document.documentStatus === "REQ" &&
                  document.documentVersion === 1 && (
                    <>
                      <Clock size={32} className="text-gray-300" />
                      <p className="text-sm">결재 진행 중입니다</p>
                    </>
                  )}

                {document.documentStatus === "REQ" &&
                  document.documentVersion !== 1 && (
                    <>
                      <Clock size={32} className="text-gray-300" />
                      <p className="text-sm">(재)결재 진행 중입니다</p>
                    </>
                  )}

                {document.documentStatus === "APR" && (
                  <>
                    <CheckCircle size={32} className="text-blue-400" />
                    <p className="text-sm font-medium text-blue-500">
                      결재 승인된 문서입니다
                    </p>
                  </>
                )}

                {document.documentStatus === "REJ" && (
                  <>
                    <MessageCircleWarning size={32} className="text-red-400" />
                    <p className="text-sm font-medium text-red-500 mb-2">
                      반려 처리된 문서입니다
                    </p>
                    <p className="text-xs text-gray-400 mb-2">
                      수정 후 재기안 요청이 가능합니다
                    </p>
                    <p className="text-gray-600">
                      반려 사유 : {document.rejectReason}
                    </p>
                    <Button
                      size="sm"
                      className="mt-1  bg-[#ffc23f8c] hover:bg-[#ffae00]"
                      onClick={() =>
                        router.push(`/approval/document/${documentId}/edit`)
                      }
                    >
                      수정
                    </Button>
                  </>
                )}
              </div>
            </CardContent>
          </Card>
        </div>

        {isOwner && document.documentStatus === "TMP" && (
          <div className="flex justify-end">
            <Button
              size="lg"
              className="mt-1  bg-[#ffc23f8c] hover:bg-[#ffae00]"
              onClick={() =>
                router.push(`/approval/document/${documentId}/edit`)
              }
            >
              수정
            </Button>
          </div>
        )}

        {canApprove && (
          <div className="flex gap-2 mt-4">
            <Button
              className="flex-1 bg-[#1a2f4e] hover:bg-[#2a4a6e]"
              onClick={() => handleApprove(document.documentId)}
            >
              <CheckCircle size={16} className="mr-1" />
              승인
            </Button>
            <Button
              variant="destructive"
              className="flex-1"
              onClick={() => handleReject(document.documentId)}
            >
              <X size={16} className="mr-1" />
              반려
            </Button>
          </div>
        )}
      </div>
    </>
  );
}
