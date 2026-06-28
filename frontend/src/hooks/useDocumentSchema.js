import { useState, useEffect } from "react";
import { apiRequest } from "@/lib/api";

// 프론트 라벨 매핑
const LABEL_MAP = {
  leaveType: "휴가 종류",
  leaveDates: "휴가 날짜",
  leaveReason: "신청 사유",
  correctionTarget: "변경할 근태 이력",
  correctionType: "정정 유형(IN/OUT)",
  beforeTime: "변경 전 시간",
  afterTime: "변경 후 시간",
};

// 타입별 input type 매핑
const INPUT_TYPE_MAP = {
  string: "text",
  number: "number",
  date: "date",
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
        }));
        setFields(mapped);

      } catch(e) {
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
