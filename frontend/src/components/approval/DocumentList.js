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

import Swal from "sweetalert2";

import { usePagination } from "@/hooks/usePagination";

import { CommonPagination } from "@/components/common/CommonPagination";
import { Button } from "../ui/button";
import { useRouter } from "next/navigation";
import DocumentDetail from "./DocumentDetail";
import { Card, CardContent, CardHeader } from "../ui/card";
import { FileText, Plus } from "lucide-react";
import Link from "next/link";
import { toast } from "sonner";

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

  return (
    <>
      <div className="h-full flex flex-col xl:flex-row gap-4 p-5 max-w-full">
        {/* 왼쪽: 문서 목록 */}
        <div className="flex-1 h-full">
          <Card
            className={
              "xl:h-full overflow-y-auto transition-transform duration-300 hover:-translate-y-1 hover:shadow-lg"
            }
          >
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
                  {documentList.length === 0 && selectedTab === "my" ? (
                    <div className="flex flex-col items-center justify-center gap-4 p-20 text-center">
                      <Card className="w-full max-w-md">
                        <CardContent className="flex flex-col items-center gap-3 py-10">
                          <FileText size={48} className="text-gray-300" />
                          <h2 className="text-lg font-semibold text-[#1a2f4e]">
                            작성한 문서가 없습니다
                          </h2>
                          <p className="text-sm text-gray-400">
                            새 문서를 작성하려면 아래 버튼을 클릭하세요
                          </p>
                          <Link href="/approval/document/write">
                            <Button className="mt-2 bg-[#1a2f4e] hover:bg-[#2a4a6e] gap-1">
                              <Plus size={16} />
                              문서 작성하기
                            </Button>
                          </Link>
                        </CardContent>
                      </Card>
                    </div>
                  ):(<div className="flex-1 bg-[#ffffffbd] rounded-lg border-[#94abcaa1] flex flex-col p-4 overflow-hidden">
                    <Table className={"flex-1 h-[550px] max-w-full"}>
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
                  </div>)}
                </TabsContent>
                <TabsContent value="tmp" className={"p-4"}>
                  {documentList.length === 0 && selectedTab === "tmp" && (
                    <div className="flex flex-col items-center justify-center gap-4 p-20 text-center">
                      <Card className="w-full max-w-md">
                        <CardContent className="flex flex-col items-center gap-3 py-10">
                          <FileText size={48} className="text-gray-300" />
                          <h2 className="text-lg font-semibold text-[#1a2f4e]">
                            작성한 문서가 없습니다
                          </h2>
                          <p className="text-sm text-gray-400">
                            새 문서를 작성하려면 아래 버튼을 클릭하세요
                          </p>
                          <Link href="/approval/document/write">
                            <Button className="mt-2 bg-[#1a2f4e] hover:bg-[#2a4a6e] gap-1">
                              <Plus size={16} />
                              문서 작성하기
                            </Button>
                          </Link>
                        </CardContent>
                      </Card>
                    </div>
                  )}
                  {documentList.length > 0 && (
                    <div className="bg-[#ffffffbd] rounded-lg border-[#94abcaa1] flex-1 flex flex-col p-4 overflow-hidden">
                      <Table className={"flex-1 h-[550px] max-w-full"}>
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
                            className="text-center text-[#1a2f4e] cursor-pointer"
                            onClick={() => handleRowClick(d.documentId)}
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
                                onClick={() =>
                                  router.push(
                                    `/approval/document/${d.documentId}/edit`,
                                  )
                                }
                                size="lg"
                              >
                                수정
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
                  </div>)}
                </TabsContent>
              </Tabs>
            </CardContent>
          </Card>
        </div>
        {/* 오른쪽: 문서 상세 */}
        {documentId && (
          <div className="flex-1 sticky top-5">
            <Card
              className={
                "xl:h-[calc(100vh-140px)] overflow-y-auto transition-transform duration-200 hover:-translate-y-1 hover:shadow-lg"
              }
              onClick={() => router.push(`/approval/document/${documentId}`)}
            >
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
