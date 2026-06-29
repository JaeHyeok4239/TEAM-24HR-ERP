import { useState, useEffect } from "react";
import { apiRequest } from "@/lib/api";

// 프론트 라벨 매핑
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

// 타입별 input type 매핑
const INPUT_TYPE_MAP = {
  string: "text",
  number: "number",
  date: "date",
  datetime: "datetime-local",
  date_list: "date_list", // 커스텀 처리
};

export function useDocumentSchema(typeId) {
  const [fields, setFields] = useState(null); // null = 로딩중, [] = 스키마없음
  const [isLoading, setIsLoading] = useState(false);

  useEffect(() => {
    if (!typeId) return;

    const load = async () => {
      setIsLoading(true);
      try {
        const res = await apiRequest(`/api/doctype/${typeId}/schema`, {
          method: "GET",
        });
        const data = await res.json();
        console.log(data);
        const mapped = data.schema.fields.map((f) => ({
          name: f.name,
          label: LABEL_MAP[f.name] ?? f.name,
          type: INPUT_TYPE_MAP[f.type] ?? "text",
          required: f.required,
          options: Array.isArray(f.options)
            ? f.options
            : f.options
              ? [f.options]
              : [],
        }));
        setFields(mapped);
      } catch (e) {
        console.error("fail", e);
        setFields([]);
      } finally {
        setIsLoading(false);
      }
    };

    load();
  }, [typeId]);

  return { fields, isLoading };
}
