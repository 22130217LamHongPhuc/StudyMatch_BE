import { useMemo } from "react";
import type {
  DayId,
  FreeTime,
  SlotId,
} from "../../Onboarding/components/types";
import {
  DAYS,
  initFreeTime,
  SLOTS,
} from "../../Onboarding/components/constants";

interface FreeTimePickerProps {
  value?: FreeTime;
  onChange?: (next: FreeTime) => void;
}

export default function FreeTimePicker({
  value,
  onChange,
}: FreeTimePickerProps) {
  const freeTime = value ?? initFreeTime();

  const totalSelected = useMemo(
    () =>
      DAYS.reduce(
        (acc, d) => acc + Object.values(freeTime[d.id]).filter(Boolean).length,
        0,
      ),
    [freeTime],
  );

  const toggle = (dayId: DayId, slotId: SlotId): void => {
    onChange?.({
      ...freeTime,
      [dayId]: {
        ...freeTime[dayId],
        [slotId]: !freeTime[dayId][slotId],
      },
    });
  };

  const selectAll = (slotId: SlotId): void => {
    const allOn = DAYS.every((d) => freeTime[d.id][slotId]);
    const updated: FreeTime = { ...freeTime };
    DAYS.forEach((d) => {
      updated[d.id] = { ...updated[d.id], [slotId]: !allOn };
    });
    onChange?.(updated);
  };

  return (
    <div className="space-y-4">
      <div className="flex items-start justify-between gap-4">
        <p className="text-sm leading-relaxed text-slate-600">
          Chọn các khung giờ rảnh trong tuần để hệ thống ghép nhóm có lịch phù
          hợp.
        </p>
        {totalSelected > 0 && (
          <span className="whitespace-nowrap rounded-full border border-orange-200 bg-orange-50 px-2 py-1 text-xs font-semibold text-orange-700">
            {totalSelected} slot
          </span>
        )}
      </div>

      <div className="-mx-1 overflow-x-auto">
        <table className="w-full min-w-max">
          <thead>
            <tr>
              <th className="w-16 pb-3" />
              {SLOTS.map((s) => {
                const Icon = s.icon;
                return (
                  <th key={s.id} className="px-3 pb-3 text-center">
                    <button
                      type="button"
                      onClick={() => selectAll(s.id)}
                      className="group mx-auto flex flex-col items-center gap-1"
                      title={`Chọn tất cả ${s.label}`}
                    >
                      <Icon
                        size={18}
                        className="text-slate-500 group-hover:text-orange-600"
                      />
                      <span className="text-xs font-semibold text-slate-700 group-hover:text-orange-600">
                        {s.label}
                      </span>
                      <span className="text-xs text-slate-400">{s.time}</span>
                    </button>
                  </th>
                );
              })}
            </tr>
          </thead>

          <tbody>
            {DAYS.map((d, di) => {
              const daySlots = freeTime[d.id];
              const dayCount = Object.values(daySlots).filter(Boolean).length;
              const isWeekend = d.id >= 5;

              return (
                <tr key={d.id} className={di % 2 === 0 ? "bg-slate-50" : ""}>
                  <td className="py-2 pr-3">
                    <div className="flex items-center gap-1.5">
                      <span
                        className={`text-sm font-semibold ${isWeekend ? "text-orange-600" : "text-slate-700"}`}
                      >
                        {d.short}
                      </span>
                      {dayCount > 0 && (
                        <span className="flex h-4 w-4 items-center justify-center rounded-full bg-orange-600 text-xs font-bold text-white">
                          {dayCount}
                        </span>
                      )}
                    </div>
                  </td>

                  {SLOTS.map((s) => {
                    const active = daySlots[s.id];
                    return (
                      <td key={s.id} className="px-2 py-2 text-center">
                        <button
                          type="button"
                          onClick={() => toggle(d.id, s.id)}
                          className={
                            "w-full rounded-xl border-2 py-2.5 text-xs font-semibold transition-colors " +
                            (active
                              ? "border-orange-400 bg-orange-500 text-white"
                              : "border-slate-200 bg-white text-slate-500 hover:border-orange-300 hover:bg-orange-50")
                          }
                        >
                          {active ? "✓" : "—"}
                        </button>
                      </td>
                    );
                  })}
                </tr>
              );
            })}
          </tbody>
        </table>
      </div>

      <p className="text-xs text-slate-400">
        Nhấn vào tiêu đề cột để chọn/bỏ chọn tất cả.
      </p>
    </div>
  );
}
