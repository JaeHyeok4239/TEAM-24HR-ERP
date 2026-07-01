"use client";

import { useCallback, useEffect, useState } from "react";
import { useRouter } from "next/navigation";
import { apiRequest } from "@/lib/api";
import { Card, CardContent, CardHeader } from "../ui/card";
import { Button } from "../ui/button";
import { Input } from "../ui/input";
import { Label } from "../ui/label";
import { Paperclip, X, Send, Save, CalendarIcon, FileText } from "lucide-react";
import { useDocumentSchema } from "@/hooks/useDocumentSchema";
import {
  Select,
  SelectContent,
  SelectGroup,
  SelectItem,
  SelectValue,
  SelectTrigger,
} from "../ui/select";
import { Calendar } from "../ui/calendar";
import { Popover, PopoverContent, PopoverTrigger } from "../ui/popover";
import { format } from "date-fns";
import { useAuthStore } from "@/store/authStore";
import Swal from "sweetalert2";
import { toast } from "sonner";

export default function DocumentWrite({ documentId }) {
  const router = useRouter();
  const isEditMode = !!documentId;

  const [typeList, setTypeList] = useState([]);
  const [selectedType, setSelectedType] = useState(null);
  const [title, setTitle] = useState("");
  const [content, setContent] = useState({});
  const [files, setFiles] = useState([]); // 신규 첨부 파일
  const [existingFiles, setExistingFiles] = useState([]); // 기존 첨부 파일 (서버에 이미 있는 것)
  const [removedAttachmentIds, setRemovedAttachmentIds] = useState([]); // 삭제할 기존 파일 id
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [isLoadingDocument, setIsLoadingDocument] = useState(isEditMode);
  const { fields, isLoading } = useDocumentSchema(selectedType);
  const [label, setLabel] = useState(null);
  const [document, setDocument] = useState(null);
  const userInfo = useAuthStore((state) => state.userInfo);
  const currentId = userInfo?.employeeId;
  const isOwner = document?.requesterId === currentId;

  // 문서 타입 목록 로드
  useEffect(() => {
    const load = async () => {
      const res = await apiRequest("/api/doctype", { method: "GET" });
      const data = await res.json();
      setTypeList(data);
    };
    load();
  }, []);

  // 수정 모드일 때 기존 문서 데이터 로드
  useEffect(() => {
    if (!isEditMode) return;

    const loadDocument = async () => {
      try {
        const res = await apiRequest(`/api/document/${documentId}`, {
          method: "GET",
        });
        const data = await res.json();
        console.log(data);
        setDocument(data);
        setTitle(data.documentTitle);
        setSelectedType(data.documentType);
        setLabel(data.documentTypeName);
        setContent(data.documentContent ?? {});
        setExistingFiles(data.documentFileList ?? []);
      } catch (e) {
        alert("문서를 불러올 수 없습니다.");
        router.back();
      } finally {
        setIsLoadingDocument(false);
      }
    };
    loadDocument();
  }, [documentId, isEditMode, router]);

  // 신규 파일 추가
  const handleFileChange = (e) => {
    const selected = Array.from(e.target.files);
    setFiles((prev) => [...prev, ...selected]);
    e.target.value = "";
  };

  const removeFile = (index) => {
    setFiles((prev) => prev.filter((_, i) => i !== index));
  };

  // 기존 첨부 파일 삭제 (체크 표시 후 제출 시 일괄 반영)
  const removeExistingFile = (attachmentId) => {
    setExistingFiles((prev) =>
      prev.filter((f) => f.attachmentId !== attachmentId),
    );
    setRemovedAttachmentIds((prev) => [...prev, attachmentId]);
  };

  // 스키마 필드 입력
  const handleContentChange = (key, value) => {
    setContent((prev) => ({ ...prev, [key]: value }));
  };

  // 삭제
  const handleDelete = useCallback(async (id) => {
    const result = await Swal.fire({
      title: "문서 삭제",
      text: "정말 문서를 삭제하시겠습니까?",
      icon: "warning",
      showCancelButton: true,
      confirmButtonColor: "#dc3545",
      cancelButtonColor: "#6c757d",
      confirmButtonText: "삭제",
      cancelButtonText: "취소",
    });

    if (result.isConfirmed) {
      const documentId = id;

      try {
        await apiRequest(`/api/document/${documentId}`, {
          method: "DELETE",
        });

        toast.success("문서 삭제 성공");
        router.push("/approval/document");
      } catch {
        toast.error("문서 삭제 실패");
        router.push(`/approval/document/${documentId}`);
        throw new Error("문서를 삭제하지 못했습니다");
      }
    }
  }, []);

  // 제출 (생성 또는 수정)
  const handleSubmit = async (status) => {
    if (!title.trim()) return toast.error("제목을 입력해주세요.");
    if (!selectedType) return toast.error("문서 타입을 선택해주세요.");

    setIsSubmitting(true);
    try {
      const formData = new FormData();

      const documentDto = {
        documentTitle: title,
        documentType: Number(selectedType),
        status,
        documentContent: content,
        attachmentIds: removedAttachmentIds, // 수정 시 삭제할 기존 첨부파일 id
      };

      formData.append(
        "document",
        new Blob([JSON.stringify(documentDto)], { type: "application/json" }),
      );

      files.forEach((file) => formData.append("files", file));

      if (isEditMode) {
        await apiRequest(`/api/document/${documentId}`, {
          method: "PUT",
          body: formData,
        });
        toast.success("수정 완료");
        router.push(`/approval/document/${documentId}`);
      } else {
        const res = await apiRequest("/api/document/", {
          method: "POST",
          body: formData,
        });
        toast.success("결재 요청 완료");
        const newDocumentId = await res.json();

        if (status === "TMP") {
          toast.success("임시저장 완료");
          router.push(`/approval/document/${newDocumentId}`);
        } else {
          router.push(`/approval/document/${newDocumentId}`);
        }
      }
    } catch (e) {
      toast.error("오류가 발생했습니다.");
      console.error(e);
    } finally {
      setIsSubmitting(false);
    }
  };

  const renderField = (field) => {
    const value = content[field.name] ?? "";

    if (field.type === "date_list") {
      const dates = Array.isArray(value) ? value.map((d) => new Date(d)) : [];

      return (
        <Popover>
          <PopoverTrigger className="w-full flex items-center gap-2 border border-input bg-input/50 rounded-2xl px-3 py-2 text-sm cursor-pointer hover:bg-accent transition-colors">
            <CalendarIcon size={16} className="text-muted-foreground" />
            <span className={dates.length > 0 ? "" : "text-muted-foreground"}>
              {dates.length > 0
                ? dates.map((d) => format(d, "yyyy-MM-dd")).join(", ")
                : "날짜 선택"}
            </span>
          </PopoverTrigger>
          <PopoverContent className="w-auto p-0">
            <Calendar
              mode="multiple"
              selected={dates}
              disabled={(date) => {
                const isWeekend = date.getDay() === 0 || date.getDay() === 6;

                const today = new Date();
                today.setHours(0, 0, 0, 0);
                const isPast = date < today;

                return isWeekend || isPast;
              }}
              onSelect={(selected) =>
                handleContentChange(
                  field.name,
                  (selected ?? []).map((d) => format(d, "yyyy-MM-dd")),
                )
              }
            />
          </PopoverContent>
        </Popover>
      );
    }

    if (field.type === "datetime-local") {
      return (
        <Input
          type="datetime-local"
          value={value ? value.replace(" ", "T").slice(0, 16) : ""}
          onChange={(e) =>
            handleContentChange(field.name, e.target.value.replace("T", " "))
          }
        />
      );
    }

    if (field.options?.length > 0) {
      return (
        <select
          value={value}
          onChange={(e) => handleContentChange(field.name, e.target.value)}
          className="w-full border border-input bg-input/50 rounded-2xl px-3 py-2 text-sm outline-none focus-visible:border-ring transition-[color,box-shadow] duration-200"
        >
          <option value="">선택</option>
          {field.options.map((opt) => (
            <option key={opt} value={opt}>
              {opt}
            </option>
          ))}
        </select>
      );
    }

    return (
      <Input
        type={field.type}
        placeholder={field.label}
        value={value}
        onChange={(e) =>
          handleContentChange(
            field.name,
            field.type === "number" ? Number(e.target.value) : e.target.value,
          )
        }
      />
    );
  };

  if (isLoadingDocument) {
    return (
      <div className="p-6 flex justify-center">
        <p className="text-sm text-gray-400">문서를 불러오는 중...</p>
      </div>
    );
  }

  return (
    <div className="p-6 flex flex-col gap-4 min-w-4xl mx-auto">
      {/* 문서 기본 정보 */}
      <Card>
        <CardHeader>
          <h2 className="text-lg font-semibold">
            {isEditMode ? "문서 수정" : "문서 작성"}
          </h2>
        </CardHeader>
        <CardContent className="flex flex-col gap-4">
          {/* 문서 타입 (재기안 시 변경 불가) */}
          <div className="flex flex-col gap-1">
            <Label className="text-xs text-gray-500">문서 타입</Label>
            <Select
              value={selectedType ?? ""}
              disabled={isEditMode && document?.rejectReason}
              onValueChange={(val) => {
                setSelectedType(val);
                setContent({});
              }}
            >
              <SelectTrigger className="w-full">
                <SelectValue
                  placeholder="문서 타입 선택"
                  label={label}
                ></SelectValue>
              </SelectTrigger>
              <SelectContent>
                <SelectGroup>
                  {typeList.map((t) => (
                    <SelectItem
                      key={t.typeName}
                      label={t.typeName}
                      value={t.typeId}
                      onClick={() => {
                        setLabel(t.typeName);
                      }}
                    >
                      {t.typeName}
                    </SelectItem>
                  ))}
                </SelectGroup>
              </SelectContent>
            </Select>
            {isEditMode && document?.rejectReason && (
              <p className="text-xs text-gray-400">
                재기안 시, 문서 유형은 수정할 수 없습니다
              </p>
            )}
          </div>

          {/* 제목 */}
          <div className="flex flex-col gap-1">
            <Label className="text-xs text-gray-500">제목</Label>
            <Input
              placeholder="문서 제목을 입력하세요"
              value={title}
              onChange={(e) => setTitle(e.target.value)}
            />
          </div>
        </CardContent>
      </Card>

      {/* 스키마 필드 */}
      {selectedType && (
        <Card>
          <CardHeader>
            <h2 className="text-lg font-semibold">문서 내용</h2>
          </CardHeader>
          <CardContent className="grid grid-cols-1 md:grid-cols-2 gap-4">
            {isLoading ? (
              <p className="col-span-2 text-sm text-gray-400">로딩 중...</p>
            ) : fields && fields.length > 0 ? (
              fields.map((field) => (
                <div key={field.name} className="flex flex-col gap-1">
                  <Label className="text-xs text-gray-500">
                    {field.label}
                    {field.required && (
                      <span className="text-red-400 ml-0.5">*</span>
                    )}
                  </Label>
                  {renderField(field)}
                </div>
              ))
            ) : (
              <textarea
                value={content?.content ?? ""}
                onChange={(e) => setContent({ content: e.target.value })}
                placeholder="문서 내용을 입력하세요"
                className="col-span-2 w-full min-h-[200px] border border-gray-200 rounded-md px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-300 resize-y"
              />
            )}
          </CardContent>
        </Card>
      )}

      {/* 첨부 파일 */}
      <Card>
        <CardHeader>
          <h2 className="text-lg font-semibold">첨부 파일</h2>
        </CardHeader>
        <CardContent className="flex flex-col gap-3">
          <label className="flex items-center gap-2 w-fit cursor-pointer text-sm text-blue-500 hover:text-blue-700">
            <Paperclip size={16} />
            파일 추가
            <input
              type="file"
              multiple
              className="hidden"
              onChange={handleFileChange}
            />
          </label>

          {/* 기존 첨부 파일 (수정 모드) */}
          {existingFiles.length > 0 && (
            <ul className="flex flex-col gap-2">
              {existingFiles.map((file) => (
                <li
                  key={file.attachmentId}
                  className="flex items-center justify-between text-sm px-3 py-2 bg-blue-50 rounded-lg"
                >
                  <span className="flex items-center gap-2 text-gray-700">
                    <FileText size={14} className="text-blue-400" />
                    {file.fileName}
                  </span>
                  <button onClick={() => removeExistingFile(file.attachmentId)}>
                    <X size={14} className="text-gray-400 hover:text-red-400" />
                  </button>
                </li>
              ))}
            </ul>
          )}

          {/* 신규 첨부 파일 */}
          {files.length > 0 && (
            <ul className="flex flex-col gap-2">
              {files.map((file, i) => (
                <li
                  key={i}
                  className="flex items-center justify-between text-sm px-3 py-2 bg-gray-50 rounded-lg"
                >
                  <span className="text-gray-700">{file.name}</span>
                  <button onClick={() => removeFile(i)}>
                    <X size={14} className="text-gray-400 hover:text-red-400" />
                  </button>
                </li>
              ))}
            </ul>
          )}

          {files.length === 0 && existingFiles.length === 0 && (
            <p className="text-sm text-gray-400">첨부 파일 없음</p>
          )}
        </CardContent>
      </Card>

      {/* 버튼 */}
      <div className="flex justify-between gap-2 pb-6">
        <div className="flex gap-2">
          <Button variant="outline" onClick={() => router.back()}>
            취소
          </Button>
          {document?.documentStatus === "TMP" && !document?.rejectReason && (
            <Button
              variant="destructive"
              onClick={() => handleDelete(documentId)}
            >
              삭제
            </Button>
          )}
        </div>

        <div>
          <Button
            variant="outline"
            onClick={() => handleSubmit("TMP")}
            disabled={isSubmitting}
            className="gap-1"
          >
            <Save size={16} />
            임시저장
          </Button>
          <Button
            onClick={() => handleSubmit("REQ")}
            disabled={isSubmitting}
            className="ml-2 gap-1 bg-[#1a2f4e] hover:bg-[#2a4a6e]"
          >
            <Send size={16} />
            {isEditMode &&
            document?.rejectReason &&
            document?.documentStatus === "REJ"
              ? "재기안 요청"
              : "결재 요청"}
          </Button>
        </div>
      </div>
    </div>
  );
}
