import {
  Avatar,
  Card,
  CardContent,
  Chip,
  Stack,
  Typography,
} from "@mui/material";
import PersonOutlineIcon from "@mui/icons-material/PersonOutline";
import { ProfileViewModel } from "../types";

interface ProfileHeaderCardProps {
  profile: ProfileViewModel;
}

export default function ProfileHeaderCard({ profile }: ProfileHeaderCardProps) {
  return (
    <Card
      sx={{
        borderRadius: 3,
        border: "1px solid #DCE6F8",
        background: "linear-gradient(135deg, #FFFFFF 0%, #F2F8FF 100%)",
        boxShadow: "0 10px 28px rgba(31, 42, 68, 0.08)",
      }}
    >
      <CardContent sx={{ p: { xs: 2, sm: 2.5 } }}>
        <Stack
          direction={{ xs: "column", sm: "row" }}
          spacing={2.5}
          alignItems={{ xs: "flex-start", sm: "center" }}
        >
          <Avatar
            sx={{
              width: 64,
              height: 64,
              bgcolor: "#3791FA",
              boxShadow: "0 6px 18px rgba(55, 145, 250, 0.35)",
            }}
          >
            <PersonOutlineIcon />
          </Avatar>

          <Stack spacing={0.5} sx={{ flex: 1 }}>
            <Typography variant="h6" sx={{ fontWeight: 700, color: "#1F2A44" }}>
              {profile.fullName}
            </Typography>
            <Typography variant="body2" color="text.secondary">
              MSSV: {profile.studentCode}
            </Typography>
            <Typography variant="body2" color="text.secondary">
              Giới tính: {profile.gender} • Độ tuổi: {profile.ageGroup} • Khu
              vực: {profile.region}
            </Typography>
          </Stack>

          <Chip
            label={profile.cohortLabel}
            sx={{
              bgcolor: "#E6F0FF",
              color: "#275D9F",
              fontWeight: 600,
              border: "1px solid #CFE0FD",
            }}
          />
        </Stack>
      </CardContent>
    </Card>
  );
}
