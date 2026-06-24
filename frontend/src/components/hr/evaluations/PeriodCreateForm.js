import { InputField, SelectField } from "./EvaluationCommon";

export default function PeriodCreateForm({
  form,
  isSaving,
  onChange,
  onSubmit,
  onCancel,
}) {
  return (
    <div className="mt-4 rounded-lg border border-slate-200 bg-slate-50 p-4">
      <div className="grid grid-cols-2 gap-3">
        <InputField
          label="평가연도"
          name="evaluationYear"
          type="number"
          value={form.evaluationYear}
          onChange={onChange}
        />

        <SelectField
          label="반기"
          name="halfType"
          value={form.halfType}
          onChange={onChange}
        >
          <option value="H1">상반기</option>
          <option value="H2">하반기</option>
        </SelectField>
      </div>

      <InputField
        label="평가명"
        name="periodName"
        value={form.periodName}
        onChange={onChange}
        placeholder="미입력 시 자동 생성"
        className="mt-3"
      />

      <div className="mt-3 grid grid-cols-2 gap-3">
        <InputField
          label="대상 시작일"
          name="targetStartDate"
          type="date"
          value={form.targetStartDate}
          onChange={onChange}
        />

        <InputField
          label="대상 종료일"
          name="targetEndDate"
          type="date"
          value={form.targetEndDate}
          onChange={onChange}
        />

        <InputField
          label="입력 시작일"
          name="inputStartDate"
          type="date"
          value={form.inputStartDate}
          onChange={onChange}
        />

        <InputField
          label="입력 종료일"
          name="inputEndDate"
          type="date"
          value={form.inputEndDate}
          onChange={onChange}
        />
      </div>

      <div className="mt-4 flex justify-end gap-2">
        <button
          type="button"
          onClick={onCancel}
          disabled={isSaving}
          className="rounded-md border border-slate-300 bg-white px-3 py-1.5 text-xs font-semibold text-slate-700 disabled:opacity-50"
        >
          취소
        </button>

        <button
          type="button"
          onClick={onSubmit}
          disabled={isSaving}
          className="rounded-md bg-slate-900 px-3 py-1.5 text-xs font-semibold text-white disabled:opacity-50"
        >
          {isSaving ? "생성 중..." : "생성"}
        </button>
      </div>
    </div>
  );
}