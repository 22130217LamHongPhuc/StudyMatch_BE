import { Check } from "lucide-react";
import { FormData, StudyGoal, StudyMode } from "./types";
import { GOALS, MODES, VALID_MODES } from "./constants";

interface Step3ModeProps {
  data: FormData;
  update: (key: keyof FormData, value: FormData[keyof FormData]) => void;
}

export function Step3Mode({ data, update }: Step3ModeProps) {
  const goalObj = GOALS.find((g) => g.key === data.studyGoal);
  const availModes: StudyMode[] = data.studyGoal
    ? (VALID_MODES[data.studyGoal as StudyGoal] ?? [])
    : [];

  return (
    <div className="space-y-4">
      <div
        className={`inline-flex items-center gap-2 px-3 py-1.5 rounded-xl text-xs font-semibold border ${goalObj?.badge}`}
      >
        {goalObj?.icon && <goalObj.icon size={14} />} {goalObj?.title}
      </div>
      <p className="text-sm text-gray-500">
        Dựa trên trình độ của bạn, hãy chọn{" "}
        <strong>cách bạn muốn học cùng người khác</strong>:
      </p>
      <div className="space-y-3">
        {availModes.map((mKey) => {
          const m = MODES[mKey];
          const active = data.studyMode === mKey;
          const Icon = m.icon;
          return (
            <button
              key={mKey}
              onClick={() => update("studyMode", mKey)}
              className={`w-full text-left p-4 rounded-2xl border-2 transition-all duration-150 ${active ? `${m.bg} ${m.border}` : "bg-white border-gray-100 hover:border-gray-200 hover:bg-gray-50"}`}
            >
              <div className="flex items-start gap-3">
                <div
                  className={`w-10 h-10 rounded-xl flex items-center justify-center ${active ? m.bg : "bg-gray-50"}`}
                >
                  <Icon
                    size={20}
                    className={active ? m.text : "text-gray-500"}
                  />
                </div>
                <div className="flex-1">
                  <p
                    className={`font-semibold text-sm ${active ? m.text : "text-gray-700"}`}
                  >
                    {m.label}
                  </p>
                  <p className="text-xs text-gray-400 mt-0.5 leading-relaxed">
                    {m.desc}
                  </p>
                </div>
                {active && (
                  <div className="w-5 h-5 rounded-full bg-blue-500 flex items-center justify-center shrink-0 mt-0.5">
                    <Check className="w-3 h-3 text-white" strokeWidth={3} />
                  </div>
                )}
              </div>
            </button>
          );
        })}
      </div>
    </div>
  );
}
