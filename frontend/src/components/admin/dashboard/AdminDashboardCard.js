export default function AdminDashboardCard({
  title,
  description,
  children,
  rightContent,
}) {
  return (
    <section className="flex h-full min-h-0 flex-col rounded-xl border border-slate-200 bg-white p-4 shadow-sm">
      <div className="mb-4 flex items-start justify-between gap-4 border-b border-slate-100 pb-3">
        <div>
          <h2 className="text-base font-bold text-slate-950">{title}</h2>
          {description && (
            <p className="mt-1 text-sm text-slate-500">{description}</p>
          )}
        </div>

        {rightContent}
      </div>

      <div className="min-h-0 flex-1">{children}</div>
    </section>
  );
}