import { RECOMMEND_USERS_URL } from "../pages/Recommendation/constants";
import {
  RecommendUserRequest,
  RecommendUsersApiResponse,
} from "../pages/Recommendation/types";

export async function getRecommendedUsers(
  payload: RecommendUserRequest,
): Promise<RecommendUsersApiResponse> {
  const response = await fetch(RECOMMEND_USERS_URL, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify(payload),
  });

  if (!response.ok) {
    const errorPayload = await response.json().catch(() => null);
    const message =
      errorPayload?.message ||
      `Không tải được danh sách gợi ý. HTTP ${response.status} ${response.statusText}`;
    throw new Error(message);
  }

  return (await response.json()) as RecommendUsersApiResponse;
}
