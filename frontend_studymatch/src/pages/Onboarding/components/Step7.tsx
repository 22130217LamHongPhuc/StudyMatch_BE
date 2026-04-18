import { Sunrise, Sun, MoonStar } from "lucide-react";
import { FormData, SlotId, StudyMode, StudyPlan } from "./types";
import { DAYS, GOALS, MODES, SLOTS, getSubjectLabel } from "./constants";

interface Step7Props {
  data: FormData;
  studyPlan: StudyPlan | null;
}

export function Step7({ data, studyPlan }: Step7Props) {
  const goalObj = GOALS.find((g) => g.key === data.studyGoal);
  const modeObj = data.studyMode ? MODES[data.studyMode as StudyMode] : null;
  const allMods = [data.mainModule, ...data.enrolledModules].filter(Boolean);

  const freeTimeNormalized: Record<string, number> = Object.fromEntries(
    DAYS.map((d) => {
      const slots = data.freeTime[d.id];
      const count = Object.values(slots).filter(Boolean).length;
      return [
        `day_${d.id}_activity`,
        parseFloat((count / SLOTS.length).toFixed(4)),
      ];
    }),
  );

  const subjectFlags: Record<string, number> = Object.fromEntries(
    allMods.map((code) => [`subject_${code}`, 1]),
  );

  const vector = {
    cohort_code: data.cohortCode,
    gender_F: data.gender === "F" ? 1 : 0,
    gender_M: data.gender === "M" ? 1 : 0,
    age_encoded:
      ({ "0-35": 1, "35-55": 2, "55<=": 3 } as Record<string, number>)[
        data.ageGroup
      ] || 0,
    num_of_prev_attempts: data.prevAttempts,
    avg_score: data.avgScore,
    ...freeTimeNormalized,
    ...subjectFlags,
    study_goal: data.studyGoal,
    mode: data.studyMode,
    code_module: data.mainModule,
    code_presentation: studyPlan?.termFullName || data.cohortCode,
  };

  const topDays = DAYS.filter((d) =>
    Object.values(data.freeTime[d.id]).some(Boolean),
  );

  const selectedModules = [data.mainModule, ...data.enrolledModules].filter(
    Boolean,
  );
  const moduleScheduleSummary = selectedModules
    .map((code) => {
      const moduleSchedule = data.moduleSlots[code];
      if (!moduleSchedule) return null;

      const daySummaries = DAYS.map((day) => {
        const pickedSlots = SLOTS.filter(
          (slot) => moduleSchedule[day.id][slot.id],
        ).map((slot) => slot.label);

        if (pickedSlots.length === 0) return null;
        return `${day.short}: ${pickedSlots.join(", ")}`;
      }).filter((line): line is string => Boolean(line));

      return { code, daySummaries };
    })
    .filter((item): item is { code: string; daySummaries: string[] } =>
      Boolean(item),
    );

  const selectedMainSubject = studyPlan?.subjects?.find(
    (subject) => subject.subjectCode === data.mainModule,
  );

  const reviewRows: [string, string][] = [
    [
      "Giới tính",
      data.gender === "M" ? "Nam" : data.gender === "F" ? "Nữ" : "—",
    ],
    ["Khu vực", data.region || "—"],
    ["Khóa hiện tại", data.cohortCode || "—"],
    [
      "Môn chính",
      data.mainModule
        ? selectedMainSubject
          ? getSubjectLabel(selectedMainSubject)
          : data.mainModule
        : "—",
    ],
    ["Điểm TB", `${data.avgScore}/10`],
    ["Lần học lại", `${data.prevAttempts} lần`],
  ];

  return (
    <div className="space-y-4">
      <div
        className={`rounded-2xl border-2 p-4 ${goalObj?.bg} ${goalObj?.border}`}
      >
        <div className="flex items-center gap-3 mb-4">
          <div
            className={`w-12 h-12 rounded-2xl flex items-center justify-center text-2xl border-2 ${goalObj?.border} bg-white`}
          >
            {goalObj?.icon && (
              <goalObj.icon size={24} className={goalObj?.text} />
            )}
          </div>
          <div className="flex-1 min-w-0">
            <p className="font-bold text-gray-800 truncate">
              {data.fullName || "—"}
            </p>
            <p className="text-xs text-gray-500">
              MSSV: {data.studentId || "—"}
            </p>
          </div>
          <div className="text-right shrink-0">
            <span
              className={`text-xs font-semibold px-2 py-1 rounded-lg border ${goalObj?.badge}`}
            >
              {goalObj?.title}
            </span>
            {modeObj && (
              <p className="text-xs text-gray-500 mt-1">
                {modeObj.icon && (
                  <modeObj.icon size={14} className="inline mr-1.5" />
                )}{" "}
                {modeObj.label}
              </p>
            )}
          </div>
        </div>
        <div className="grid grid-cols-2 gap-x-4 gap-y-0 text-sm border-t border-white border-opacity-60 pt-3">
          {reviewRows.map(([k, v]) => (
            <div
              key={k}
              className="flex justify-between py-1.5 border-b border-white border-opacity-40 gap-2"
            >
              <span className="text-gray-500 shrink-0">{k}</span>
              <span
                className={`font-medium text-right truncate ${goalObj?.text}`}
              >
                {v}
              </span>
            </div>
          ))}
        </div>
      </div>

      <div className="grid grid-cols-2 gap-3">
        <div className="bg-green-50 border border-green-200 rounded-xl p-3">
          <p className="text-xs font-semibold text-green-700 uppercase tracking-wider mb-2">
            Môn đang học
          </p>
          <div className="flex flex-wrap gap-1.5">
            {data.mainModule && (
              <span className="text-xs font-bold text-blue-700 bg-blue-100 border border-blue-200 px-2 py-0.5 rounded-md">
                ★ {data.mainModule}
              </span>
            )}
            {data.enrolledModules.map((m) => (
              <span
                key={m}
                className="text-xs font-medium text-green-700 bg-green-100 px-2 py-0.5 rounded-md"
              >
                {m}
              </span>
            ))}
            {allMods.length === 0 && (
              <span className="text-xs text-gray-400">Chưa chọn</span>
            )}
          </div>
          {studyPlan?.termFullName && (
            <p className="text-[11px] text-green-600 mt-2">
              {studyPlan.termFullName}
            </p>
          )}

          {moduleScheduleSummary.length > 0 && (
            <div className="mt-3 space-y-1">
              {moduleScheduleSummary.slice(0, 3).map((item) => (
                <p key={item.code} className="text-[11px] text-green-700">
                  {item.code}: {item.daySummaries.join(" | ") || "Chưa chọn"}
                </p>
              ))}
              {moduleScheduleSummary.length > 3 && (
                <p className="text-[11px] text-green-500">
                  +{moduleScheduleSummary.length - 3} môn khác
                </p>
              )}
            </div>
          )}
        </div>

        <div className="bg-indigo-50 border border-indigo-200 rounded-xl p-3">
          <p className="text-xs font-semibold text-indigo-700 uppercase tracking-wider mb-2">
            Thời gian rảnh
          </p>
          {topDays.slice(0, 5).map((d) => {
            const ft = data.freeTime[d.id];
            const hasMorning = ft.ca1 || ft.ca2;
            const hasAfternoon = ft.ca3 || ft.ca4;
            const hasEvening = ft.ca5 || ft.ca6;
            return (
              <div key={d.id} className="flex items-center gap-1.5 mb-1">
                <span className="text-xs font-medium text-indigo-600 w-6">
                  {d.short}
                </span>
                {hasMorning && <Sunrise size={14} className="text-amber-500" />}
                {hasAfternoon && <Sun size={14} className="text-blue-500" />}
                {hasEvening && (
                  <MoonStar size={14} className="text-indigo-500" />
                )}
              </div>
            );
          })}
          {topDays.length > 5 && (
            <p className="text-xs text-indigo-400">
              +{topDays.length - 5} ngày khác
            </p>
          )}
          {topDays.length === 0 && (
            <p className="text-xs text-gray-400">Chưa chọn</p>
          )}
        </div>
      </div>
    </div>
  );
}
