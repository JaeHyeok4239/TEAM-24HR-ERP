"use client";

import { apiRequest } from "@/lib/api";
import { useEffect, useState } from "react";
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from "../ui/table";
import { Input } from "../ui/input";
import { Button } from "../ui/button";
import { Card, CardContent } from "../ui/card";
import { Search, Trash2, Pencil } from "lucide-react";
import { useAuthStore } from "@/store/authStore";
import { hasAnyRole, HR_MANAGE_ROLES } from "@/lib/roles";
import { CommonPagination } from "../common/CommonPagination";

export default function ApprovalLineList() {
  const userInfo = useAuthStore((state) => state.userInfo);
  const isAdmin = hasAnyRole(userInfo?.roles, HR_MANAGE_ROLES);

  const [lineList, setLineList] = useState([]);
  const [keyword, setKeyword] = useState("");
  const [searchTrigger, setSearchTrigger] = useState(0);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const pageNumbers = Array.from({ length: totalPages }, (_, i) => i);

  useEffect(() => {
    const load = async () => {
      const params = new URLSearchParams({ page, size: 10 });
      if (keyword) params.append("keyword", keyword);
      const res = await apiRequest(`/api/approval/line?${params.toString()}`, {
        method: "GET",
      });
      const data = await res.json();

      console.log(keyword);
      console.log(data);
      setLineList(data.content ?? []);
      setTotalPages(data.totalPages ?? 0);
    };
    load();
  }, [page, searchTrigger]);

  const handleSearch = () => {
    setPage(0);
    setSearchTrigger((prev) => prev + 1);
  };

  const handleDelete = async (lineId) => {
    if (!confirm("삭제하시겠습니까?")) return;
    await apiRequest(`/api/approval/line/${lineId}`, { method: "DELETE" });
    setSearchTrigger((prev) => prev + 1);
  };

  // 데이터 그룹핑 유틸
  function groupLines(lines) {
    const groups = [];
    const seen = new Map();

    for (const line of lines) {
      const key = `${line.departmentName ?? "공통"}_${line.documentTypeName}`;
      if (!seen.has(key)) {
        seen.set(key, { key, rows: [] });
        groups.push(seen.get(key));
      }
      seen.get(key).rows.push(line);
    }
    return groups;
  }

  return (
    <div className="flex flex-col gap-4 p-5">
      <Card className="transition-transform duration-300 hover:-translate-y-1 hover:shadow-lg">
        <CardContent className="flex flex-col p-4 gap-4">
          <div className="flex gap-2 flex-wrap items-center">
            <div className="flex gap-2 flex-1">
              <Input
                placeholder="결재자, 부서명, 문서 유형명으로 검색"
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

          <div className="bg-[#ffffffbd] rounded-lg border-[#94abcaa1] overflow-hidden">
            {lineList.length === 0 ? (
              <p className="text-center text-sm text-gray-400 py-10">
                결재선이 없습니다
              </p>
            ) : (
              <Table>
                <TableHeader className="bg-[#94abcaa1]">
                  <TableRow className="text-[#1a2f4e]">
                    <TableHead className="font-bold text-center">
                      부서
                    </TableHead>
                    <TableHead className="font-bold text-center">
                      문서 유형
                    </TableHead>
                    <TableHead className="font-bold text-center">
                      단계
                    </TableHead>
                    <TableHead className="font-bold text-center">
                      결재자
                    </TableHead>
                    <TableHead className="font-bold text-center">
                      직급
                    </TableHead>
                    {isAdmin && (
                      <TableHead className="font-bold text-center">
                        관리
                      </TableHead>
                    )}
                  </TableRow>
                </TableHeader>
                <TableBody>
                  {groupLines(lineList).map((group) =>
                    group.rows.map((line, idx) => (
                      <TableRow
                        key={line.approvalLineId}
                        className="text-center text-[#1a2f4e]"
                      >
                        {/* 첫 행에만 부서/문서유형 렌더링, rowSpan으로 합치기 */}
                        {idx === 0 && (
                          <>
                            <TableCell
                              rowSpan={group.rows.length}
                              className="align-middle border-r"
                            >
                              {line.departmentName ?? "공통"}
                            </TableCell>
                            <TableCell
                              rowSpan={group.rows.length}
                              className="align-middle border-r"
                            >
                              {line.documentTypeName}
                            </TableCell>
                          </>
                        )}
                        <TableCell>{line.stepOrder}단계</TableCell>
                        <TableCell>{line.approverName}</TableCell>
                        <TableCell>{line.approverPosition}</TableCell>
                        {isAdmin && (
                          <TableCell>
                            <div className="flex gap-2 justify-center">
                              <Button
                                size="sm"
                                className="bg-[#ffc23f8c] hover:bg-[#ffae00]"
                              >
                                <Pencil size={14} />
                              </Button>
                              <Button
                                size="sm"
                                variant="destructive"
                                onClick={() =>
                                  handleDelete(line.approvalLineId)
                                }
                              >
                                <Trash2 size={14} />
                              </Button>
                            </div>
                          </TableCell>
                        )}
                      </TableRow>
                    )),
                  )}
                </TableBody>
              </Table>
            )}
          </div>
          <CommonPagination {...{ page, setPage, totalPages, pageNumbers }} />
        </CardContent>
      </Card>
    </div>
  );
}
