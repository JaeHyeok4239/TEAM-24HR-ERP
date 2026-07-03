export default function DetailSection({ title, icon, action, children }) {
  return (
    <section className="rounded-lg border border-slate-200 bg-slate-50 p-5">
      <div className="mb-5 flex items-center justify-between gap-3 border-b border-slate-200 pb-4">
        <div className="flex items-center gap-2">
          {icon && (
            <span className="flex h-8 w-8 items-center justify-center rounded-md bg-white text-[#1a2f4e] shadow-sm">
              {icon}
            </span>
          )}

          <h3 className="text-sm font-bold text-slate-950">{title}</h3>
        </div>

        {action}
      </div>

      {children}
    </section>
  );
}