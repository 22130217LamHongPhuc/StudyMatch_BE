import { Alert, Box, CircularProgress, Stack, Typography } from "@mui/material";
import AcademicInfoCard from "./components/AcademicInfoCard";
import FreeTimeCard from "./components/FreeTimeCard";
import ProfileHeaderCard from "./components/ProfileHeaderCard";
import ScheduleTable from "./components/ScheduleTable";
import SubjectsCard from "./components/SubjectsCard";
import { useProfileData } from "./hooks/useProfileData";

const DEFAULT_USER_ID = 28;

export default function ProfilePage() {
  const { profileVm, loading, error, usingMockData } =
    useProfileData(DEFAULT_USER_ID);

  if (loading) {
    return (
      <Box sx={{ py: 10, textAlign: "center" }}>
        <CircularProgress />
      </Box>
    );
  }

  if (!profileVm) {
    return (
      <Alert severity="error" sx={{ m: 3 }}>
        Không có dữ liệu hồ sơ để hiển thị.
      </Alert>
    );
  }

  return (
    <Box
      sx={{
        minHeight: "80%",
        py: { xs: 2, md: 4 },
        px: { xs: 1.5, md: 2 },
        background:
          "radial-gradient(circle at 10% 0%, #EAF3FF 0%, #F5F8FF 38%, #F8FAFF 100%)",
      }}
    >
      <Box
        sx={{
          width: { xs: "100%", md: "60%", xl: "74%" },
          mx: "auto",
        }}
      >
        <Stack spacing={3}>
          <Box
            sx={{
              p: { xs: 2, md: 2.5 },
              borderRadius: 3,
              background: "linear-gradient(110deg, #FFFFFF 0%, #F6FAFF 100%)",
              border: "1px solid #E1E9F8",
            }}
          >
            <Typography variant="h5" sx={{ fontWeight: 700, color: "#1F2A44" }}>
              Hồ sơ học tập
            </Typography>
            <Typography variant="body2" sx={{ mt: 0.5, color: "#5D6A85" }}>
              Cập nhật lúc: {profileVm.createdAtLabel}
            </Typography>
          </Box>

          {/* {error && usingMockData && (
            <Alert severity="warning" sx={{ borderRadius: 2 }}>
              Không lấy được dữ liệu từ API ({error}). Đang hiển thị dữ liệu mẫu
              để bạn tiếp tục giao diện.
            </Alert>
          )} */}

          <ProfileHeaderCard profile={profileVm} />

          <Box
            sx={{
              display: "grid",
              gridTemplateColumns: { xs: "1fr", lg: "1fr 1fr" },
              gap: 2.5,
              alignItems: "stretch",
            }}
          >
            <AcademicInfoCard profile={profileVm} />
            <SubjectsCard profile={profileVm} />
          </Box>

          <FreeTimeCard profile={profileVm} />
          <ScheduleTable profile={profileVm} />
        </Stack>
      </Box>
    </Box>
  );
}
