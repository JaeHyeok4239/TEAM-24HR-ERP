import { LayoutDashboard } from "lucide-react";

export default function EmptyDashboardSlot({ title }) {
  return (
    <section className="flex h-full min-h-0 items-center justify-center rounded-xl border border-dashed border-slate-300 bg-white p-4">
      <div className="text-center">
        <div className="mx-auto flex h-10 w-10 items-center justify-center rounded-full bg-slate-100 text-slate-400">
          <LayoutDashboard size={18} />
        </div>

        <p className="mt-3 text-sm font-semibold text-slate-500">{title}</p>

        <p className="mt-1 text-xs text-slate-400">
          추후 대시보드 카드 추가 예정
        </p>
      </div>
    </section>
  );
}