"use client";

import { useEffect, useState } from "react";
import { apiRequest } from "@/lib/api";
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogFooter,
} from "../ui/dialog";
import { Button } from "../ui/button";
import { Input } from "../ui/input";
import { Label } from "../ui/label";
import {
  Select,
  SelectTrigger,
  SelectValue,
  SelectContent,
  SelectItem,
} from "../ui/select";

export default function DocumentTypeFormModal({ open, onOpenChange, onSuccess }) {
  const [typeName, setTypeName] = useState("");
  const [detailTable, setDetailTable] = useState("");
  const [requiredProcessing, setRequiredProcessing] = useState("N");
  const [submitting, setSubmitting] = useState(false);

  useEffect(() => {
    if (!open) return;

    setTypeName("");
    setDetailTable("");
    setRequiredProcessing("N");
  }, [open]);

  const handleSubmit = async () => {
    if (!typeName.trim()) {
      alert("문서 종류 이름을 입력해주세요.");
      return;
    }

    setSubmitting(true);
    try {
      await apiRequest("/api/admin/document/type", {
        method: "POST",
        body: JSON.stringify({
          typeName: typeName.trim(),
          detailTable: detailTable.trim() || null,
          requiredProcessing,
        }),
      });
      onSuccess?.();
    } catch (err) {
      console.error(err);
      alert("문서 종류 생성에 실패했습니다.");
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <Dialog className="relative" open={open} onOpenChange={onOpenChange}>
      <DialogContent className="sm:max-w-md">
        <DialogHeader>
          <DialogTitle>문서 종류 생성</DialogTitle>
        </DialogHeader>

        <div className="flex flex-col gap-4 py-2">
          <div className="flex flex-col gap-2">
            <Label>문서 종류 이름</Label>
            <Input
              value={typeName}
              onChange={(e) => setTypeName(e.target.value)}
              placeholder="예: 연차 신청서"
            />
          </div>

          <div className="flex flex-col gap-2">
            <Label>상세 테이블 (detail_table)</Label>
            <Input
              value={detailTable}
              onChange={(e) => setDetailTable(e.target.value)}
              placeholder="예: leave (없으면 비워두세요)"
            />
          </div>

          <div className="flex flex-col gap-2">
            <Label>후처리 필요 여부</Label>
            <Select value={requiredProcessing} onValueChange={setRequiredProcessing}>
              <SelectTrigger>
                <SelectValue placeholder="선택" />
              </SelectTrigger>
              <SelectContent>
                <SelectItem value="Y">Y</SelectItem>
                <SelectItem value="N">N</SelectItem>
              </SelectContent>
            </Select>
          </div>
        </div>

        <DialogFooter>
          <Button variant="outline" onClick={() => onOpenChange(false)}>
            취소
          </Button>
          <Button
            onClick={handleSubmit}
            disabled={submitting}
            className="bg-[#1a2f4e] hover:bg-[#2a4a6e]"
          >
            생성
          </Button>
        </DialogFooter>
      </DialogContent>
    </Dialog>
  );
}