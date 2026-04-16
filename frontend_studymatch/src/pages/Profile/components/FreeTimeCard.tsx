import { Card, CardContent, Chip, Stack, Typography } from "@mui/material";
import { ProfileViewModel } from "../types";

interface FreeTimeCardProps {
  profile: ProfileViewModel;
}

export default function FreeTimeCard({ profile }: FreeTimeCardProps) {
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
            Khung giờ rảnh
          </Typography>

          {profile.freeTimeGroups.length === 0 ? (
            <Typography variant="body2" color="text.secondary">
              Chưa có dữ liệu thời gian rảnh.
            </Typography>
          ) : (
            profile.freeTimeGroups.map((group) => (
              <Stack key={group.dayId} spacing={0.75}>
                <Typography
                  variant="body2"
                  sx={{ fontWeight: 600, color: "#1F2A44" }}
                >
                  {group.dayLabel}
                </Typography>
                <Stack direction="row" spacing={1} useFlexGap flexWrap="wrap">
                  {group.slots.map((slot) => (
                    <Chip
                      key={slot.id}
                      label={`${slot.label} (${slot.time})`}
                      size="small"
                      sx={{
                        bgcolor: "#EAF8EE",
                        color: "#1E7B34",
                        borderRadius: 1.75,
                      }}
                    />
                  ))}
                </Stack>
              </Stack>
            ))
          )}
        </Stack>
      </CardContent>
    </Card>
  );
}
