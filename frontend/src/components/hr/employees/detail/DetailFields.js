import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";

export function ReadOnlyField({ label, value }) {
  return (
    <div>
      <Label className="mb-2 block text-xs font-medium text-slate-500">
        {label}
      </Label>

      <Input
        value={value || "-"}
        readOnly
        className="h-10 border-slate-200 bg-white text-sm text-slate-800"
      />
    </div>
  );
}

export function EditableField({ label, name, value, onChange, readOnly }) {
  return (
    <div>
      <Label className="mb-2 block text-xs font-medium text-slate-500">
        {label}
      </Label>

      <Input
        name={name}
        value={value}
        onChange={onChange}
        readOnly={readOnly}
        className={`h-10 border-slate-200 bg-white text-sm text-slate-800 ${
          readOnly ? "cursor-default" : "border-slate-300 bg-white"
        }`}
      />
    </div>
  );
}