import {
  EVALUATION_STATUS_LABELS,
  PERIOD_STATUS_LABELS,
} from "./evaluationConstants";

export function Card({ children }) {
  return (
    <div className="rounded-xl border border-slate-200 bg-white p-5 shadow-sm">
      {children}
    </div>
  );
}

export function CardTitle({ icon, title, description, action }) {
  return (
    <div className="flex items-start justify-between gap-4">
      <div className="flex items-start gap-2">
        {icon && <div className="mt-0.5 text-slate-500">{icon}</div>}

        <div>
          <h2 className="text-base font-bold text-slate-950">{title}</h2>

          {description && (
            <p className="mt-1 text-sm text-slate-500">{description}</p>
          )}
        </div>
      </div>

      {action}
    </div>
  );
}

export function InputField({
  label,
  name,
  value,
  onChange,
  type = "text",
  placeholder,
  className = "",
}) {
  return (
    <div className={className}>
      <label className="mb-1.5 block text-xs font-semibold text-slate-500">
        {label}
      </label>

      <input
        name={name}
        type={type}
        value={value}
        onChange={onChange}
        placeholder={placeholder}
        className="h-9 w-full rounded-md border border-slate-300 bg-white px-3 text-sm outline-none focus:border-slate-500"
      />
    </div>
  );
}

export function SelectField({ label, name, value, onChange, children }) {
  return (
    <div>
      <label className="mb-1.5 block text-xs font-semibold text-slate-500">
        {label}
      </label>

      <select
        name={name}
        value={value}
        onChange={onChange}
        className="h-9 w-full rounded-md border border-slate-300 bg-white px-3 text-sm outline-none focus:border-slate-500"
      >
        {children}
      </select>
    </div>
  );
}

export function InfoRow({ label, value }) {
  return (
    <div className="flex justify-between gap-3">
      <span className="shrink-0 text-slate-400">{label}</span>
      <span className="text-right font-semibold text-slate-700">{value}</span>
    </div>
  );
}

export function EmptyBox({ message }) {
  return (
    <div className="mt-4 flex min-h-[140px] items-center justify-center rounded-lg border border-dashed border-slate-300 bg-slate-50 px-4 py-8 text-sm text-slate-400">
      {message}
    </div>
  );
}

export function StatusBadge({ status }) {
  const label = PERIOD_STATUS_LABELS[status] ?? status;

  const className =
    status === "OPEN"
      ? "bg-blue-50 text-blue-700"
      : status === "CLOSED"
        ? "bg-slate-100 text-slate-500"
        : "bg-amber-50 text-amber-700";

  return (
    <span
      className={`rounded-full px-2 py-0.5 text-[11px] font-bold ${className}`}
    >
      {label}
    </span>
  );
}

export function EvaluationStatusBadge({ status }) {
  const label = EVALUATION_STATUS_LABELS[status] ?? status;

  const className =
    status === "CONFIRMED"
      ? "bg-blue-50 text-blue-700"
      : status === "SAVED"
        ? "bg-amber-50 text-amber-700"
        : "bg-slate-100 text-slate-500";

  return (
    <span
      className={`rounded-full px-2.5 py-1 text-xs font-bold ${className}`}
    >
      {label}
    </span>
  );
}

const GRADE_STYLE = {
  S: "bg-purple-50 text-purple-700",
  A: "bg-blue-50 text-blue-700",
  B: "bg-green-50 text-green-700",
  C: "bg-amber-50 text-amber-700",
  D: "bg-red-50 text-red-700",
};

export function GradeBadge({ grade }) {
  if (!grade) return <span className="text-slate-400">-</span>;
  return (
    <span className={`rounded-full px-2.5 py-1 text-xs font-bold ${GRADE_STYLE[grade] ?? "bg-slate-100 text-slate-500"}`}>
      {grade}
    </span>
  );
}

export function formatDate(value) {
  if (!value) {
    return "-";
  }

  return value;
}

export function formatDateTime(value) {
  if (!value) {
    return "-";
  }

  const date = new Date(value);

  if (Number.isNaN(date.getTime())) {
    return value;
  }

  return new Intl.DateTimeFormat("ko-KR", {
    year: "numeric",
    month: "2-digit",
    day: "2-digit",
  }).format(date);
}