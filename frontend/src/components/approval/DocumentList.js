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
import {
  Pagination,
  PaginationContent,
  PaginationItem,
  PaginationLink,
  PaginationNext,
  PaginationPrevious,
} from "../ui/pagination";

import { usePagination } from "@/hooks/usePagination";

import {
  CommonPagination,
  CustomPagination,
} from "@/components/common/CommonPagination";
import { Button } from "../ui/button";
import Link from "next/link";
import { useRouter } from "next/navigation";
import { Badge } from "../ui/badge";
import DocumentDetail from "./DocumentDetail";
import { Card, CardContent, CardHeader } from "../ui/card";

export default function MyDocument() {
  const [selectedTab, setSelectedTab] = useState("my");

  const router = useRouter();

  const loadList = useCallback(
    async (page) => {
      const endpoint =
        selectedTab === "my"
          ? `/api/document?page=${page}`
          : `/api/document/tmp?page=${page}`;

      const response = await apiRequest(endpoint, {
        method: "GET",
      });
      return await response.json();
    },
    [selectedTab],
  );

  const { data, page, setPage, totalPages, isFirst, isLast } = usePagination(
    loadList,
    [selectedTab],
  );

  useEffect(() => {
    setPage(0);
  }, [selectedTab, setPage]);

  const pageNumbers = [];
  for (let i = 0; i < totalPages; i++) {
    pageNumbers.push(i);
  }

  const documentList = data;

  const [documentId, setDocumentId] = useState(null);

  useEffect(() => {
    console.log(documentId);
  }, [documentId]);

  // 화면 너비 감지
  const [isDesktop, setIsDesktop] = useState(false);

  useEffect(() => {
    const check = () => {
      const desktop = window.innerWidth >= 1280;
      setIsDesktop(desktop);
      if (!desktop) setDocumentId(null); // 모바일로 전환 시 초기화
    };
    check();
    window.addEventListener("resize", check);
    return () => window.removeEventListener("resize", check);
  }, []);

  // 클릭 핸들러
  const handleRowClick = (id) => {
    if (isDesktop) {
      setDocumentId(id);
    } else {
      router.push(`/approval/document/${id}`);
    }
  };

  if (data === null || data === undefined) {
    return <div className="p-5">로딩 중...</div>;
  }

  if (documentList.length === 0) {
    return (
      <div className="p-5">
        <h1>작성한 문서가 없습니다</h1>
      </div>
    );
  }
  return (
    <>
      <div className="flex flex-col xl:flex-row gap-4 p-5">
        {/* 왼쪽: 문서 목록 */}
        <div className="flex-1 h-full">
          <Card className={"xl:h-[calc(100vh-140px)] overflow-y-auto transition-transform duration-300 hover:-translate-y-1 hover:shadow-lg"}>
            <CardContent className={"flex-1 flex flex-col p-0"}>
              <Tabs
                value={selectedTab}
                onValueChange={setSelectedTab}
                defaultValue="my"
                className="flex-1 flex flex-col mx-2"
              >
                <TabsList variant="line" className="w-full mb-4">
                  <TabsTrigger value="my">내 문서함</TabsTrigger>
                  <TabsTrigger value="tmp">임시 저장함</TabsTrigger>
                </TabsList>
                <TabsContent value="my" className={"p-4"}>
                  <div className="flex-1 bg-[#ffffffbd] rounded-lg border-[#94abcaa1] flex-1 flex flex-col p-4 overflow-hidden">
                    <Table className={"flex-1 h-[550px]"}>
                      <TableHeader className="bg-[#94abcaa1] h-1/10">
                        <TableRow className="text-[#1a2f4e]">
                          <TableHead className="font-bold text-center">
                            문서 번호
                          </TableHead>
                          <TableHead className="font-bold text-center">
                            문서 종류
                          </TableHead>
                          <TableHead className="font-bold text-center">
                            문서 제목
                          </TableHead>
                          <TableHead className="font-bold text-center">
                            문서 상태
                          </TableHead>
                          <TableHead className="font-bold text-center">
                            결재 단계
                          </TableHead>
                          <TableHead className="font-bold text-center">
                            작성 시각
                          </TableHead>
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
                            <TableCell>{d.status}</TableCell>
                            <TableCell>{d.currentStep}</TableCell>
                            <TableCell>{d.createdAt}</TableCell>
                          </TableRow>
                        ))}
                      </TableBody>
                    </Table>
                    <div className="p-5 shrink-0">
                      <CommonPagination
                        {...{ page, setPage, totalPages, pageNumbers }}
                      />
                    </div>
                  </div>
                </TabsContent>
                <TabsContent value="tmp" className={"p-4"}>
                  <div className="flex-1 bg-[#ffffffbd] rounded-lg border-[#94abcaa1] flex-1 flex flex-col p-4 overflow-hidden">
                    <Table className={"flex-1 h-[550px]"}>
                      <TableHeader className="bg-[#94abcaa1] h-1/10">
                        <TableRow className="text-[#1a2f4e]">
                          <TableHead className="font-bold text-center">
                            문서 번호
                          </TableHead>
                          <TableHead className="font-bold text-center">
                            문서 종류
                          </TableHead>
                          <TableHead className="font-bold text-center">
                            문서 제목
                          </TableHead>
                          <TableHead className="font-bold text-center">
                            문서 상태
                          </TableHead>
                          <TableHead className="font-bold text-center">
                            작성 시각
                          </TableHead>
                          <TableHead className="font-bold text-center">
                            관리
                          </TableHead>
                        </TableRow>
                      </TableHeader>
                      <TableBody>
                        {documentList.map((d) => (
                          <TableRow
                            key={d.documentId}
                            className="text-center text-[#1a2f4e]"
                          >
                            <TableCell>{d.documentId}</TableCell>
                            <TableCell>{d.documentTypeName}</TableCell>
                            <TableCell>{d.documentTitle}</TableCell>
                            <TableCell>{d.status}</TableCell>
                            <TableCell>{d.createdAt}</TableCell>
                            <TableCell>
                              <Button
                                className={
                                  "mx-2 bg-[#ffc23f8c] hover:bg-[#ffae00]"
                                }
                                size="lg"
                              >
                                수정
                              </Button>
                              <Button variant="destructive" size="lg">
                                삭제
                              </Button>
                            </TableCell>
                          </TableRow>
                        ))}
                      </TableBody>
                    </Table>
                    <div className="p-5">
                      <CommonPagination
                        {...{ page, setPage, totalPages, pageNumbers }}
                      />
                    </div>
                  </div>
                </TabsContent>
              </Tabs>
            </CardContent>
          </Card>
        </div>
        {/* 오른쪽: 문서 상세 */}
        {documentId && (
          <div className="flex-1 sticky top-5">
            <Card className={"xl:h-[calc(100vh-140px)] overflow-y-auto transition-transform duration-200 hover:-translate-y-1 hover:shadow-lg"} onClick={() => router.push(`/approval/document/${documentId}`)}>
              <CardContent>
                <DocumentDetail key={documentId} documentId={documentId} />
              </CardContent>
            </Card>
          </div>
        )}
      </div>
    </>
  );
}