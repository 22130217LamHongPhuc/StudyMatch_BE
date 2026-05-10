import React from "react";

interface PurposeCardProps {
  icon: React.ReactNode;
  label: string;
  description?: string;
  value: string;
  defaultChecked?: boolean;
  name?: string;
  checked?: boolean;
  onChange?: (value: string) => void;
}

export default function PurposeCard({
  icon,
  label,
  description,
  value,
  defaultChecked = false,
  name = "purpose",
  checked,
  onChange,
}: PurposeCardProps) {
  return (
    <label className="flex cursor-pointer flex-col items-center justify-center rounded-xl border-2 border-slate-100 p-3 text-center transition-colors hover:bg-slate-50 has-[:checked]:border-orange-500 has-[:checked]:bg-orange-50 has-[:checked]:text-orange-700">
      <input
        className="hidden"
        name={name}
        value={value}
        type="radio"
        checked={checked}
        defaultChecked={checked === undefined ? defaultChecked : undefined}
        onChange={() => onChange?.(value)}
      />
      {icon}
      <span className="mt-1 text-[11px] font-semibold uppercase tracking-wider">
        {label}
      </span>
      {description && (
        <p className="mt-2 text-xs leading-relaxed text-slate-600">
          {description}
        </p>
      )}
    </label>
  );
}
