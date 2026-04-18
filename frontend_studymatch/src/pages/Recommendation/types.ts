export interface RecommendUserRequest {
  user_id: number;
}

export interface RecommendationApiItem {
  user_id: number;
  term_id: number;
  main_subject_id: number;
  study_goal: string;
  study_mode: string;
  avg_score: number;
  studied_credits: number;
  total_clicks: number;
  gender: string;
  region: string;
  similarity_score: number;
  mode_bonus: number;
  shared_subject_score: number;
  n_shared_subjects: number;
  shared_subject_ids: string[];
  final_score: number;
  match_percentage: number;
}

export interface RecommendUsersApiResponse {
  success: boolean;
  message: string;
  recommendations: RecommendationApiItem[];
}

export interface RecommendationCardVm {
  userId: number;
  studyGoal: string;
  studyModeLabel: string;
  avgScore: number;
  studiedCredits: number;
  gender: string;
  region: string;
  similarityScore: number;
  sharedSubjectScore: number;
  sharedSubjectCount: number;
  matchPercentage: number;
}

