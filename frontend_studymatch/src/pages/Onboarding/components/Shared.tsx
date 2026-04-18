import { useState } from "react";

interface FieldLabelProps {
  children: React.ReactNode;
  className?: string;
}

export function FieldLabel({ children, className = "" }: FieldLabelProps) {
  return (
    <p
      className={`text-xs font-semibold text-gray-400 uppercase tracking-widest mb-2 ${className}`}
    >
      {children}
    </p>
  );
}

interface TInputProps {
  value: string | number;
  onChange: (value: string) => void;
  placeholder?: string;
  type?: string;
}

export function TInput({
  value,
  onChange,
  placeholder,
  type = "text",
}: TInputProps) {
  const [focused, setFocused] = useState<boolean>(false);
  return (
    <input
      type={type}
      value={value}
      placeholder={placeholder}
      onChange={(e) => onChange(e.target.value)}
      onFocus={() => setFocused(true)}
      onBlur={() => setFocused(false)}
      className={`w-full px-4 py-3 rounded-xl border bg-gray-50 text-sm text-gray-800 outline-none transition-all
        ${focused ? "border-blue-400 ring-2 ring-blue-100 bg-white" : "border-gray-200"}`}
    />
  );
}

interface ChipProps {
  active: boolean;
  onClick: () => void;
  children: React.ReactNode;
  activeClass?: string;
}

export function Chip({
  active,
  onClick,
  children,
  activeClass = "border-blue-500 bg-blue-50 text-blue-700",
}: ChipProps) {
  return (
    <button
      onClick={onClick}
      className={`px-4 py-2 rounded-xl border text-sm text-gray-700 font-medium transition-all
        ${active ? activeClass : "border-gray-200 bg-gray-50 text-gray-500 hover:bg-gray-100"}`}
    >
      {children}
    </button>
  );
}
