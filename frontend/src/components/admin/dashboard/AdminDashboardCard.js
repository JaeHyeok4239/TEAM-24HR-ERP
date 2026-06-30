export default function AdminDashboardCard({
  title,
  description,
  children,
  rightContent,
}) {
  return (
    <section className="flex h-full min-h-0 flex-col overflow-hidden rounded-xl border border-slate-200 bg-white shadow-sm">
      <div className="flex shrink-0 items-start justify-between gap-4 border-b border-slate-100 px-4 py-3">
        <div className="min-w-0">
          <h2 className="truncate text-base font-bold text-slate-950">
            {title}
          </h2>

          {description && (
            <p className="mt-1 truncate text-xs font-medium text-slate-500">
              {description}
            </p>
          )}
        </div>

        {rightContent && <div className="shrink-0">{rightContent}</div>}
      </div>

      <div className="min-h-0 flex-1 p-4">{children}</div>
    </section>
  );
}