interface OptionCardProps {
  name: string;
  value: string;
  title: string;
  description: string;
  defaultChecked?: boolean;
  checked?: boolean;
  onChange?: (value: string) => void;
}

export default function OptionCard({
  name,
  value,
  title,
  description,
  defaultChecked = false,
  checked,
  onChange,
}: OptionCardProps) {
  return (
    <label className="relative flex cursor-pointer flex-col rounded-xl border-2 border-slate-100 bg-slate-50/50 p-4 transition-colors hover:border-orange-500/30 has-[:checked]:border-orange-500 has-[:checked]:bg-orange-50">
      <input
        className="hidden"
        name={name}
        value={value}
        type="radio"
        checked={checked}
        defaultChecked={checked === undefined ? defaultChecked : undefined}
        onChange={() => onChange?.(value)}
      />
      <span className="font-semibold text-slate-900">{title}</span>
      <span className="mt-1 text-xs leading-relaxed text-slate-600">
        {description}
      </span>
    </label>
  );
}
