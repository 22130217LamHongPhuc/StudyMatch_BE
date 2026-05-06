export type LearnerType =
  | "Survivor"
  | "Passive Learner"
  | "Standard Learner"
  | "High Achiever";

export type StudyPurpose =
  | "mutual_support"
  | "peer_support"
  | "support"
  | "challenge";

export type Visibility = "public" | "private";

export interface MemberSuggestion {
  id: number;
  name: string;
  description: string;
  avatarUrl: string;
  colorClass: string;
}
