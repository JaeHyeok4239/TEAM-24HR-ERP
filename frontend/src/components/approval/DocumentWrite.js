"use client";

import { useEffect, useState } from "react";
import { useRouter } from "next/navigation";
import { apiRequest } from "@/lib/api";
import { Card, CardContent, CardHeader } from "../ui/card";
import { Button } from "../ui/button";
import { Input } from "../ui/input";
import { Label } from "../ui/label";
import { Paperclip, X, Send, Save } from "lucide-react";
import { useDocumentSchema } from "@/hooks/useDocumentSchema";

export default function DocumentWrite() {
  const router = useRouter();

  const [typeList, setTypeList] = useState([]);
  const [selectedType, setSelectedType] = useState(null);
  const [title, setTitle] = useState("");
  const [content, setContent] = useState({});
  const [files, setFiles] = useState([]);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const { fields, isLoading } = useDocumentSchema(selectedType);

  // 문서 타입 목록 로드
  useEffect(() => {
    const load = async () => {
      const res = await apiRequest("/api/doctype", { method: "GET" });
      const data = await res.json();
      setTypeList(data);
    };
    load();
  }, []);

  // 파일 추가
  const handleFileChange = (e) => {
    const selected = Array.from(e.target.files);
    setFiles((prev) => [...prev, ...selected]);
    e.target.value = "";
  };

  const removeFile = (index) => {
    setFiles((prev) => prev.filter((_, i) => i !== index));
  };

  // 스키마 필드 입력
  const handleContentChange = (key, value) => {
    setContent((prev) => ({ ...prev, [key]: value }));
  };

  // 제출
  const handleSubmit = async (status) => {
    if (!title.trim()) return alert("제목을 입력해주세요.");
    if (!selectedType) return alert("문서 타입을 선택해주세요.");

    setIsSubmitting(true);
    try {
      const formData = new FormData();

      const documentDto = {
        documentTitle: title,
        documentType: Number(selectedType),
        status,
        documentContent: content,
        attachmentIds: [],
      };

      formData.append(
        "document",
        new Blob([JSON.stringify(documentDto)], { type: "application/json" }),
      );

      files.forEach((file) => formData.append("files", file));

      const res = await apiRequest("/api/document/", {
        method: "POST",
        body: formData,
      });

      const documentId = await res.json();

      if (status === "TMP") {
        alert("임시저장 완료");
      } else {
        router.push(`/approval/document/${documentId}`);
      }
    } catch (e) {
      alert("오류가 발생했습니다.");
    } finally {
      setIsSubmitting(false);
    }
  };

  const renderField = (field) => {
    const value = content[field.name] ?? "";

    if (field.type === "date_list") {
      // 날짜 여러개 선택 - 간단하게 쉼표 구분 입력으로
      return (
        <Input
          type="text"
          placeholder="2026-01-01, 2026-01-02"
          value={Array.isArray(value) ? value.join(", ") : value}
          onChange={(e) =>
            handleContentChange(
              field.name,
              e.target.value
                .split(",")
                .map((d) => d.trim())
                .filter(Boolean),
            )
          }
        />
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

  return (
    <div className="p-6 flex flex-col gap-4 max-w-4xl mx-auto">
      {/* 문서 기본 정보 */}
      <Card>
        <CardHeader>
          <h2 className="text-lg font-semibold">문서 작성</h2>
        </CardHeader>
        <CardContent className="flex flex-col gap-4">
          {/* 문서 타입 */}
          <div className="flex flex-col gap-1">
            <Label className="text-xs text-gray-500">문서 타입</Label>
            <select
              value={selectedType ?? ""}
              onChange={(e) => {
                setSelectedType(e.target.value);
                setContent({});
              }}
              className="w-full border border-gray-200 rounded-md px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-300"
            >
              <option value="">문서 타입 선택</option>
              {typeList.map((t) => (
                <option key={t.typeId} value={t.typeId}>
                  {t.typeName}
                </option>
              ))}
            </select>
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
          {files.length === 0 && (
            <p className="text-sm text-gray-400">첨부 파일 없음</p>
          )}
        </CardContent>
      </Card>

      {/* 버튼 */}
      <div className="flex justify-end gap-2 pb-6">
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
          className="gap-1 bg-[#1a2f4e] hover:bg-[#2a4a6e]"
        >
          <Send size={16} />
          결재 요청
        </Button>
      </div>
    </div>
  );
}
