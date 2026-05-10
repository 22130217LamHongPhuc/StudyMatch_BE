import { Settings2 } from "lucide-react";
import type { FreeTime } from "../../Onboarding/components/types";
import FreeTimePicker from "./FreeTimePicker";
import SectionCard from "./SectionCard";

export type Visibility = "public" | "private";

interface GroupSettingsSectionProps {
  maxMembers: number;
  onMaxMembersChange: (next: number) => void;
  visibility: Visibility;
  onVisibilityChange: (next: Visibility) => void;
  freeTime: FreeTime;
  onFreeTimeChange: (next: FreeTime) => void;
}

export default function GroupSettingsSection({
  maxMembers,
  onMaxMembersChange,
  visibility,
  onVisibilityChange,
  freeTime,
  onFreeTimeChange,
}: GroupSettingsSectionProps) {
  return (
    <SectionCard
      icon={<Settings2 className="h-5 w-5" />}
      title="Thiết lập nhóm"
    >
      <div className="space-y-6">
        <div>
          <label className="mb-2 block text-sm font-medium text-slate-700">
            Số lượng thành viên tối đa 3 - 10
          </label>

          <input
            className="h-2 w-full cursor-pointer appearance-none rounded-lg bg-slate-200 accent-orange-500"
            max={10}
            min={3}
            type="range"
            value={maxMembers}
            onChange={(e) => onMaxMembersChange(Number(e.target.value))}
          />

          <div className="mt-2 flex justify-between px-1 text-xs text-slate-400">
            {[3, 4, 5, 6, 7, 8, 9, 10].map((num) => (
              <span
                key={num}
                className={
                  num === maxMembers ? "font-semibold text-orange-600" : ""
                }
              >
                {num}
              </span>
            ))}
          </div>
        </div>

        <div className="grid grid-cols-1 gap-4 md:grid-cols-2">
          <VisibilityOption
            value="public"
            title="Công khai"
            description="Sinh viên phù hợp có thể yêu cầu tham gia"
            checked={visibility === "public"}
            onChange={onVisibilityChange}
          />

          <VisibilityOption
            value="private"
            title="Riêng tư"
            description="Chỉ những sinh viên được mời mới có thể tham gia"
            checked={visibility === "private"}
            onChange={onVisibilityChange}
          />
        </div>

        <div className="pt-2">
          <div className="mb-3">
            <p className="text-sm font-semibold text-slate-800">
              Thời gian rảnh của nhóm
            </p>
          </div>

          <FreeTimePicker value={freeTime} onChange={onFreeTimeChange} />
        </div>
      </div>
    </SectionCard>
  );
}

interface VisibilityOptionProps {
  value: Visibility;
  title: string;
  description: string;
  checked: boolean;
  onChange: (next: Visibility) => void;
}

function VisibilityOption({
  value,
  title,
  description,
  checked,
  onChange,
}: VisibilityOptionProps) {
  return (
    <label className="flex cursor-pointer items-center gap-4 rounded-xl border-2 border-slate-100 bg-slate-50/30 p-4 transition-colors hover:bg-slate-50 has-[:checked]:border-orange-500 has-[:checked]:bg-orange-50">
      <input
        className="text-orange-600 focus:ring-orange-200"
        name="visibility"
        value={value}
        type="radio"
        checked={checked}
        onChange={() => onChange(value)}
      />
      <div>
        <span className="block font-semibold text-slate-900">{title}</span>
        <span className="text-xs text-slate-500">{description}</span>
      </div>
    </label>
  );
}
