import { ProfileApiResponse } from "../pages/Profile/types";

const PROFILE_API_BASE_URL = "http://localhost:8082/api";

export async function getProfileByUserId(userId: number): Promise<ProfileApiResponse> {
  const response = await fetch(`${PROFILE_API_BASE_URL}/onboarding/profile/${userId}`);

  if (!response.ok) {
    const errorPayload = await response.json().catch(() => null);
    const message =
      errorPayload?.message ||
      `Khong tai duoc profile. HTTP ${response.status} ${response.statusText}`;
    throw new Error(message);
  }

  return (await response.json()) as ProfileApiResponse;
}

