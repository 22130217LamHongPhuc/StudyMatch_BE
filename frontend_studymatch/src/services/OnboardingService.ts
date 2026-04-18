import {
  FormData,
  Cohort,
  StudyPlan,
  StudyGoal,
  StudyMode,
  DayId,
  SlotId,
} from "../pages/Onboarding/components/types";

const API_BASE_URL = "http://localhost:8081/api";
const API_BASE_URL2 = "http://localhost:8082/api";

export interface SubjectScheduleSlot {
  subjectId: number | string;
  dayOfWeek: DayId;
  slotCode: SlotId;
  scheduleType: "MAIN_SUBJECT" | "CURRENT_TERM";
}

export interface FreeTimeSlot {
  dayOfWeek: DayId;
  slotCode: SlotId;
}

export interface OnboardingSubmissionPayload {
  studentCode: string;
  fullName: string;
  gender: "male" | "female" | "";
  ageGroup: string;
  region: string;
  cohortId: number | string;
  termId?: number | string;
  studyYearNo?: number;
  semesterNo?: number;
  avgScore: number;
  studiedCredits: number;
  studyGoal: string;
  studyMode: string;
  mainSubjectId: number | string;
  currentSubjectIds: (number | string)[];
  freeTimeSlots: FreeTimeSlot[];
  subjectScheduleSlots: SubjectScheduleSlot[];
}

const genderMap: Record<string, "male" | "female" | ""> = {
  M: "male",
  F: "female",
  "": "",
};

const studyGoalMap: Record<StudyGoal, string> = {
  Survivor: "Survivor",
  "Passive Learner": "Passive Learner",
  "Standard Learner": "Standard Learner",
  "High Achiever": "High Achiever",
};

const studyModeMap: Record<StudyMode, string> = {
  mutual_support: "mutual_support",
  peer_support: "peer_support",
  challenge: "challenge",
  support: "support",
};

export function transformFormDataToPayload(
  formData: FormData,
  cohorts: Cohort[],
  studyPlan: StudyPlan | null,
  subjectCodeToIdMap: Record<string, number | string>,
): OnboardingSubmissionPayload {
  const cohort = cohorts.find(
    (c) => String(c.cohortCode) === String(formData.cohortCode),
  );
  const cohortId = cohort?.cohortId || "";

  const mainSubjectId = subjectCodeToIdMap[formData.mainModule] || "";

  const currentSubjectIds = formData.enrolledModules
    .map((code) => subjectCodeToIdMap[code])
    .filter(Boolean);

  const freeTimeSlots: FreeTimeSlot[] = [];
  Object.keys(formData.freeTime).forEach((dayStr) => {
    const dayId = parseInt(dayStr) as DayId;
    const daySlots = formData.freeTime[dayId];
    Object.keys(daySlots).forEach((slotCode) => {
      if (daySlots[slotCode as SlotId]) {
        freeTimeSlots.push({
          dayOfWeek: dayId,
          slotCode: slotCode as SlotId,
        });
      }
    });
  });

  const subjectScheduleSlots: SubjectScheduleSlot[] = [];

  if (formData.moduleSlots[formData.mainModule]) {
    Object.keys(formData.moduleSlots[formData.mainModule]).forEach((dayStr) => {
      const dayId = parseInt(dayStr) as DayId;
      const daySlots = formData.moduleSlots[formData.mainModule][dayId];
      Object.keys(daySlots).forEach((slotCode) => {
        if (daySlots[slotCode as SlotId]) {
          subjectScheduleSlots.push({
            subjectId: mainSubjectId,
            dayOfWeek: dayId,
            slotCode: slotCode as SlotId,
            scheduleType: "MAIN_SUBJECT",
          });
        }
      });
    });
  }

  formData.enrolledModules.forEach((moduleCode) => {
    if (formData.moduleSlots[moduleCode]) {
      Object.keys(formData.moduleSlots[moduleCode]).forEach((dayStr) => {
        const dayId = parseInt(dayStr) as DayId;
        const daySlots = formData.moduleSlots[moduleCode][dayId];
        Object.keys(daySlots).forEach((slotCode) => {
          if (daySlots[slotCode as SlotId]) {
            subjectScheduleSlots.push({
              subjectId: subjectCodeToIdMap[moduleCode],
              dayOfWeek: dayId,
              slotCode: slotCode as SlotId,
              scheduleType: "CURRENT_TERM",
            });
          }
        });
      });
    }
  });

  return {
    studentCode: formData.studentId,
    fullName: formData.fullName,
    gender: genderMap[formData.gender] || "",
    ageGroup: formData.ageGroup,
    region: formData.region,
    cohortId,
    termId: studyPlan?.termFullName
      ? parseInt(studyPlan.termFullName.match(/\d+/)?.[0] || "0")
      : undefined,
    studyYearNo: studyPlan?.studyYearNo,
    semesterNo: studyPlan?.semesterNo,
    avgScore: formData.avgScore,
    studiedCredits: parseInt(formData.studiedCredits) || 0,
    studyGoal:
      studyGoalMap[formData.studyGoal as StudyGoal] || formData.studyGoal,
    studyMode:
      studyModeMap[formData.studyMode as StudyMode] || formData.studyMode,
    mainSubjectId,
    currentSubjectIds,
    freeTimeSlots,
    subjectScheduleSlots,
  };
}

export async function submitOnboardingForm(
  payload: OnboardingSubmissionPayload,
): Promise<{ success: boolean; data?: unknown; error?: string }> {
  try {
    const response = await fetch(`${API_BASE_URL2}/onboarding/submit`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify(payload),
    });

    if (!response.ok) {
      const errorData = await response.json().catch(() => ({}));
      throw new Error(
        errorData.message ||
          `API Error: ${response.status} ${response.statusText}`,
      );
    }

    const data = await response.json();
    console.log("Onboarding form submitted with payload:", payload);
    console.log("API response:", data);
    return {
      success: true,
      data: { message: "Form submitted successfully (mocked response)" },
    };
  } catch (error) {
    const message =
      error instanceof Error
        ? error.message
        : "Lỗi không xác định khi gửi dữ liệu";

    console.error("Onboarding submission error:", message);
    return { success: false, error: message };
  }
}

export function createSubjectCodeToIdMap(
  studyPlans: Array<StudyPlan | null>,
): Record<string, number | string> {
  const map: Record<string, number | string> = {};
  studyPlans.forEach((studyPlan) => {
    if (!studyPlan?.subjects) return;
    studyPlan.subjects.forEach((subject) => {
      map[subject.subjectCode] = subject.subjectId;
    });
  });
  return map;
}
