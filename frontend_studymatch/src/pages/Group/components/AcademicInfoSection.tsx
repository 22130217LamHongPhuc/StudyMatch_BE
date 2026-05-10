import { School2Icon } from "lucide-react";
import SectionCard from "./SectionCard";

interface AcademicInfoSectionProps {
  mainSubject: string;
  onMainSubjectChange: (next: string) => void;
}

export default function AcademicInfoSection({
  mainSubject,
  onMainSubjectChange,
}: AcademicInfoSectionProps) {
  return (
    <SectionCard
      icon={<School2Icon className="h-5 w-5" />}
      title="Thông tin học thuật"
    >
      <div className="grid grid-cols-1 gap-6 md:grid-cols-2">
        <SelectField
          label="Môn học chính"
          value={mainSubject}
          onChange={onMainSubjectChange}
          options={[
            "Lập trình Java",
            "Cấu trúc dữ liệu",
            "Hệ quản trị CSDL",
            "Công nghệ phần mềm",
            "Phát triển Web",
          ]}
        />
      </div>
    </SectionCard>
  );
}

interface SelectFieldProps {
  label: string;
  value: string;
  onChange: (next: string) => void;
  options: string[];
}

function SelectField({ label, value, onChange, options }: SelectFieldProps) {
  return (
    <div>
      <label className="mb-2 block text-sm font-medium text-slate-700">
        {label}
      </label>
      <select
        className="h-11 w-full rounded-xl border border-slate-200 bg-white px-3 text-sm text-slate-900 outline-none focus:border-green-300 focus:ring-2 focus:ring-green-200/40"
        value={value}
        onChange={(e) => onChange(e.target.value)}
      >
        <option value="" disabled>
          Chọn môn học...
        </option>
        {options.map((item) => (
          <option key={item}>{item}</option>
        ))}
      </select>
    </div>
  );
}
