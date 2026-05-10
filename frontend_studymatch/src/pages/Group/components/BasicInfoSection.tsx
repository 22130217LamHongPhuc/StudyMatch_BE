import { InfoIcon } from "lucide-react";
import SectionCard from "./SectionCard";

const fieldClassName =
  "h-11 w-full rounded-xl border border-slate-200 bg-white px-4 text-sm text-slate-900 placeholder:text-slate-400 outline-none focus:border-green-300 focus:ring-2 focus:ring-green-200/40";

const textAreaClassName =
  "min-h-32 w-full resize-y rounded-xl border border-slate-200 bg-white px-4 py-3 text-sm text-slate-900 placeholder:text-slate-400 outline-none focus:border-green-300 focus:ring-2 focus:ring-green-200/40";

interface BasicInfoSectionProps {
  groupName: string;
  goalDescription: string;
  onChange: (next: { groupName: string; goalDescription: string }) => void;
}

export default function BasicInfoSection({
  groupName,
  goalDescription,
  onChange,
}: BasicInfoSectionProps) {
  return (
    <SectionCard
      icon={<InfoIcon className="h-5 w-5" />}
      title="Thông tin cơ bản"
    >
      <div className="space-y-6">
        <div>
          <label className="mb-2 block text-sm font-medium text-slate-700">
            Tên nhóm
          </label>
          <input
            className={fieldClassName}
            placeholder="v.d. Nhóm ôn tập Java"
            type="text"
            value={groupName}
            onChange={(e) =>
              onChange({ groupName: e.target.value, goalDescription })
            }
          />
        </div>

        <div>
          <label className="mb-2 block text-sm font-medium text-slate-700">
            Mô tả mục tiêu
          </label>
          <textarea
            className={textAreaClassName}
            placeholder="Mô tả mục tiêu tập trung của nhóm..."
            value={goalDescription}
            onChange={(e) =>
              onChange({ groupName, goalDescription: e.target.value })
            }
          />
        </div>
      </div>
    </SectionCard>
  );
}
