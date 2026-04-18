export type DayId = 0 | 1 | 2 | 3 | 4 | 5 | 6;
export type SlotId = "ca1" | "ca2" | "ca3" | "ca4" | "ca5" | "ca6";
export type ScheduleType = "MAIN_SUBJECT" | "CURRENT_TERM";

export interface CurriculumApi {
  curriculumId: number;
  curriculumCode: string;
  curriculumName: string;
}

export interface CohortApi {
  cohortId: number;
  cohortCode: string;
  startAcademicYear: number;
  totalStudyYears: number;
  curriculum: CurriculumApi;
}

export interface ProfileApi {
  profileId: number;
  userId: number;
  studentCode: string;
  fullName: string;
  gender: string;
  ageGroup: string;
  region: string;
  cohort: CohortApi;
  createdAt: string;
}

export interface TermApi {
  termId: number;
  academicYearStart: number;
  academicYearEnd: number;
  semesterNo: number;
  fullName: string;
  status: string;
}

export interface TermProfileApi {
  id: number;
  userId: number;
  term: TermApi;
  studyYearNo: number;
  semesterNo: number;
  avgScore: number;
  studiedCredits: number;
  studyGoal: string;
  studyMode: string;
  mainSubjectId: number;
  mainSubjectName: string;
}

export interface SubjectApi {
  subjectId: number;
  subjectCode: string;
  subjectName: string;
}

export interface EnrollmentApi {
  enrollmentId: number;
  subject: SubjectApi;
  term: TermApi;
}

export interface FreeTimeSlotApi {
  id: number;
  dayOfWeek: DayId;
  slotCode: SlotId;
  isAvailable: boolean;
}

export interface ScheduleSlotApi {
  id: number;
  subject: SubjectApi;
  dayOfWeek: DayId;
  slotCode: SlotId;
  scheduleType: ScheduleType;
  location: string | null;
  note: string | null;
}

export interface ProfileApiResponse {
  profile: ProfileApi;
  termProfiles: TermProfileApi[];
  enrollments: EnrollmentApi[];
  freeTimeSlots: FreeTimeSlotApi[];
  scheduleSlots: ScheduleSlotApi[];
  success: boolean;
  message: string;
}

export interface DayHeader {
  id: DayId;
  label: string;
  short: string;
}

export interface SlotMeta {
  id: SlotId;
  label: string;
  time: string;
}

export interface ScheduleClassVm {
  id: number;
  subjectCode: string;
  subjectName: string;
  scheduleType: ScheduleType;
}

export interface ScheduleCellVm {
  dayId: DayId;
  classes: ScheduleClassVm[];
  isFree: boolean;
}

export interface ScheduleRowVm {
  slot: SlotMeta;
  cells: ScheduleCellVm[];
}

export interface FreeTimeGroupVm {
  dayId: DayId;
  dayLabel: string;
  slots: SlotMeta[];
}

export interface ProfileViewModel {
  fullName: string;
  studentCode: string;
  gender: string;
  ageGroup: string;
  region: string;
  cohortLabel: string;
  curriculumName: string;
  createdAtLabel: string;
  termLabel: string;
  studyYearNo: number;
  semesterNo: number;
  avgScore: number;
  studiedCredits: number;
  studyGoal: string;
  studyModeLabel: string;
  mainSubjectName: string;
  enrolledSubjects: SubjectApi[];
  freeTimeGroups: FreeTimeGroupVm[];
  scheduleRows: ScheduleRowVm[];
  dayHeaders: DayHeader[];
}

