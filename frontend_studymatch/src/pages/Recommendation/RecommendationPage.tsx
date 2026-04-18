import {
  Alert,
  Box,
  Button,
  CircularProgress,
  Stack,
  TextField,
  Typography,
} from "@mui/material";
import { FormEvent, useState } from "react";
import RecommendationCard from "./components/RecommendationCard";
import { useRecommendations } from "./hooks/useRecommendations";

export default function RecommendationPage() {
  const { userId, loading, error, message, items, fetchRecommendations } =
    useRecommendations(28);
  const [userIdInput, setUserIdInput] = useState<string>(String(userId));

  const handleSubmit = async (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    const parsed = Number(userIdInput);

    if (!Number.isFinite(parsed) || parsed <= 0) {
      return;
    }

    await fetchRecommendations(parsed);
  };

  return (
    <Box
      sx={{
        minHeight: "100%",
        py: { xs: 2, md: 4 },
        px: { xs: 1.5, md: 2 },
        background:
          "radial-gradient(circle at 85% 0%, #E8F7F7 0%, #F2F9FF 35%, #F8FAFF 100%)",
      }}
    >
      <Box sx={{ width: { xs: "100%", md: "78%", xl: "74%" }, mx: "auto" }}>
        <Stack spacing={2.5}>
          <Box
            sx={{
              p: { xs: 2, md: 2.5 },
              borderRadius: 3,
              background: "linear-gradient(125deg, #FFFFFF 0%, #F3FAFF 100%)",
              border: "1px solid #DEE9F8",
            }}
          >
            <Stack
              direction={{ xs: "column", md: "row" }}
              justifyContent="space-between"
              spacing={2}
              alignItems={{ xs: "flex-start", md: "center" }}
            >
              <Box>
                <Typography
                  variant="h5"
                  sx={{ fontWeight: 700, color: "#1F2A44" }}
                >
                  Gợi ý bạn học phù hợp
                </Typography>
                <Typography variant="body2" sx={{ mt: 0.5, color: "#5D6A85" }}>
                  Danh sách được đề xuất dựa trên hồ sơ học tập hiện tại.
                </Typography>
              </Box>

              {/* <Box component="form" onSubmit={handleSubmit}>
                <Stack direction="row" spacing={1.2}>
                  <TextField
                    size="small"
                    label="User ID"
                    value={userIdInput}
                    onChange={(event) => setUserIdInput(event.target.value)}
                    sx={{ minWidth: 120, bgcolor: "#FFFFFF" }}
                  />
                  <Button type="submit" variant="contained" disabled={loading}>
                    Tải gợi ý
                  </Button>
                </Stack>
              </Box> */}
            </Stack>
          </Box>

          {/* {message && !error && (
            <Alert severity="success" sx={{ borderRadius: 2 }}>
              {message}
            </Alert>
          )} */}

          {error && (
            <Alert
              severity="error"
              sx={{ borderRadius: 2 }}
              action={
                <Button
                  color="inherit"
                  size="small"
                  onClick={() => fetchRecommendations(userId)}
                >
                  Thử lại
                </Button>
              }
            >
              {error}
            </Alert>
          )}

          <Box>
            <Typography variant="body2" sx={{ color: "#5D6A85", mb: 1 }}>
              Tìm thấy {items.length} kết quả phù hợp.
            </Typography>

            {loading ? (
              <Box sx={{ py: 8, textAlign: "center" }}>
                <CircularProgress />
              </Box>
            ) : items.length === 0 ? (
              <Alert severity="info" sx={{ borderRadius: 2 }}>
                Chưa có dữ liệu gợi ý cho user này.
              </Alert>
            ) : (
              <Box
                sx={{
                  display: "grid",
                  gridTemplateColumns: { xs: "1fr", lg: "1fr 1fr" },
                  gap: 2,
                }}
              >
                {items.map((item) => (
                  <RecommendationCard
                    key={item.userId}
                    recommendation={item}
                    onConnect={() => {}}
                    onReject={() => {}}
                  />
                ))}
              </Box>
            )}
          </Box>
        </Stack>
      </Box>
    </Box>
  );
}
