import { useEffect, useMemo, useState } from "react";
import { getProfileByUserId } from "../../../services/ProfileService";
import { mapProfileResponseToVm } from "../mappers/profileMapper";
import { mockProfileData } from "../mockProfileData";
import { ProfileApiResponse, ProfileViewModel } from "../types";

interface UseProfileDataResult {
  profileData: ProfileApiResponse | null;
  profileVm: ProfileViewModel | null;
  loading: boolean;
  error: string | null;
  usingMockData: boolean;
}

export function useProfileData(userId: number): UseProfileDataResult {
  const [profileData, setProfileData] = useState<ProfileApiResponse | null>(
    null,
  );
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);
  const [usingMockData, setUsingMockData] = useState<boolean>(false);

  useEffect(() => {
    let isMounted = true;

    async function loadProfile() {
      setLoading(true);
      setError(null);
      setUsingMockData(false);

      try {
        const data = await getProfileByUserId(userId);
        if (!isMounted) return;
        setProfileData(data);
      } catch (err) {
        // if (!isMounted) return;
        // const message = err instanceof Error ? err.message : "Khong tai duoc du lieu profile";
        // setError(message);
        // setProfileData(mockProfileData);
        // setUsingMockData(true);
      } finally {
        if (isMounted) {
          setLoading(false);
        }
      }
    }

    loadProfile();

    return () => {
      isMounted = false;
    };
  }, [userId]);

  const profileVm = useMemo(() => {
    if (!profileData) return null;
    return mapProfileResponseToVm(profileData);
  }, [profileData]);

  return {
    profileData,
    profileVm,
    loading,
    error,
    usingMockData,
  };
}
