import { ClipboardCheck, Lock, PlayCircle, Users } from "lucide-react";

import { PERIOD_STATUS_LABELS } from "./evaluationConstants";
import {
  Card,
  CardTitle,
  EmptyBox,
  InfoRow,
  formatDate,
} from "./EvaluationCommon";

export default function SelectedPeriodActionCard({
  selectedPeriod,
  targetCount,
  isSaving,
  onCreateTargets,
  onOpen,
  onClose,
}) {
  if (!selectedPeriod) {
    return (
      <Card>
        <EmptyBox message="평가기간을 선택해주세요." />
      </Card>
    );
  }

  return (
    <Card>
      <CardTitle icon={<ClipboardCheck size={17} />} title="운영 작업" />

      <div className="mt-4 space-y-3 text-sm">
        <InfoRow label="평가명" value={selectedPeriod.periodName} />

        <InfoRow
          label="상태"
          value={
            PERIOD_STATUS_LABELS[selectedPeriod.status] ?? selectedPeriod.status
          }
        />

        <InfoRow label="대상자 수" value={`${targetCount}명`} />

        <InfoRow
          label="대상기간"
          value={`${formatDate(selectedPeriod.targetStartDate)} ~ ${formatDate(
            selectedPeriod.targetEndDate,
          )}`}
        />

        <InfoRow
          label="입력기간"
          value={`${formatDate(selectedPeriod.inputStartDate)} ~ ${formatDate(
            selectedPeriod.inputEndDate,
          )}`}
        />
      </div>

      <div className="mt-5 grid grid-cols-1 gap-2">
        <button
          type="button"
          onClick={onCreateTargets}
          disabled={isSaving || selectedPeriod.status === "CLOSED"}
          className="inline-flex items-center justify-center gap-2 rounded-md border border-slate-300 bg-white px-3 py-2 text-sm font-semibold text-slate-700 hover:bg-slate-50 disabled:opacity-50"
        >
          <Users size={15} />
          대상자 생성
        </button>

        <button
          type="button"
          onClick={onOpen}
          disabled={
            isSaving ||
            selectedPeriod.status === "OPEN" ||
            selectedPeriod.status === "CLOSED"
          }
          className="inline-flex items-center justify-center gap-2 rounded-md bg-slate-900 px-3 py-2 text-sm font-semibold text-white disabled:opacity-50"
        >
          <PlayCircle size={15} />
          평가 시작
        </button>

        <button
          type="button"
          onClick={onClose}
          disabled={isSaving || selectedPeriod.status !== "OPEN"}
          className="inline-flex items-center justify-center gap-2 rounded-md bg-red-600 px-3 py-2 text-sm font-semibold text-white disabled:opacity-50"
        >
          <Lock size={15} />
          평가 마감
        </button>
      </div>
    </Card>
  );
}