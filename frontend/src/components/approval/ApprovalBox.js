"use client";

import { apiRequest } from "@/lib/api";
import { useCallback, useEffect, useState } from "react";
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from "../ui/table";
import { Tabs, TabsContent, TabsList, TabsTrigger } from "../ui/tabs";
import { usePagination } from "@/hooks/usePagination";
import { CommonPagination } from "@/components/common/CommonPagination";
import { Input } from "../ui/input";
import { Button } from "../ui/button";
import { useRouter } from "next/navigation";
import { Card, CardContent } from "../ui/card";
import { ClipboardCheck, FolderOpen, Search } from "lucide-react";
import MyDocument from "./DocumentList";
import Link from "next/link";

const STATUS_LABEL = {
  PND: "대기",
  APR: "승인",
  REJ: "반려",
  CAN: "취소",
};

export default function ApprovalBox() {
  const [selectedTab, setSelectedTab] = useState("pending");
  const router = useRouter();

  // 이력 검색 필터
  const [keyword, setKeyword] = useState("");
  const [status, setStatus] = useState("");
  const [searchTrigger, setSearchTrigger] = useState(0);

  const loadList = useCallback(
    async (page) => {
      let endpoint;
      if (selectedTab === "pending") {
        endpoint = `/api/approval/?page=${page}&size=10`;
      } else {
        const params = new URLSearchParams({ page, size: 10 });
        if (keyword) params.append("keyword", keyword);
        if (status) params.append("status", status);
        endpoint = `/api/approval/history?${params.toString()}`;
      }
      const response = await apiRequest(endpoint, { method: "GET" });
      return await response.json();
    },
    [selectedTab, searchTrigger],
  );

  const { data, page, setPage, totalPages, pageNumbers, isFirst, isLast } = usePagination(
    loadList,
    [selectedTab, searchTrigger],
  );

  useEffect(() => {
    setPage(0);
  }, [selectedTab, setPage]);

  const handleSearch = () => {
    setPage(0);
    setSearchTrigger((prev) => prev + 1);
  };

  const handleRowClick = (documentId) => {
    router.push(`/approval/document/${documentId}`);
  };

  if (data === null || data === undefined) {
    return <div className="p-5">로딩 중...</div>;
  }

  const documentList = data;

    if (documentList.length === 0) {
    return (
      <div className="flex flex-col items-center justify-center gap-4 p-20 text-center">
        <Card className="w-full max-w-md">
          <CardContent className="flex flex-col items-center gap-3 py-10">
            <ClipboardCheck size={48} className="text-gray-300" />
            <h2 className="text-lg font-semibold text-[#1a2f4e]">
              확인할 결재 이력이 없습니다
            </h2>
            <p className="text-sm text-gray-400">
              작성한 문서를 확인하려면 버튼을 클릭하세요
            </p>
            <Link href="/approval/document">
              <Button className="mt-2 bg-[#1a2f4e] hover:bg-[#2a4a6e] gap-1">
                <FolderOpen size={16} />
                내 문서함으로 이동
              </Button>
            </Link>
          </CardContent>
        </Card>
      </div>
    );
  }

  return (
    <div className="flex flex-col gap-4 p-5">
      <Card className="xl:h-[calc(100vh-140px)] overflow-y-auto transition-transform duration-300 hover:-translate-y-1 hover:shadow-lg">
        <CardContent className="flex-1 flex flex-col p-0">
          <Tabs
            value={selectedTab}
            onValueChange={setSelectedTab}
            defaultValue="pending"
            className="flex-1 flex flex-col mx-2"
          >
            <TabsList variant="line" className="w-full mb-4">
              <TabsTrigger value="pending">결재 대기함</TabsTrigger>
              <TabsTrigger value="history">결재 이력 보관함</TabsTrigger>
            </TabsList>

            {/* 결재 대기함 */}
            <TabsContent value="pending" className="p-4">
              <div className="bg-[#ffffffbd] rounded-lg border-[#94abcaa1] flex flex-col p-4 overflow-hidden">
                {documentList.length === 0 ? (
                  <p className="text-center text-sm text-gray-400 py-10">
                    대기 중인 결재가 없습니다
                  </p>
                ) : (
                  <Table className="flex-1 h-[550px]">
                    <TableHeader className="bg-[#94abcaa1]">
                      <TableRow className="text-[#1a2f4e]">
                        <TableHead className="font-bold text-center">문서 번호</TableHead>
                        <TableHead className="font-bold text-center">문서 종류</TableHead>
                        <TableHead className="font-bold text-center">문서 제목</TableHead>
                        <TableHead className="font-bold text-center">기안자</TableHead>
                        <TableHead className="font-bold text-center">결재 단계</TableHead>
                        <TableHead className="font-bold text-center">요청 시각</TableHead>
                      </TableRow>
                    </TableHeader>
                    <TableBody>
                      {documentList.map((d) => (
                        <TableRow
                          key={d.documentId}
                          onClick={() => handleRowClick(d.documentId)}
                          className="text-center text-[#1a2f4e] cursor-pointer"
                        >
                          <TableCell>{d.documentId}</TableCell>
                          <TableCell>{d.documentTypeName}</TableCell>
                          <TableCell>{d.documentTitle}</TableCell>
                          <TableCell>{d.requesterName}</TableCell>
                          <TableCell>{d.stepOrder}단계</TableCell>
                          <TableCell>{d.createdAt}</TableCell>
                        </TableRow>
                      ))}
                    </TableBody>
                  </Table>
                )}
                <div className="p-5 shrink-0">
                  <CommonPagination
                    {...{ page, setPage, totalPages, pageNumbers }}
                  />
                </div>
              </div>
            </TabsContent>

            {/* 결재 이력 보관함 */}
            <TabsContent value="history" className="p-4">
              <div className="bg-[#ffffffbd] rounded-lg border-[#94abcaa1] flex flex-col p-4 overflow-hidden gap-4">
                {/* 검색 필터 */}
                <div className="flex gap-2 items-center">
                  <select
                    value={status}
                    onChange={(e) => setStatus(e.target.value)}
                    className="border border-input bg-input/50 rounded-2xl px-3 py-2 text-sm outline-none focus-visible:border-ring transition-[color,box-shadow] duration-200"
                  >
                    <option value="">전체 상태</option>
                    <option value="APR">승인</option>
                    <option value="REJ">반려</option>
                    <option value="CAN">취소</option>
                  </select>
                  <div className="flex gap-2 flex-1">
                    <Input
                      placeholder="제목 검색"
                      value={keyword}
                      onChange={(e) => setKeyword(e.target.value)}
                      onKeyDown={(e) => e.key === "Enter" && handleSearch()}
                      className="flex-1"
                    />
                    <Button
                      onClick={handleSearch}
                      className="bg-[#1a2f4e] hover:bg-[#2a4a6e] gap-1"
                    >
                      <Search size={16} />
                      검색
                    </Button>
                  </div>
                </div>

                {documentList.length === 0 ? (
                  <p className="text-center text-sm text-gray-400 py-10">
                    결재 이력이 없습니다
                  </p>
                ) : (
                  <Table className="flex-1 h-[500px]">
                    <TableHeader className="bg-[#94abcaa1]">
                      <TableRow className="text-[#1a2f4e]">
                        <TableHead className="font-bold text-center">문서 번호</TableHead>
                        <TableHead className="font-bold text-center">문서 종류</TableHead>
                        <TableHead className="font-bold text-center">문서 제목</TableHead>
                        <TableHead className="font-bold text-center">기안자</TableHead>
                        <TableHead className="font-bold text-center">결재 결과</TableHead>
                        <TableHead className="font-bold text-center">처리 시각</TableHead>
                      </TableRow>
                    </TableHeader>
                    <TableBody>
                      {documentList.map((d) => (
                        <TableRow
                          key={d.documentId}
                          onClick={() => handleRowClick(d.documentId)}
                          className="text-center text-[#1a2f4e] cursor-pointer"
                        >
                          <TableCell>{d.documentId}</TableCell>
                          <TableCell>{d.documentTypeName}</TableCell>
                          <TableCell>{d.documentTitle}</TableCell>
                          <TableCell>{d.requesterName}</TableCell>
                          <TableCell>
                            <span
                              className={
                                d.status === "APR"
                                  ? "text-green-600 font-semibold"
                                  : d.status === "REJ"
                                    ? "text-red-500 font-semibold"
                                    : "text-gray-500"
                              }
                            >
                              {STATUS_LABEL[d.status] ?? d.status}
                            </span>
                          </TableCell>
                          <TableCell>{d.createdAt}</TableCell>
                        </TableRow>
                      ))}
                    </TableBody>
                  </Table>
                )}
                <div className="p-5 shrink-0">
                  <CommonPagination
                    {...{ page, setPage, totalPages, pageNumbers, isFirst, isLast }}
                  />
                </div>
              </div>
            </TabsContent>
          </Tabs>
        </CardContent>
      </Card>
    </div>
  );
}