import {
  Box,
  Card,
  CardContent,
  Chip,
  Divider,
  LinearProgress,
  Stack,
  Typography,
  Button,
} from "@mui/material";
import { RecommendationCardVm } from "../types";

interface RecommendationCardProps {
  recommendation: RecommendationCardVm;
}

interface RecommendationCardProps {
  recommendation: RecommendationCardVm;
  onConnect?: (id: number) => void;
  onReject?: (id: number) => void;
}

function getMatchColor(match: number) {
  if (match >= 70) {
    return {
      track: "#E7F8EE",
      bar: "#1F8F48",
      chipBg: "#E7F8EE",
      chipText: "#1F8F48",
    };
  }

  if (match >= 50) {
    return {
      track: "#ECF4FF",
      bar: "#2E75D8",
      chipBg: "#ECF4FF",
      chipText: "#245FB3",
    };
  }

  return {
    track: "#FFF3E8",
    bar: "#C5762A",
    chipBg: "#FFF3E8",
    chipText: "#9A5A1D",
  };
}

export default function RecommendationCard({
  recommendation,
  onConnect,
  onReject,
}: RecommendationCardProps) {
  const match = Number(recommendation.matchPercentage.toFixed(2));
  const colors = getMatchColor(match);

  return (
    <Card
      sx={{
        borderRadius: 4,
        border: "1px solid #DCE8FA",
        background: "linear-gradient(160deg, #FFFFFF 0%, #F7FBFF 100%)",
        boxShadow: "0 10px 26px rgba(20, 38, 70, 0.08)",
        height: "100%",
        transition: "transform 0.2s ease, box-shadow 0.2s ease",
        "&:hover": {
          transform: "translateY(-3px)",
          boxShadow: "0 14px 30px rgba(20, 38, 70, 0.12)",
        },
      }}
    >
      <CardContent sx={{ p: { xs: 2, sm: 2.75 } }}>
        <Stack spacing={2.2}>
          <Stack
            direction="row"
            justifyContent="space-between"
            alignItems="center"
          >
            <Box>
              <Typography
                variant="subtitle2"
                sx={{ color: "#60708D", fontWeight: 600, letterSpacing: 0.2 }}
              >
                Mức độ phù hợp
              </Typography>
              <Typography
                variant="h5"
                sx={{ fontWeight: 800, color: colors.chipText }}
              >
                {match}%
              </Typography>
            </Box>
            <Chip
              label={recommendation.studyModeLabel}
              sx={{
                bgcolor: colors.chipBg,
                color: colors.chipText,
                fontWeight: 700,
                border: "1px solid rgba(33, 94, 170, 0.16)",
                maxWidth: 220,
              }}
            />
          </Stack>

          <Box>
            <LinearProgress
              variant="determinate"
              value={Math.min(100, Math.max(0, match))}
              sx={{
                height: 8,
                borderRadius: 999,
                bgcolor: colors.track,
                "& .MuiLinearProgress-bar": {
                  borderRadius: 999,
                  bgcolor: colors.bar,
                },
              }}
            />
          </Box>

          <Divider sx={{ borderColor: "#E4ECFA" }} />

          <Stack direction="row" spacing={1} useFlexGap flexWrap="wrap">
            <Chip
              size="small"
              label={`Khu vực: ${recommendation.region}`}
              sx={{ bgcolor: "#F1F6FF", color: "#304666", fontWeight: 600 }}
            />
            <Chip
              size="small"
              label={`Giới tính: ${recommendation.gender}`}
              sx={{ bgcolor: "#F1F6FF", color: "#304666", fontWeight: 600 }}
            />
          </Stack>

          <Box
            sx={{
              display: "grid",
              gridTemplateColumns: {
                xs: "1fr",
                sm: "repeat(2, minmax(0, 1fr))",
              },
              gap: 1.25,
            }}
          >
            <Metric label="Trình độ" value={recommendation.studyGoal} />
            <Metric
              label="Điểm TB"
              value={recommendation.avgScore.toFixed(2)}
            />
            <Metric
              label="Tín chỉ đã học"
              value={recommendation.studiedCredits}
            />

            <Metric
              label="Điểm môn chung"
              value={`${(recommendation.sharedSubjectScore * 100).toFixed(0)}%`}
            />
          </Box>

          <Box
            sx={{
              borderRadius: 2.5,
              px: 1.5,
              py: 1.25,
              bgcolor: "#EEF5FF",
              border: "1px solid #D5E5FF",
            }}
          >
            <Typography
              variant="caption"
              sx={{ color: "#52627E", fontWeight: 600 }}
            >
              Môn học chung trong học kỳ
            </Typography>
            <Typography
              variant="h6"
              sx={{ color: "#204B8A", fontWeight: 800, mt: 0.25 }}
            >
              {recommendation.sharedSubjectCount} môn
            </Typography>
          </Box>

          <Box sx={{ display: "flex", gap: 1.5, mt: 1 }}>
            <Button
              variant="contained"
              fullWidth
              onClick={() => onConnect?.(recommendation.userId)}
              sx={{
                textTransform: "none",
                fontWeight: 700,
                borderRadius: 2,
                background: "linear-gradient(135deg, #1F8F48 0%, #28A745 100%)",
                boxShadow: "0 6px 14px rgba(31, 143, 72, 0.25)",
                "&:hover": {
                  background:
                    "linear-gradient(135deg, #18773A 0%, #23963E 100%)",
                },
              }}
            >
              Kết nối
            </Button>

            <Button
              variant="outlined"
              fullWidth
              onClick={() => onReject?.(recommendation.userId)}
              sx={{
                textTransform: "none",
                fontWeight: 700,
                borderRadius: 2,
                borderColor: "#E57373",
                color: "#D32F2F",
                "&:hover": {
                  borderColor: "#C62828",
                  backgroundColor: "#FFF3F3",
                },
              }}
            >
              Từ chối
            </Button>
          </Box>
        </Stack>
      </CardContent>
    </Card>
  );
}

function Metric({ label, value }: { label: string; value: string | number }) {
  return (
    <Box
      sx={{
        p: 1.25,
        borderRadius: 2,
        bgcolor: "#F7FAFF",
        border: "1px solid #E6EEFB",
      }}
    >
      <Typography variant="caption" color="text.secondary">
        {label}
      </Typography>
      <Typography
        variant="body2"
        sx={{ fontWeight: 700, color: "#24324F", mt: 0.25 }}
      >
        {value}
      </Typography>
    </Box>
  );
}
