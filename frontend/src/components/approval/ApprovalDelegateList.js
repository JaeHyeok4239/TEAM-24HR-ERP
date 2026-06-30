"use client";

import { apiRequest } from "@/lib/api";
import { useEffect, useState } from "react";
import {
  Table, TableBody, TableCell, TableHead, TableHeader, TableRow,
} from "../ui/table";
import { Input } from "../ui/input";
import { Button } from "../ui/button";
import { Card, CardContent } from "../ui/card";
import { Search, Plus, X } from "lucide-react";
import { useAuthStore } from "@/store/authStore";
import { hasAnyRole, HR_MANAGE_ROLES } from "@/lib/roles";
import { CommonPagination } from "../common/CommonPagination";

export default function ApprovalDelegateList() {
  const userInfo = useAuthStore((state) => state.userInfo);
  const isAdmin = hasAnyRole(userInfo?.roles, HR_MANAGE_ROLES);

  const [delegateList, setDelegateList] = useState([]);
  const [keyword, setKeyword] = useState("");
  const [searchTrigger, setSearchTrigger] = useState(0);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const pageNumbers = Array.from({ length: totalPages }, (_, i) => i);

  // 생성 폼 상태
  const [showForm, setShowForm] = useState(false);
  const [form, setForm] = useState({
    approverId: "",
    delegateId: "",
    approvalLineId: "",
    startDate: "",
    endDate: "",
    reason: "",
  });

  // 결재선 목록 (폼에서 선택용)
  const [lineList, setLineList] = useState([]);

  useEffect(() => {
    const load = async () => {
      const params = new URLSearchParams({ page, size: 10 });
      if (keyword) params.append("keyword", keyword);
      const res = await apiRequest(`/api/approval/management/delegate?${params.toString()}`, { method: "GET" });
      const data = await res.json();
      setDelegateList(data.content ?? []);
      setTotalPages(data.totalPages ?? 0);
    };
    load();
  }, [page, searchTrigger]);

  useEffect(() => {
    if (!showForm) return;
    const load = async () => {
      const res = await apiRequest("/api/approval/line?size=100", { method: "GET" });
      const data = await res.json();
      setLineList(data.content ?? []);
    };
    load();
  }, [showForm]);

  const handleSearch = () => { setPage(0); setSearchTrigger(prev => prev + 1); };

  const handleDeactivate = async (delegateId) => {
    if (!confirm("비활성화하시겠습니까?")) return;
    await apiRequest(`/api/approval/management/delegate/${delegateId}`, { method: "PATCH" });
    setSearchTrigger(prev => prev + 1);
  };

  const handleCreate = async () => {
    if (!form.approverId || !form.delegateId || !form.approvalLineId || !form.startDate || !form.endDate) {
      return alert("모든 필드를 입력해주세요.");
    }
    await apiRequest("/api/approval/management/delegate", {
      method: "POST",
      body: JSON.stringify({
        approverId: Number(form.approverId),
        delegateId: Number(form.delegateId),
        approvalLineId: Number(form.approvalLineId),
        startDate: form.startDate,
        endDate: form.endDate,
        reason: form.reason,
      }),
    });
    setShowForm(false);
    setForm({ approverId: "", delegateId: "", approvalLineId: "", startDate: "", endDate: "", reason: "" });
    setSearchTrigger(prev => prev + 1);
  };

  return (
    <div className="flex flex-col gap-4 p-5">
      <Card className="transition-transform duration-300 hover:-translate-y-1 hover:shadow-lg">
        <CardContent className="flex flex-col p-4 gap-4">

          {/* 검색 + 생성 버튼 */}
          <div className="flex gap-2 items-center">
            <div className="flex gap-2 flex-1">
              <Input
                placeholder="결재자/대리결재자 이름 검색"
                value={keyword}
                onChange={(e) => setKeyword(e.target.value)}
                onKeyDown={(e) => e.key === "Enter" && handleSearch()}
                className="flex-1"
              />
              <Button onClick={handleSearch} className="bg-[#1a2f4e] hover:bg-[#2a4a6e] gap-1">
                <Search size={16} />
                검색
              </Button>
            </div>
            <Button
              onClick={() => setShowForm(prev => !prev)}
              className="bg-[#ffc23f8c] hover:bg-[#ffae00] gap-1"
            >
              <Plus size={16} />
              대리결재 설정
            </Button>
          </div>

          {/* 생성 폼 */}
          {showForm && (
            <div className="bg-gray-50 rounded-2xl p-4 flex flex-col gap-3 border border-gray-200">
              <h3 className="text-sm font-semibold text-[#1a2f4e]">대리결재 설정</h3>
              <div className="grid grid-cols-1 md:grid-cols-2 gap-3">
                <div className="flex flex-col gap-1">
                  <label className="text-xs text-gray-500">결재선</label>
                  <select
                    value={form.approvalLineId}
                    onChange={(e) => setForm(prev => ({ ...prev, approvalLineId: e.target.value }))}
                    className="border border-input bg-white rounded-2xl px-3 py-2 text-sm outline-none"
                  >
                    <option value="">결재선 선택</option>
                    {lineList.map((l) => (
                      <option key={l.approvalLineId} value={l.lineId}>
                        [{l.documentTypeName}] {l.departmentName ?? "공통"} - {l.stepOrder}단계 ({l.approverName})
                      </option>
                    ))}
                  </select>
                </div>
                <div className="flex flex-col gap-1">
                  <label className="text-xs text-gray-500">결재자 ID</label>
                  <Input
                    type="number"
                    placeholder="결재자 employeeId"
                    value={form.approverId}
                    onChange={(e) => setForm(prev => ({ ...prev, approverId: e.target.value }))}
                  />
                </div>
                <div className="flex flex-col gap-1">
                  <label className="text-xs text-gray-500">대리결재자 ID</label>
                  <Input
                    type="number"
                    placeholder="대리결재자 employeeId"
                    value={form.delegateId}
                    onChange={(e) => setForm(prev => ({ ...prev, delegateId: e.target.value }))}
                  />
                </div>
                <div className="flex flex-col gap-1">
                  <label className="text-xs text-gray-500">사유</label>
                  <Input
                    placeholder="대리결재 사유"
                    value={form.reason}
                    onChange={(e) => setForm(prev => ({ ...prev, reason: e.target.value }))}
                  />
                </div>
                <div className="flex flex-col gap-1">
                  <label className="text-xs text-gray-500">시작일</label>
                  <Input
                    type="date"
                    value={form.startDate}
                    onChange={(e) => setForm(prev => ({ ...prev, startDate: e.target.value }))}
                  />
                </div>
                <div className="flex flex-col gap-1">
                  <label className="text-xs text-gray-500">종료일</label>
                  <Input
                    type="date"
                    value={form.endDate}
                    onChange={(e) => setForm(prev => ({ ...prev, endDate: e.target.value }))}
                  />
                </div>
              </div>
              <div className="flex gap-2 justify-end">
                <Button variant="outline" onClick={() => setShowForm(false)}>취소</Button>
                <Button onClick={handleCreate} className="bg-[#1a2f4e] hover:bg-[#2a4a6e]">저장</Button>
              </div>
            </div>
          )}

          {/* 테이블 */}
          <div className="bg-[#ffffffbd] rounded-lg overflow-hidden">
            {delegateList.length === 0 ? (
              <p className="text-center text-sm text-gray-400 py-10">대리결재 설정이 없습니다</p>
            ) : (
              <Table>
                <TableHeader className="bg-[#94abcaa1]">
                  <TableRow className="text-[#1a2f4e]">
                    <TableHead className="font-bold text-center">결재자</TableHead>
                    <TableHead className="font-bold text-center">대리결재자</TableHead>
                    <TableHead className="font-bold text-center">결재선</TableHead>
                    <TableHead className="font-bold text-center">기간</TableHead>
                    <TableHead className="font-bold text-center">사유</TableHead>
                    <TableHead className="font-bold text-center">상태</TableHead>
                    <TableHead className="font-bold text-center">관리</TableHead>
                  </TableRow>
                </TableHeader>
                <TableBody>
                  {delegateList.map((d) => (
                    <TableRow key={d.approvalDelegateId} className="text-center text-[#1a2f4e]">
                      <TableCell>{d.approverName}</TableCell>
                      <TableCell>{d.delegateName}</TableCell>
                      <TableCell>{d.approvalLineInfo}</TableCell>
                      <TableCell>{d.startDate} ~ {d.endDate}</TableCell>
                      <TableCell>{d.reason}</TableCell>
                      <TableCell>
                        <span className={d.isActive === "Y" ? "text-green-600 font-semibold" : "text-gray-400"}>
                          {d.isActive === "Y" ? "활성" : "비활성"}
                        </span>
                      </TableCell>
                      <TableCell>
                        {d.isActive === "Y" && (
                          <Button
                            size="sm"
                            variant="destructive"
                            onClick={() => handleDeactivate(d.delegateId)}
                          >
                            <X size={14} />
                          </Button>
                        )}
                      </TableCell>
                    </TableRow>
                  ))}
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