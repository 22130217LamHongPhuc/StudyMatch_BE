import { PROFILE_DAYS, PROFILE_SLOTS, STUDY_MODE_LABELS } from "../constants";
import {
  DayId,
  FreeTimeGroupVm,
  ProfileApiResponse,
  ProfileViewModel,
  ScheduleCellVm,
  ScheduleClassVm,
  ScheduleRowVm,
  SlotId,
} from "../types";

function formatDateTime(value: string): string {
  const date = new Date(value);
  if (Number.isNaN(date.getTime())) return value;
  return date.toLocaleString("vi-VN", {
    hour12: false,
    year: "numeric",
    month: "2-digit",
    day: "2-digit",
    hour: "2-digit",
    minute: "2-digit",
  });
}

function buildScheduleMap(data: ProfileApiResponse): Map<string, ScheduleClassVm[]> {
  const map = new Map<string, ScheduleClassVm[]>();

  data.scheduleSlots.forEach((slot) => {
    const key = `${slot.dayOfWeek}-${slot.slotCode}`;
    const currentItems = map.get(key) || [];
    currentItems.push({
      id: slot.id,
      subjectCode: slot.subject.subjectCode,
      subjectName: slot.subject.subjectName,
      scheduleType: slot.scheduleType,
    });
    currentItems.sort((a, b) => {
      if (a.scheduleType === b.scheduleType) return a.subjectCode.localeCompare(b.subjectCode);
      return a.scheduleType === "MAIN_SUBJECT" ? -1 : 1;
    });
    map.set(key, currentItems);
  });

  return map;
}

function buildFreeTimeSet(data: ProfileApiResponse): Set<string> {
  const set = new Set<string>();
  data.freeTimeSlots.forEach((slot) => {
    if (slot.isAvailable) {
      set.add(`${slot.dayOfWeek}-${slot.slotCode}`);
    }
  });
  return set;
}

function toScheduleRows(data: ProfileApiResponse): ScheduleRowVm[] {
  const scheduleMap = buildScheduleMap(data);
  const freeSet = buildFreeTimeSet(data);

  return PROFILE_SLOTS.map((slot) => {
    const cells: ScheduleCellVm[] = PROFILE_DAYS.map((day) => {
      const key = `${day.id}-${slot.id}`;
      return {
        dayId: day.id,
        classes: scheduleMap.get(key) || [],
        isFree: freeSet.has(key),
      };
    });

    return { slot, cells };
  });
}

function toFreeTimeGroups(data: ProfileApiResponse): FreeTimeGroupVm[] {
  const byDay = new Map<DayId, Set<SlotId>>();

  data.freeTimeSlots.forEach((slot) => {
    if (!slot.isAvailable) return;
    const slots = byDay.get(slot.dayOfWeek) || new Set<SlotId>();
    slots.add(slot.slotCode);
    byDay.set(slot.dayOfWeek, slots);
  });

  return PROFILE_DAYS.map((day) => {
    const slotSet = byDay.get(day.id) || new Set<SlotId>();
    const slots = PROFILE_SLOTS.filter((slot) => slotSet.has(slot.id));
    return {
      dayId: day.id,
      dayLabel: day.label,
      slots,
    };
  }).filter((item) => item.slots.length > 0);
}

export function mapProfileResponseToVm(data: ProfileApiResponse): ProfileViewModel {
  const termProfile = data.termProfiles[0];

  return {
    fullName: data.profile.fullName,
    studentCode: data.profile.studentCode,
    gender: data.profile.gender,
    ageGroup: data.profile.ageGroup,
    region: data.profile.region,
    cohortLabel: `Khoa ${data.profile.cohort.cohortCode} (${data.profile.cohort.startAcademicYear})`,
    curriculumName: data.profile.cohort.curriculum.curriculumName,
    createdAtLabel: formatDateTime(data.profile.createdAt),
    termLabel: termProfile?.term.fullName || "Chua co hoc ky",
    studyYearNo: termProfile?.studyYearNo || 0,
    semesterNo: termProfile?.semesterNo || 0,
    avgScore: termProfile?.avgScore || 0,
    studiedCredits: termProfile?.studiedCredits || 0,
    studyGoal: termProfile?.studyGoal || "-",
    studyModeLabel: STUDY_MODE_LABELS[termProfile?.studyMode || ""] || termProfile?.studyMode || "-",
    mainSubjectName: termProfile?.mainSubjectName || "-",
    enrolledSubjects: data.enrollments.map((item) => item.subject),
    freeTimeGroups: toFreeTimeGroups(data),
    scheduleRows: toScheduleRows(data),
    dayHeaders: PROFILE_DAYS,
  };
}

