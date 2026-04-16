import { useState } from "react";
import { Check } from "lucide-react";
import {
  FormData,
  StudyPlan,
  DayConfig,
  SlotConfig,
  DayId,
  SlotId,
  FreeTime,
} from "./types";
import {
  DAYS,
  SLOTS,
  getSortedSubjects,
  getStudyPlanTitle,
  normalizeModuleSchedule,
} from "./constants";

interface Step4Props {
  data: FormData;
  update: (key: keyof FormData, value: FormData[keyof FormData]) => void;
  studyPlan: StudyPlan | null;
  studyPlanLoading: boolean;
  studyPlanError: string;
}

export function Step4CurrentPlan({
  data,
  update,
  studyPlan,
  studyPlanLoading,
  studyPlanError,
}: Step4Props) {
  const [editingModuleCode, setEditingModuleCode] = useState<string | null>(
    null,
  );
  const subjects = getSortedSubjects(studyPlan?.subjects || []);
  const selectedModules = [data.mainModule, ...data.enrolledModules].filter(
    Boolean,
  );
  const hasMissingModuleSlots = selectedModules.some((code) => {
    const schedule = data.moduleSlots[code];
    return (
      !schedule ||
      !DAYS.some((day) => Object.values(schedule[day.id]).some(Boolean))
    );
  });

  const toggleSubject = (subjectCode: string): void => {
    const current = data.enrolledModules;
    update(
      "enrolledModules",
      current.includes(subjectCode)
        ? current.filter((code) => code !== subjectCode)
        : [...current, subjectCode],
    );
  };

  const toggleModuleSlot = (
    moduleCode: string,
    dayId: DayId,
    slotId: SlotId,
  ): void => {
    const currentModuleSchedule =
      data.moduleSlots[moduleCode] ?? normalizeModuleSchedule();

    update("moduleSlots", {
      ...data.moduleSlots,
      [moduleCode]: {
        ...currentModuleSchedule,
        [dayId]: {
          ...currentModuleSchedule[dayId],
          [slotId]: !currentModuleSchedule[dayId][slotId],
        },
      },
    });
  };

  const selectAllModuleSlot = (moduleCode: string, slotId: SlotId): void => {
    const currentModuleSchedule =
      data.moduleSlots[moduleCode] ?? normalizeModuleSchedule();
    const allOn = DAYS.every((day) => currentModuleSchedule[day.id][slotId]);

    const updatedModuleSchedule = { ...currentModuleSchedule };
    DAYS.forEach((day) => {
      updatedModuleSchedule[day.id] = {
        ...updatedModuleSchedule[day.id],
        [slotId]: !allOn,
      };
    });

    update("moduleSlots", {
      ...data.moduleSlots,
      [moduleCode]: updatedModuleSchedule,
    });
  };

  const clearModuleSchedule = (moduleCode: string): void => {
    update("moduleSlots", {
      ...data.moduleSlots,
      [moduleCode]: normalizeModuleSchedule(),
    });
  };

  const getModuleSchedule = (moduleCode: string): FreeTime =>
    data.moduleSlots[moduleCode] ?? normalizeModuleSchedule();

  const getModuleSelectedCount = (moduleCode: string): number => {
    const moduleSchedule = getModuleSchedule(moduleCode);
    return DAYS.reduce(
      (acc, day) =>
        acc + Object.values(moduleSchedule[day.id]).filter(Boolean).length,
      0,
    );
  };

  const getModuleDaySummary = (moduleCode: string): string[] => {
    const moduleSchedule = getModuleSchedule(moduleCode);
    return DAYS.map((day) => {
      const pickedSlots = SLOTS.filter(
        (slot) => moduleSchedule[day.id][slot.id],
      ).map((slot) => slot.label);

      if (pickedSlots.length === 0) return null;
      return `${day.short}: ${pickedSlots.join(", ")}`;
    }).filter((line): line is string => Boolean(line));
  };

  const editingModuleInfo = editingModuleCode
    ? subjects.find((s) => s.subjectCode === editingModuleCode)
    : null;

  return (
    <div className="space-y-4">
      <div className="flex flex-wrap gap-2">
        {data.cohortCode && (
          <span className="bg-blue-100 text-blue-700 text-xs font-semibold px-3 py-1 rounded-full">
            Khóa {data.cohortCode}
          </span>
        )}
        {studyPlan?.termFullName && (
          <span className="bg-indigo-100 text-indigo-700 text-xs font-semibold px-3 py-1 rounded-full">
            {studyPlan.termFullName}
          </span>
        )}
        {studyPlan?.studyYearNo ? (
          <span className="bg-gray-100 text-gray-700 text-xs font-semibold px-3 py-1 rounded-full">
            Năm học {studyPlan.studyYearNo}
          </span>
        ) : null}
      </div>

      <p className="text-sm text-gray-500">
        Hệ thống tự tải chương trình học hiện tại của khóa bạn đã chọn. Chọn môn
        chính trước, sau đó có thể chọn thêm các môn khác đang học.
      </p>

      {studyPlanLoading ? (
        <div className="rounded-2xl border border-gray-200 bg-gray-50 px-4 py-3 text-sm text-gray-500">
          Đang tải danh sách môn học hiện tại...
        </div>
      ) : studyPlanError ? (
        <div className="rounded-2xl border border-red-200 bg-red-50 px-4 py-3 text-sm text-red-600">
          {studyPlanError}
        </div>
      ) : !studyPlan ? (
        <div className="rounded-2xl border border-gray-200 bg-gray-50 px-4 py-3 text-sm text-gray-500">
          Hãy quay lại step 1 để chọn khóa, sau đó hệ thống sẽ load môn học
          tương ứng.
        </div>
      ) : (
        <>
          <div className="rounded-2xl border border-blue-100 bg-blue-50 px-4 py-3 space-y-1">
            <p className="text-sm font-semibold text-blue-700 truncate">
              {studyPlan.curriculumName}
            </p>
            <p className="text-xs text-blue-500">
              {getStudyPlanTitle(studyPlan)}
            </p>
          </div>

          <div className="space-y-2">
            <p className="text-xs font-semibold text-gray-400 uppercase tracking-widest">
              Chọn môn chính
            </p>
            {subjects.map((subject) => {
              const active = data.mainModule === subject.subjectCode;
              return (
                <button
                  key={subject.subjectId}
                  type="button"
                  onClick={() =>
                    update("mainModule", String(subject.subjectCode))
                  }
                  className={`w-full text-left px-4 py-3.5 rounded-xl border-2 transition-all flex items-center gap-4 ${active ? "border-blue-500 bg-blue-50" : "border-gray-200 bg-white hover:border-gray-300 hover:bg-gray-50"}`}
                >
                  <span
                    className={`text-xs font-bold w-16 py-1 text-center rounded-lg ${active ? "bg-blue-500 text-white" : "bg-gray-100 text-gray-500"}`}
                  >
                    {subject.subjectCode}
                  </span>
                  <span
                    className={`text-sm flex-1 ${active ? "text-blue-700 font-medium" : "text-gray-700"}`}
                  >
                    {subject.subjectName}
                  </span>
                  <div className="flex items-center gap-2">
                    {active && (
                      <div className="w-5 h-5 rounded-full bg-blue-500 flex items-center justify-center shrink-0">
                        <Check className="w-3 h-3 text-white" strokeWidth={3} />
                      </div>
                    )}
                  </div>
                </button>
              );
            })}
          </div>

          <div className="space-y-2">
            <p className="text-xs font-semibold text-gray-400 uppercase tracking-widest">
              Môn khác đang học
            </p>
            {subjects
              .filter((subject) => subject.subjectCode !== data.mainModule)
              .map((subject) => {
                const active = data.enrolledModules.includes(
                  subject.subjectCode,
                );
                return (
                  <button
                    key={subject.subjectId}
                    type="button"
                    onClick={() => toggleSubject(String(subject.subjectCode))}
                    className={`w-full text-left px-4 py-3.5 rounded-xl border-2 transition-all flex items-center gap-3 ${active ? "border-emerald-500 bg-emerald-50" : "border-gray-200 bg-white hover:border-gray-300 hover:bg-gray-50"}`}
                  >
                    <div className="flex items-center gap-2">
                      {active && (
                        <div className="w-5 h-5 rounded-full bg-blue-500 flex items-center justify-center shrink-0">
                          <Check
                            className="w-3 h-3 text-white"
                            strokeWidth={3}
                          />
                        </div>
                      )}
                    </div>
                    <span className="text-xs font-bold w-16 py-1 text-center rounded-lg">
                      {subject.subjectCode}
                    </span>
                    <span
                      className={`text-sm flex-1 ${active ? "text-emerald-700 font-medium" : "text-gray-700"}`}
                    >
                      {subject.subjectName}
                    </span>
                  </button>
                );
              })}
          </div>

          {data.enrolledModules.length > 0 && (
            <div className="flex items-center gap-2 flex-wrap bg-emerald-50 border border-emerald-200 rounded-xl px-4 py-2.5">
              <span className="text-xs text-emerald-600 font-semibold">
                Đã chọn:
              </span>
              {data.enrolledModules.map((code) => (
                <span
                  key={code}
                  className="text-xs font-bold text-emerald-700 bg-emerald-200 px-2 py-0.5 rounded-md"
                >
                  {code}
                </span>
              ))}
            </div>
          )}

          {selectedModules.length > 0 && (
            <div className="space-y-3 rounded-2xl border border-blue-100 bg-blue-50 p-4">
              <div className="flex items-start justify-between gap-3">
                <div>
                  <p className="text-xs font-semibold text-blue-700 uppercase tracking-widest">
                    Ca học theo từng môn
                  </p>
                  <p className="text-xs text-blue-500 mt-1">
                    Chọn ít nhất 1 ô thời gian (thứ + ca) cho mỗi môn.
                  </p>
                </div>
                <span className="text-xs font-semibold text-blue-700 bg-white border border-blue-200 px-2 py-1 rounded-full">
                  {selectedModules.length} môn
                </span>
              </div>

              <div className="space-y-2">
                {selectedModules.map((moduleCode) => {
                  const moduleInfo = subjects.find(
                    (s) => s.subjectCode === moduleCode,
                  );
                  const selectedCount = getModuleSelectedCount(moduleCode);
                  const daySummaries = getModuleDaySummary(moduleCode);

                  return (
                    <div
                      key={moduleCode}
                      className="bg-white rounded-xl border border-blue-100 p-3"
                    >
                      <div className="flex items-start justify-between gap-3">
                        <div className="min-w-0">
                          <div className="flex items-center gap-2 flex-wrap">
                            <span
                              className={`text-[11px] font-bold px-2 py-0.5 rounded-md ${moduleCode === data.mainModule ? "bg-blue-600 text-white" : "bg-emerald-100 text-emerald-700"}`}
                            >
                              {moduleCode}
                            </span>
                            <span className="text-xs text-gray-700 truncate">
                              {moduleInfo?.subjectName || "Môn đã chọn"}
                            </span>
                          </div>
                          <p className="text-[11px] text-gray-400 mt-1">
                            {selectedCount > 0
                              ? `Đã chọn ${selectedCount} ô thời gian`
                              : "Chưa chọn thời gian"}
                          </p>
                          {daySummaries.length > 0 && (
                            <p className="text-[11px] text-blue-600 mt-1 line-clamp-2">
                              {daySummaries.join(" | ")}
                            </p>
                          )}
                        </div>
                        <button
                          type="button"
                          onClick={() => setEditingModuleCode(moduleCode)}
                          className="shrink-0 text-xs font-semibold px-3 py-1.5 rounded-lg border border-blue-300 text-blue-700 bg-blue-50 hover:bg-blue-100"
                        >
                          {selectedCount > 0
                            ? "Sửa thời gian môn học"
                            : "Thêm thời gian môn học"}
                        </button>
                      </div>
                    </div>
                  );
                })}
              </div>

              {hasMissingModuleSlots && (
                <p className="text-xs text-red-500">
                  Bạn cần chọn ít nhất 1 ô thời gian (thứ + ca) cho mỗi môn để
                  tiếp tục.
                </p>
              )}
            </div>
          )}

          {editingModuleCode && (
            <div className="fixed inset-0 z-50 flex items-center justify-center px-4">
              <div
                className="absolute inset-0 bg-black/40"
                onClick={() => setEditingModuleCode(null)}
              ></div>
              <div className="relative w-full max-w-3xl max-h-[85vh] overflow-auto rounded-2xl border border-blue-100 bg-white shadow-xl p-5">
                <div className="flex items-start justify-between gap-4 mb-4">
                  <div>
                    <p className="text-xs font-semibold text-blue-600 uppercase tracking-widest">
                      Cập nhật thời gian môn học
                    </p>
                    <h3 className="text-base font-bold text-gray-800 mt-1">
                      {editingModuleCode} -{" "}
                      {editingModuleInfo?.subjectName || "Môn đã chọn"}
                    </h3>
                    <p className="text-xs text-gray-500 mt-1">
                      Chọn lịch chi tiết theo thứ và ca cho môn này.
                    </p>
                  </div>
                  <button
                    type="button"
                    onClick={() => setEditingModuleCode(null)}
                    className="text-sm font-semibold text-gray-500 hover:text-gray-700"
                  >
                    Đóng
                  </button>
                </div>

                <div className="overflow-x-auto -mx-1">
                  <table className="w-full min-w-[640px]">
                    <thead>
                      <tr>
                        <th className="w-16 pb-3"></th>
                        {SLOTS.map((slot: SlotConfig) => (
                          <th key={slot.id} className="pb-3 text-center px-2">
                            <button
                              type="button"
                              onClick={() =>
                                selectAllModuleSlot(editingModuleCode, slot.id)
                              }
                              className="flex flex-col items-center gap-1 mx-auto"
                              title={`Chọn tất cả ${slot.label}`}
                            >
                              <span className="text-xs font-semibold text-gray-700">
                                {slot.label}
                              </span>
                              <span className="text-xs text-gray-400">
                                {slot.time}
                              </span>
                            </button>
                          </th>
                        ))}
                      </tr>
                    </thead>
                    <tbody>
                      {DAYS.map((day: DayConfig, dayIdx: number) => {
                        const moduleSchedule =
                          getModuleSchedule(editingModuleCode);
                        const daySlots = moduleSchedule[day.id];
                        const dayCount =
                          Object.values(daySlots).filter(Boolean).length;
                        const isWeekend = day.id >= 5;

                        return (
                          <tr
                            key={`modal-${editingModuleCode}-${day.id}`}
                            className={dayIdx % 2 === 0 ? "bg-gray-50" : ""}
                          >
                            <td className="py-2 pr-3">
                              <div className="flex items-center gap-1.5">
                                <span
                                  className={`text-sm font-semibold ${isWeekend ? "text-orange-500" : "text-gray-700"}`}
                                >
                                  {day.short}
                                </span>
                                {dayCount > 0 && (
                                  <span className="w-4 h-4 rounded-full bg-blue-500 text-white text-xs flex items-center justify-center font-bold">
                                    {dayCount}
                                  </span>
                                )}
                              </div>
                            </td>
                            {SLOTS.map((slot: SlotConfig) => {
                              const active = daySlots[slot.id];
                              return (
                                <td
                                  key={`modal-${editingModuleCode}-${day.id}-${slot.id}`}
                                  className="py-2 px-1 text-center"
                                >
                                  <button
                                    type="button"
                                    onClick={() =>
                                      toggleModuleSlot(
                                        editingModuleCode,
                                        day.id,
                                        slot.id,
                                      )
                                    }
                                    className={`w-full rounded-lg border text-xs font-semibold py-2 transition-all ${active ? "bg-blue-500 border-blue-500 text-white" : "bg-white border-gray-200 text-gray-400 hover:border-blue-300 hover:bg-blue-50"}`}
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

                <div className="flex items-center justify-between gap-3 mt-4">
                  <button
                    type="button"
                    onClick={() => clearModuleSchedule(editingModuleCode)}
                    className="text-xs font-semibold px-3 py-1.5 rounded-lg border border-red-200 text-red-600 bg-red-50 hover:bg-red-100"
                  >
                    Xóa toàn bộ thời gian môn này
                  </button>
                  <button
                    type="button"
                    onClick={() => setEditingModuleCode(null)}
                    className="text-xs font-semibold px-3 py-1.5 rounded-lg border border-blue-300 text-blue-700 bg-blue-50 hover:bg-blue-100"
                  >
                    Xong
                  </button>
                </div>
              </div>
            </div>
          )}

          <div className="text-xs text-gray-400">
            Tổng số môn hiện tại: {subjects.length}
          </div>
        </>
      )}
    </div>
  );
}
