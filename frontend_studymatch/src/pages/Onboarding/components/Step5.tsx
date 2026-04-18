import { DayId, FormData, SlotId } from "./types";
import { DAYS, SLOTS } from "./constants";

interface Step5Props {
  data: FormData;
  update: (key: keyof FormData, value: FormData[keyof FormData]) => void;
}

export function Step5({ data, update }: Step5Props) {
  const toggle = (dayId: DayId, slotId: SlotId): void => {
    update("freeTime", {
      ...data.freeTime,
      [dayId]: {
        ...data.freeTime[dayId],
        [slotId]: !data.freeTime[dayId][slotId],
      },
    });
  };

  const selectAll = (slotId: SlotId): void => {
    const allOn = DAYS.every((d) => data.freeTime[d.id][slotId]);
    const updated = { ...data.freeTime };
    DAYS.forEach((d) => {
      updated[d.id] = { ...updated[d.id], [slotId]: !allOn };
    });
    update("freeTime", updated);
  };

  const totalSelected = DAYS.reduce(
    (acc, d) => acc + Object.values(data.freeTime[d.id]).filter(Boolean).length,
    0,
  );

  const slotColors: Record<SlotId, { active: string; inactive: string }> = {
    ca1: {
      active: "bg-blue-300 border-amber-400 text-white",
      inactive:
        "border-gray-200 bg-white text-gray-300 hover:border-amber-300 hover:bg-amber-50",
    },
    ca2: {
      active: "bg-blue-300 border-amber-400 text-white",
      inactive:
        "border-gray-200 bg-white text-gray-300 hover:border-amber-300 hover:bg-amber-50",
    },
    ca3: {
      active: "bg-blue-400 border-blue-400 text-white",
      inactive:
        "border-gray-200 bg-white text-gray-300 hover:border-blue-300 hover:bg-blue-50",
    },
    ca4: {
      active: "bg-blue-400 border-blue-400 text-white",
      inactive:
        "border-gray-200 bg-white text-gray-300 hover:border-blue-300 hover:bg-blue-50",
    },
    ca5: {
      active: "bg-indigo-500 border-indigo-500 text-white",
      inactive:
        "border-gray-200 bg-white text-gray-300 hover:border-indigo-300 hover:bg-indigo-50",
    },
    ca6: {
      active: "bg-indigo-500 border-indigo-500 text-white",
      inactive:
        "border-gray-200 bg-white text-gray-300 hover:border-indigo-300 hover:bg-indigo-50",
    },
  };

  return (
    <div className="space-y-4">
      <div className="flex items-center justify-between">
        <p className="text-sm text-gray-500">
          Chọn các khung giờ rảnh trong tuần để hệ thống ghép bạn học cùng lịch.
        </p>
        {totalSelected > 0 && (
          <span className="text-xs font-semibold text-blue-600 bg-blue-50 border border-blue-200 px-2 py-1 rounded-full whitespace-nowrap">
            {totalSelected} slot
          </span>
        )}
      </div>

      <div className="overflow-x-auto -mx-1">
        <table className="w-full min-w-max">
          <thead>
            <tr>
              <th className="w-16 pb-3"></th>
              {SLOTS.map((s) => {
                const Icon = s.icon;
                return (
                  <th key={s.id} className="pb-3 text-center px-3">
                    <button
                      onClick={() => selectAll(s.id)}
                      className="flex flex-col items-center gap-1 mx-auto group"
                      title={`Chọn tất cả ${s.label}`}
                    >
                      <Icon
                        size={18}
                        className="text-gray-500 group-hover:text-blue-600"
                      />
                      <span className="text-xs font-semibold text-gray-700 group-hover:text-blue-600">
                        {s.label}
                      </span>
                      <span className="text-xs text-gray-400">{s.time}</span>
                    </button>
                  </th>
                );
              })}
            </tr>
          </thead>
          <tbody className="space-y-1">
            {DAYS.map((d, di) => {
              const daySlots = data.freeTime[d.id];
              const dayCount = Object.values(daySlots).filter(Boolean).length;
              const isWeekend = d.id >= 5;
              return (
                <tr
                  key={d.id}
                  className={di % 2 === 0 ? "bg-gray-50 rounded-xl" : ""}
                >
                  <td className="py-2 pr-3">
                    <div className="flex items-center gap-1.5">
                      <span
                        className={`text-sm font-semibold ${isWeekend ? "text-orange-500" : "text-gray-700"}`}
                      >
                        {d.short}
                      </span>
                      {dayCount > 0 && (
                        <span className="w-4 h-4 rounded-full bg-blue-500 text-white text-xs flex items-center justify-center font-bold">
                          {dayCount}
                        </span>
                      )}
                    </div>
                  </td>
                  {SLOTS.map((s) => {
                    const active = daySlots[s.id];
                    const colorSet = slotColors[s.id];
                    return (
                      <td key={s.id} className="py-2 px-2 text-center">
                        <button
                          onClick={() => toggle(d.id, s.id)}
                          className={`w-full py-2.5 rounded-xl border-2 text-xs font-semibold transition-all duration-100 ${active ? colorSet.active : colorSet.inactive}`}
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

      <div className="flex items-center gap-4 pt-1">
        <span className="text-xs text-gray-400">
          Nhấn vào tiêu đề cột để chọn cả ngày •
        </span>
        {[
          { color: "bg-blue-300", label: "Sáng" },
          { color: "bg-blue-400", label: "Chiều" },
          { color: "bg-indigo-500", label: "Tối" },
        ].map((l) => (
          <div key={l.label} className="flex items-center gap-1.5">
            <div className={`w-3 h-3 rounded ${l.color}`}></div>
            <span className="text-xs text-gray-500">{l.label}</span>
          </div>
        ))}
      </div>
    </div>
  );
}
