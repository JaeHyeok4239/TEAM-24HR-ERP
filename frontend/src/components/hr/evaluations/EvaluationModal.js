import { CheckCircle2, X } from "lucide-react";

import { EVALUATION_STATUS_LABELS } from "./evaluationConstants";

export default function EvaluationModal({
  detail,
  form,
  totalScore,
  isSaving,
  isPeriodClosed,
  isPeriodOpen,
  onClose,
  onAnswerChange,
  onCommentChange,
  onSave,
  onConfirm,
}) {
  const isConfirmed = detail.status === "CONFIRMED";
  const editable = isPeriodOpen && !isConfirmed && !isPeriodClosed;

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-slate-950/40 px-6">
      <div className="flex max-h-[90vh] w-full max-w-4xl flex-col overflow-hidden rounded-xl bg-white shadow-xl">
        <div className="flex items-start justify-between gap-4 border-b px-6 py-5">
          <div>
            <h2 className="text-lg font-bold text-slate-950">
              {detail.employeeName} 평가 입력
            </h2>

            <p className="mt-1 text-sm text-slate-500">
              {detail.periodName} · {detail.departmentName ?? "-"} ·{" "}
              {detail.positionName ?? "-"}
            </p>
          </div>

          <button
            type="button"
            onClick={onClose}
            className="rounded-md p-1 text-slate-400 hover:bg-slate-100 hover:text-slate-700"
          >
            <X size={20} />
          </button>
        </div>

        <div className="min-h-0 flex-1 overflow-y-auto px-6 py-5">
          <div className="mb-5 flex items-center justify-between rounded-lg bg-slate-50 px-4 py-3">
            <div>
              <p className="text-xs font-semibold text-slate-500">평가상태</p>
              <p className="mt-1 text-sm font-bold text-slate-900">
                {EVALUATION_STATUS_LABELS[detail.status] ?? detail.status}
              </p>
            </div>

            <div className="text-right">
              <p className="text-xs font-semibold text-slate-500">총점</p>
              <p className="mt-1 text-xl font-bold text-slate-950">
                {totalScore} / 50
              </p>
            </div>
          </div>

          <div className="space-y-4">
            {(detail.answers ?? []).map((question, index) => {
              const answer = form.answers.find(
                (item) =>
                  item.evaluationQuestionId === question.evaluationQuestionId,
              );

              return (
                <div
                  key={question.evaluationQuestionId}
                  className="rounded-lg border border-slate-200 px-4 py-4"
                >
                  <div className="flex items-start justify-between gap-4">
                    <div>
                      <p className="text-sm font-semibold text-slate-900">
                        {index + 1}. {question.questionContent}
                      </p>

                      <p className="mt-1 text-xs text-slate-400">
                        1점부터 {question.maxScore}점까지 선택
                      </p>
                    </div>

                    <div className="flex shrink-0 gap-1">
                      {[1, 2, 3, 4, 5].map((score) => {
                        const selected = Number(answer?.score) === score;

                        return (
                          <button
                            key={score}
                            type="button"
                            onClick={() => {
                              if (editable) {
                                onAnswerChange(
                                  question.evaluationQuestionId,
                                  score,
                                );
                              }
                            }}
                            disabled={!editable}
                            className={`flex h-9 w-9 items-center justify-center rounded-md border text-sm font-bold transition disabled:cursor-not-allowed ${
                              selected
                                ? "border-slate-900 bg-slate-900 text-white"
                                : "border-slate-300 bg-white text-slate-500 hover:bg-slate-50"
                            }`}
                          >
                            {score}
                          </button>
                        );
                      })}
                    </div>
                  </div>
                </div>
              );
            })}
          </div>

          <div className="mt-5">
            <label className="mb-2 block text-sm font-semibold text-slate-700">
              종합 의견
            </label>

            <textarea
              value={form.comment}
              onChange={onCommentChange}
              disabled={!editable}
              rows={4}
              placeholder="평가 의견을 입력해주세요."
              className="w-full resize-none rounded-lg border border-slate-300 px-3 py-2 text-sm outline-none focus:border-slate-500 disabled:bg-slate-50"
            />
          </div>

          {!editable && (
            <div className="mt-4 rounded-md bg-amber-50 px-4 py-3 text-sm text-amber-700">
              확정되었거나 마감된 평가는 수정할 수 없습니다.
            </div>
          )}
        </div>

        <div className="flex justify-end gap-2 border-t px-6 py-4">
          <button
            type="button"
            onClick={onClose}
            disabled={isSaving}
            className="rounded-md border border-slate-300 bg-white px-4 py-2 text-sm font-semibold text-slate-700 disabled:opacity-50"
          >
            닫기
          </button>

          {editable && (
            <>
              <button
                type="button"
                onClick={onSave}
                disabled={isSaving}
                className="rounded-md border border-slate-300 bg-white px-4 py-2 text-sm font-semibold text-slate-700 disabled:opacity-50"
              >
                임시저장
              </button>

              <button
                type="button"
                onClick={onConfirm}
                disabled={isSaving}
                className="inline-flex items-center gap-1 rounded-md bg-slate-900 px-4 py-2 text-sm font-semibold text-white disabled:opacity-50"
              >
                <CheckCircle2 size={16} />
                확정
              </button>
            </>
          )}
        </div>
      </div>
    </div>
  );
}
