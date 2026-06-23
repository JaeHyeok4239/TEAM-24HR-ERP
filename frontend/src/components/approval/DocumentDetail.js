"use client";

import { apiRequest } from "@/lib/api";
import { useCallback, useEffect, useMemo, useState } from "react";
import { Card, CardContent, CardHeader } from "../ui/card";
import { stringify } from "postcss/lib/postcss";
import { Badge } from "../ui/badge";
import ApprovalStepper from "./ApprovalStepprer";

export default function DocumentDetail({ documentId }) {
  const [document, setDocument] = useState(null);
  const isLoading = useMemo(() => {
    if (document === null) {
      return false;
    } else {
      return true;
    }
  }, [document]);

  useEffect(() => {
    const loadData = async () => {
      try {
        const response = await apiRequest(`/api/document/${documentId}`, {
          method: "GET",
        });

        const data = await response.json();

        console.log("data", data);

        setDocument(data);
      } catch (err) {
        throw new Error("데이터를 불러올 수 없습니다");
      }
    };
    if (documentId) {
      loadData();
    }
  }, [documentId]);
  if (!isLoading) {
    return <>로딩중</>;
  }
  return (
    <div className="p-4 space-y-4">
      {/* 문서 정보 */}
      <Card>
        <CardHeader>
          <h2 className="text-lg font-medium">문서 정보</h2>
        </CardHeader>
        <CardContent className="grid grid-cols-2 gap-4 text-sm">
          <div>
            <span className="text-gray-500">문서번호</span>
            <p>No.{document.documentId}</p>
          </div>
          <div>
            <span className="text-gray-500">문서유형</span>
            <p>{document.documentType}</p>
          </div>
          <div>
            <span className="text-gray-500">제목</span>
            <p>{document.documentTitle}</p>
          </div>
          <div>
            <span className="text-gray-500">기안자</span>
            <p>{document.requester}</p>
          </div>
          <div>
            <span className="text-gray-500">상태</span>
            <p>{document.documentStatus}</p>
          </div>
          <div>
            <span className="text-gray-500">기안일시</span>
            <p>{document.requestedAt}</p>
          </div>
          <div></div>
          <div className="col-span-2">
            <span className="text-gray-500">문서 내용</span>
            <div className="mt-1 space-y-1">
              {Object.entries(document.documentContent).map(([key, value]) => (
                <div key={key} className="flex gap-2">
                  <span className="text-gray-400">{key}:</span>
                  <span>{value}</span>
                </div>
              ))}
            </div>
          </div>
        </CardContent>
      </Card>

      {/* 결재 현황 */}
      <Card>
        <CardHeader>
          <h2 className="text-lg font-medium">결재 현황</h2>
        </CardHeader>
        <CardContent>
          <ApprovalStepper approvalHistories={document.approvalHistories} />
        </CardContent>
      </Card>

      {/* 첨부 파일 */}
      <Card>
        <CardHeader>
          <h2 className="text-lg font-medium">첨부 파일</h2>
        </CardHeader>
        <CardContent>
          {document.documentFileList.length === 0 ? (
            <p className="text-sm text-gray-400">첨부 파일 없음</p>
          ) : (
            <ul>
              {document.documentFileList.map((file, i) => (
                <li key={i}>{file.fileName}</li>
              ))}
            </ul>
          )}
        </CardContent>
      </Card>
    </div>
  );
}
