import { Card, CardContent, Divider, Stack, Typography } from "@mui/material";
import { ProfileViewModel } from "../types";

interface AcademicInfoCardProps {
  profile: ProfileViewModel;
}

function MetricItem({
  label,
  value,
}: {
  label: string;
  value: string | number;
}) {
  return (
    <Stack spacing={0.25}>
      <Typography variant="caption" color="text.secondary">
        {label}
      </Typography>
      <Typography variant="body1" sx={{ fontWeight: 600, color: "#1F2A44" }}>
        {value}
      </Typography>
    </Stack>
  );
}

export default function AcademicInfoCard({ profile }: AcademicInfoCardProps) {
  return (
    <Card
      sx={{
        borderRadius: 3,
        border: "1px solid #E3EAF8",
        boxShadow: "0 8px 20px rgba(20, 38, 70, 0.06)",
      }}
    >
      <CardContent sx={{ p: { xs: 2, sm: 2.5 } }}>
        <Stack spacing={2}>
          <Typography
            variant="subtitle1"
            sx={{ fontWeight: 700, color: "#1F2A44" }}
          >
            Thông tin học tập
          </Typography>

          <MetricItem label="Học kỳ" value={profile.termLabel} />

          <Stack direction={{ xs: "column", sm: "row" }} spacing={2}>
            <MetricItem label="Năm học" value={`Năm ${profile.studyYearNo}`} />
            <MetricItem label="Học kỳ thứ" value={profile.semesterNo} />
          </Stack>

          <Divider />

          <Stack direction={{ xs: "column", sm: "row" }} spacing={2}>
            <MetricItem label="Điểm TB" value={profile.avgScore.toFixed(2)} />
            <MetricItem label="Tín chỉ đã học" value={profile.studiedCredits} />
          </Stack>

          <MetricItem label="Trình độ" value={profile.studyGoal} />
          <MetricItem label="Chế độ học" value={profile.studyModeLabel} />
        </Stack>
      </CardContent>
    </Card>
  );
}
