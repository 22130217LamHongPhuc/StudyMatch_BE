import { Card, CardContent, Chip, Stack, Typography } from "@mui/material";
import { ProfileViewModel } from "../types";

interface SubjectsCardProps {
  profile: ProfileViewModel;
}

export default function SubjectsCard({ profile }: SubjectsCardProps) {
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
            Môn học
          </Typography>

          <Stack spacing={0.5}>
            <Typography variant="caption" color="text.secondary">
              Môn chính
            </Typography>
            <Typography
              variant="body1"
              sx={{ fontWeight: 600, color: "#1F2A44" }}
            >
              {profile.mainSubjectName}
            </Typography>
          </Stack>

          <Stack spacing={1}>
            <Typography variant="caption" color="text.secondary">
              Môn đang học
            </Typography>
            <Stack direction="row" spacing={1} useFlexGap flexWrap="wrap">
              {profile.enrolledSubjects.map((subject) => (
                <Chip
                  key={subject.subjectId}
                  label={`${subject.subjectCode} - ${subject.subjectName}`}
                  variant="outlined"
                  sx={{
                    borderColor: "#D6E2F7",
                    bgcolor: "#F8FBFF",
                    borderRadius: 1.75,
                  }}
                />
              ))}
            </Stack>
          </Stack>
        </Stack>
      </CardContent>
    </Card>
  );
}
