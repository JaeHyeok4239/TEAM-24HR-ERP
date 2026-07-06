"use client";

import { useState } from "react";
import { apiRequest } from "@/lib/api";
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogFooter,
} from "../ui/dialog";
import { Input } from "../ui/input";
import { Label } from "../ui/label";
import { Button } from "../ui/button";
import { Checkbox } from "../ui/checkbox";
import {
  Select,
  SelectTrigger,
  SelectValue,
  SelectContent,
  SelectItem,
} from "../ui/select";
import { Plus, Trash2, ArrowUp, ArrowDown } from "lucide-react";

// 필드명(name)을 알면 라벨 자동완성 (프론트 표시 전용, 서버에는 안 보냄)
const LABEL_MAP = {
  leaveTypeName: "휴가 종류",
  leaveDates: "휴가 날짜",
  leaveReason: "신청 사유",
  targetDate: "정정 대상 날짜",
  correctionReason: "정정 이유",
  correctionType: "정정 유형(IN/OUT)",
  beforeTime: "변경 전 시간",
  afterTime: "변경 후 시간",
};

// 백엔드 schema.fields[].type 값과 동일하게 맞춤
const FIELD_TYPES = [
  { value: "string", label: "텍스트" },
  { value: "number", label: "숫자" },
  { value: "date", label: "날짜" },
  { value: "datetime", label: "날짜+시간" },
  { value: "date_list", label: "날짜 여러 개 선택" },
];

const emptyField = () => ({
  id: crypto.randomUUID(),
  name: "",
  label: "",
  type: "string",
  required: false,
  options: "", // 콤마로 구분, 선택지가 필요한 경우만 입력
});

export default function DocumentTypeSchemaFormModal({
  open,
  onOpenChange,
  typeId,
  onSuccess,
}) {
  const [fields, setFields] = useState([emptyField()]);
  const [submitting, setSubmitting] = useState(false);
  const [loading, setLoading] = useState(false);
  const [hasExistingSchema, setHasExistingSchema] = useState(false);

  // open이 바뀌는 시점을 렌더링 중에 감지해서 스키마를 새로 조회
 
  const [prevOpen, setPrevOpen] = useState(open);
 
  const loadSchema = async () => {
    setLoading(true);
    try {
      const res = await apiRequest(`/api/doctype/${typeId}/schema`, {
        method: "GET",
      });

      if (!res.ok) {
        setHasExistingSchema(false);
        setFields([emptyField()]);
        return;
      }

      const data = await res.json();

      if (!data?.schema?.fields?.length) {
        setHasExistingSchema(false);
        setFields([emptyField()]);
        return;
      }

      setHasExistingSchema(true);
      setFields(
        data.schema.fields.map((f) => ({
          id: crypto.randomUUID(),
          name: f.name ?? "",
          label: LABEL_MAP[f.name] ?? f.name ?? "",
          type: f.type ?? "string",
          required: !!f.required,
          options: Array.isArray(f.options)
            ? f.options.join(", ")
            : f.options
              ? String(f.options)
              : "",
        }))
      );
    } catch (e) {
      console.error(e);
      setHasExistingSchema(false);
      setFields([emptyField()]);
    } finally {
      setLoading(false);
    }
  };
 
  if (open !== prevOpen) {
    setPrevOpen(open);
    if (open && typeId) {
      loadSchema();
    }
  }


  const updateField = (id, patch) => {
    setFields((prev) =>
      prev.map((f) => {
        if (f.id !== id) return f;
        const next = { ...f, ...patch };
        if (patch.name !== undefined && LABEL_MAP[patch.name] && !f.label) {
          next.label = LABEL_MAP[patch.name];
        }
        return next;
      })
    );
  };

  const addField = () => setFields((prev) => [...prev, emptyField()]);

  const removeField = (id) =>
    setFields((prev) => prev.filter((f) => f.id !== id));

  const moveField = (index, direction) => {
    setFields((prev) => {
      const next = [...prev];
      const target = index + direction;
      if (target < 0 || target >= next.length) return prev;
      [next[index], next[target]] = [next[target], next[index]];
      return next;
    });
  };

  const validate = () => {
    if (fields.length === 0) {
      alert("필드를 1개 이상 추가해주세요.");
      return false;
    }
    for (const f of fields) {
      if (!f.name.trim() || !f.label.trim()) {
        alert("모든 필드의 필드명(name)과 라벨을 입력해주세요.");
        return false;
      }
    }
    const names = fields.map((f) => f.name.trim());
    if (new Set(names).size !== names.length) {
      alert("필드명(name)이 중복되었습니다.");
      return false;
    }
    return true;
  };

  const buildFieldsPayload = () =>
    fields.map(({ name, type, required, options }) => ({
      name: name.trim(),
      type,
      required,
      options: options.trim()
        ? options.split(",").map((o) => o.trim()).filter(Boolean)
        : [],
    }));

  const handleSubmit = async () => {
    if (!validate()) return;

    setSubmitting(true);
    try {
      await apiRequest(`/api/admin/document/schema/type/${typeId}`, {
        method: "PUT",
        body: JSON.stringify({ fields: buildFieldsPayload() }),
      });

      onSuccess?.();
    } catch (e) {
      console.error(e);
      alert("스키마 저장 중 오류가 발생했습니다.");
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent className="max-w-3xl max-h-[85vh] overflow-y-auto">
        <DialogHeader>
          <DialogTitle className="text-[#1a2f4e]">
            {hasExistingSchema ? "문서 양식 수정" : "문서 양식 생성"}
          </DialogTitle>
        </DialogHeader>

        {loading ? (
          <p className="text-center text-sm text-gray-400 py-10">
            불러오는 중...
          </p>
        ) : (
          <>
            <div className="flex flex-col gap-3">
              {fields.map((f, idx) => (
                <div
                  key={f.id}
                  className="grid grid-cols-[1fr_1fr_1fr_auto_auto_auto] gap-2 items-start border border-[#94abcaa1] rounded-md p-3"
                >
                  <div className="flex flex-col gap-1">
                    <Label className="text-xs text-gray-500">
                      필드명(name)
                    </Label>
                    <Input
                      value={f.name}
                      onChange={(e) =>
                        updateField(f.id, { name: e.target.value })
                      }
                      placeholder="예: leaveReason"
                    />
                  </div>

                  <div className="flex flex-col gap-1">
                    <Label className="text-xs text-gray-500">라벨</Label>
                    <Input
                      value={f.label}
                      onChange={(e) =>
                        updateField(f.id, { label: e.target.value })
                      }
                      placeholder="예: 신청 사유"
                    />
                  </div>

                  <div className="flex flex-col gap-1">
                    <Label className="text-xs text-gray-500">타입</Label>
                    <Select
                      value={f.type}
                      onValueChange={(v) => updateField(f.id, { type: v })}
                    >
                      <SelectTrigger>
                        <SelectValue />
                      </SelectTrigger>
                      <SelectContent>
                        {FIELD_TYPES.map((t) => (
                          <SelectItem key={t.value} value={t.value}>
                            {t.label}
                          </SelectItem>
                        ))}
                      </SelectContent>
                    </Select>
                  </div>

                  <div className="flex flex-col items-center gap-1">
                    <Label className="text-xs text-gray-500">필수</Label>
                    <Checkbox
                      checked={f.required}
                      onCheckedChange={(v) =>
                        updateField(f.id, { required: !!v })
                      }
                    />
                  </div>

                  <div className="flex flex-col gap-1">
                    <Button
                      variant="ghost"
                      size="icon"
                      disabled={idx === 0}
                      onClick={() => moveField(idx, -1)}
                    >
                      <ArrowUp size={14} />
                    </Button>
                    <Button
                      variant="ghost"
                      size="icon"
                      disabled={idx === fields.length - 1}
                      onClick={() => moveField(idx, 1)}
                    >
                      <ArrowDown size={14} />
                    </Button>
                  </div>

                  <Button
                    variant="ghost"
                    size="icon"
                    onClick={() => removeField(f.id)}
                    className="text-red-500"
                  >
                    <Trash2 size={16} />
                  </Button>

                  <div className="col-span-6 flex flex-col gap-1">
                    <Label className="text-xs text-gray-500">
                      선택지 (콤마로 구분, 필요한 경우만 입력)
                    </Label>
                    <Input
                      value={f.options}
                      onChange={(e) =>
                        updateField(f.id, { options: e.target.value })
                      }
                      placeholder="예: 연차, 병가, 경조사"
                    />
                  </div>
                </div>
              ))}

              <Button
                variant="outline"
                onClick={addField}
                className="gap-1 self-start"
              >
                <Plus size={16} /> 필드 추가
              </Button>
            </div>

            <DialogFooter className="mt-4">
              <Button variant="outline" onClick={() => onOpenChange(false)}>
                취소
              </Button>
              <Button
                onClick={handleSubmit}
                disabled={submitting}
                className="bg-[#1a2f4e] hover:bg-[#2a4a6e]"
              >
                {submitting ? "저장 중..." : "저장"}
              </Button>
            </DialogFooter>
          </>
        )}
      </DialogContent>
    </Dialog>
  );
}