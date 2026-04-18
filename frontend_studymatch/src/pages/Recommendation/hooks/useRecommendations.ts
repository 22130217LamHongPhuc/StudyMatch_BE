import { useCallback, useEffect, useState } from "react";
import { STUDY_MODE_LABELS } from "../constants";
import { getRecommendedUsers } from "../../../services/RecommendationService";
import { RecommendationApiItem, RecommendationCardVm } from "../types";

function mapToViewModel(item: RecommendationApiItem): RecommendationCardVm {
  return {
    userId: item.user_id,
    studyGoal: item.study_goal,
    studyModeLabel: STUDY_MODE_LABELS[item.study_mode] ?? item.study_mode,
    avgScore: item.avg_score,
    studiedCredits: item.studied_credits,
    gender: item.gender,
    region: item.region,
    similarityScore: item.similarity_score,
    sharedSubjectScore: item.shared_subject_score,
    sharedSubjectCount: item.n_shared_subjects,
    matchPercentage: item.match_percentage,
  };
}

export function useRecommendations(initialUserId = 28) {
  const [userId, setUserId] = useState<number>(initialUserId);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);
  const [message, setMessage] = useState<string>("");
  const [items, setItems] = useState<RecommendationCardVm[]>([]);

  const fetchRecommendations = useCallback(
    async (targetUserId?: number) => {
      const nextUserId = targetUserId ?? userId;
      setLoading(true);
      setError(null);

      try {
        const response = await getRecommendedUsers({ user_id: nextUserId });

        if (!response.success) {
          throw new Error(response.message || "Không thể tạo danh sách gợi ý.");
        }

        const mapped = response.recommendations.map(mapToViewModel);
        mapped.sort((a, b) => b.matchPercentage - a.matchPercentage);

        setItems(mapped);
        setMessage(response.message);
        setUserId(nextUserId);
      } catch (err) {
        const errorMessage =
          err instanceof Error ? err.message : "Đã có lỗi xảy ra.";
        setError(errorMessage);
        setItems([]);
      } finally {
        setLoading(false);
      }
    },
    [userId],
  );

  useEffect(() => {
    fetchRecommendations(initialUserId);
  }, [fetchRecommendations, initialUserId]);

  return {
    userId,
    loading,
    error,
    message,
    items,
    setUserId,
    fetchRecommendations,
  };
}
