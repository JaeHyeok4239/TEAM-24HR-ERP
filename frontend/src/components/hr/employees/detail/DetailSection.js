export default function DetailSection({ title, icon, action, children }) {
  return (
    <section className="rounded-lg border border-slate-200 bg-slate-50 p-5">
      <div className="mb-4 flex items-center justify-between gap-3">
        <div className="flex items-center gap-2">
          {icon && <span className="text-slate-500">{icon}</span>}
          <h3 className="text-sm font-semibold text-slate-900">{title}</h3>
        </div>

        {action}
      </div>

      {children}
    </section>
  );
}