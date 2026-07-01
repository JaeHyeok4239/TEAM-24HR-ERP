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
import { Button } from "../ui/button";
import { Card, CardContent } from "../ui/card";
import { Plus } from "lucide-react";
import { useAuthStore } from "@/store/authStore";
import { hasAnyRole } from "@/lib/roles";
import DocumentTypeFormModal from "./DocumentTypeFormModal";

// TODO: 팀 roles 상수에 관리자 전용 역할 상수 있으면 그걸로 교체해주세요 (예: ADMIN_ROLES)
const DOCTYPE_MANAGE_ROLES = ["ADMIN"];

export default function DocumentTypeList() {
  const userInfo = useAuthStore((state) => state.userInfo);
  const isAdmin = hasAnyRole(userInfo?.roles, DOCTYPE_MANAGE_ROLES);

  const [typeList, setTypeList] = useState([]);
  const [searchTrigger, setSearchTrigger] = useState(0);
  const [formOpen, setFormOpen] = useState(false);

  useEffect(() => {
    const load = async () => {
      // GET /api/doctype는 페이지네이션 없이 전체 List를 반환합니다.
      const res = await apiRequest("/api/doctype", { method: "GET" });
      const data = await res.json();

      setTypeList(data ?? []);
    };
    load();
  }, [searchTrigger]);

  const handleCreateClick = () => {
    setFormOpen(true);
  };

  const handleFormSuccess = () => {
    setFormOpen(false);
    setSearchTrigger((prev) => prev + 1);
  };

  return (
    <div className="flex flex-col gap-4 p-5">
      <Card className="transition-transform duration-300 hover:-translate-y-1 hover:shadow-lg">
        <CardContent className="flex flex-col p-4 gap-4">
          <div className="flex justify-end">
            {isAdmin && (
              <Button
                onClick={handleCreateClick}
                className="bg-[#1a2f4e] hover:bg-[#2a4a6e] gap-1"
              >
                <Plus size={16} />
                문서 종류 생성
              </Button>
            )}
          </div>

          <div className="bg-[#ffffffbd] rounded-lg border-[#94abcaa1] overflow-hidden">
            {typeList.length === 0 ? (
              <p className="text-center text-sm text-gray-400 py-10">
                등록된 문서 종류가 없습니다
              </p>
            ) : (
              <Table>
                <TableHeader className="bg-[#94abcaa1]">
                  <TableRow className="text-[#1a2f4e]">
                    <TableHead className="font-bold text-center">ID</TableHead>
                    <TableHead className="font-bold text-center">
                      문서 종류명
                    </TableHead>
                  </TableRow>
                </TableHeader>
                <TableBody>
                  {typeList.map((type) => (
                    <TableRow
                      key={type.typeId}
                      className="text-center text-[#1a2f4e]"
                    >
                      <TableCell>{type.typeId}</TableCell>
                      <TableCell>{type.typeName}</TableCell>
                    </TableRow>
                  ))}
                </TableBody>
              </Table>
            )}
          </div>
        </CardContent>
      </Card>

      <DocumentTypeFormModal
        open={formOpen}
        onOpenChange={setFormOpen}
        onSuccess={handleFormSuccess}
      />
    </div>
  );
}