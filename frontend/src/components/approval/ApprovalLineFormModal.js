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
import EmployeeSearch from "../common/EmployeeSearch"; // TODO: 실제 경로/props로 맞춰주세요

export default function ApprovalLineFormModal({
  open,
  onOpenChange,
  mode, // "create" | "edit"
  initialData, // edit일 때 대상 결재선 row 데이터
  onSuccess,
}) {
  const isEdit = mode === "edit";

  const [documentTypeList, setDocumentTypeList] = useState([]);
  const [departmentList, setDepartmentList] = useState([]);

  const [documentType, setDocumentType] = useState("");
  const [departmentId, setDepartmentId] = useState("");
  const [stepOrder, setStepOrder] = useState("");
  const [approver, setApprover] = useState(null); // { employeeId, name, position }
  const [employeeSearchOpen, setEmployeeSearchOpen] = useState(false);
  const [submitting, setSubmitting] = useState(false);
  const [employees, setEmployees] = useState([]);
  const [empKeyword, setEmpKeyword] = useState("");
  const [empLoading, setEmpLoading] = useState(false);

  useEffect(() => {
    if (!open) return;

    if (isEdit && initialData) {
      setStepOrder(initialData.stepOrder ?? "");
      setApprover(
        initialData.approverId
          ? {
              employeeId: initialData.approverId,
              name: initialData.approverName,
              position: initialData.approverPosition,
            }
          : null,
      );
    } else {
      setDocumentType("");
      setDepartmentId("");
      setStepOrder("");
      setApprover(null);
    }
  }, [open, isEdit, initialData]);

  useEffect(() => {
    if (!open || isEdit) return;

    const loadOptions = async () => {
      const [docTypeRes, deptRes] = await Promise.all([
        apiRequest("/api/doctype", { method: "GET" }), // TODO: 실제 경로 확인
        apiRequest("/api/hr/reference-data/departments", { method: "GET" }), // TODO: 실제 경로 확인
      ]);
      setDocumentTypeList(await docTypeRes.json());
      setDepartmentList(await deptRes.json());
    };
    loadOptions();
  }, [open, isEdit]);

  const fetchEmployees = async (keyword = "") => {
    setEmpLoading(true);
    try {
      const res = await apiRequest(`/api/hr/employees/simple?keyword=${keyword}`);
      const data = await res.json();

      setEmployees(Array.isArray(data) ? data : (data.content ?? []));
    } catch (err) {
      console.error(err);
    } finally {
      setEmpLoading(false);
    }
  };

  const handleSubmit = async () => {
    if (!approver) {
      alert("결재자를 선택해주세요.");
      return;
    }

    setSubmitting(true);
    try {
      if (isEdit) {
        await apiRequest(`/api/approval/management/line/${initialData.approvalLineId}`, {
          method: "PATCH",
          body: JSON.stringify({ approverId: approver.employeeId }),
        });
      } else {
        if (!documentType || !stepOrder) {
          alert("문서 유형과 단계를 입력해주세요.");
          setSubmitting(false);
          return;
        }
        await apiRequest("/api/approval/management/line", {
          method: "POST",
          body: JSON.stringify({
            documentType,
            departmentId: departmentId || null,
            stepOrder: Number(stepOrder),
            defaultApprover: approver.employeeId,
          }),
        });
      }
      onSuccess?.();
    } catch (err) {
      console.error(err);
      alert(
        isEdit ? "결재선 수정에 실패했습니다." : "결재선 생성에 실패했습니다.",
      );
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <>
      <Dialog className="relative" open={open} onOpenChange={onOpenChange}>
        <DialogContent className="sm:max-w-md">
          <DialogHeader>
            <DialogTitle>{isEdit ? "결재선 수정" : "결재선 생성"}</DialogTitle>
          </DialogHeader>

          <div className="flex flex-col gap-4 py-2">
            <div className="flex flex-col gap-2">
              <Label>문서 유형</Label>
              {isEdit ? (
                <Input value={initialData?.documentTypeName ?? ""} disabled />
              ) : (
                <Select value={documentType} onValueChange={setDocumentType}>
                  <SelectTrigger>
                    <SelectValue placeholder="문서 유형 선택" />
                  </SelectTrigger>
                  <SelectContent>
                    {documentTypeList.map((dt) => (
                      <SelectItem key={dt.typeId} value={dt.typeId}>
                        {dt.typeName}
                      </SelectItem>
                    ))}
                  </SelectContent>
                </Select>
              )}
            </div>

            <div className="flex flex-col gap-2">
              <Label>부서 (선택, 미선택 시 공통)</Label>
              {isEdit ? (
                <Input value={initialData?.departmentName ?? "공통"} disabled />
              ) : (
                <Select value={departmentId} onValueChange={setDepartmentId}>
                  <SelectTrigger>
                    <SelectValue placeholder="부서 선택 (선택 안 하면 공통)" />
                  </SelectTrigger>
                  <SelectContent>
                    {departmentList.map((d) => (
                      <SelectItem key={d.departmentId} value={d.departmentId}>
                        {d.departmentName}
                      </SelectItem>
                    ))}
                  </SelectContent>
                </Select>
              )}
            </div>

            <div className="flex flex-col gap-2">
              <Label>단계</Label>
              <Input
                type="number"
                min={1}
                value={stepOrder}
                onChange={(e) => setStepOrder(e.target.value)}
                disabled={isEdit}
                placeholder="예: 1"
              />
            </div>

            <div className="flex flex-col gap-2">
              <Label>결재자</Label>
              <div className="flex gap-2 items-center">
                <Input
                  value={
                    approver
                      ? `${approver.name} (${approver.positionName ?? ""})`
                      : ""
                  }
                  placeholder="결재자를 선택해주세요"
                  disabled
                  className="flex-1"
                />
                <Button
                  type="button"
                  variant="outline"
                  onClick={() => setEmployeeSearchOpen(true)}
                >
                  검색
                </Button>
              </div>
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
              {isEdit ? "수정" : "생성"}
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>

      {employeeSearchOpen && (
        <div
        className="absolute inset-0 z-[999] flex items-center justify-center bg-black/40"
          onClick={() => setEmployeeSearchOpen(false)}
        >
          <div
            className="w-full max-w-sm h-[60vh] bg-white rounded-xl shadow-lg overflow-hidden"
            onClick={(e) => e.stopPropagation()}
          >
            <EmployeeSearch
              employees={employees ?? []}
              selectedEmployeeId={approver?.employeeId}
              keyword={empKeyword}
              isLoading={empLoading}
              onKeywordChange={setEmpKeyword}
              onKeywordKeyDown={(e) => {
                if (e.key === "Enter") fetchEmployees(empKeyword);
              }}
              onSearch={() => fetchEmployees(empKeyword)}
              selectEmployee={(employeeId) => {
                const selected = (employees ?? []).find(
                  (e) => e.employeeId === employeeId,
                );

                if (!selected) return;

                setApprover(selected);
                setEmployeeSearchOpen(false);
              }}
            />
          </div>
        </div>
      )}
    </>
  );
}
