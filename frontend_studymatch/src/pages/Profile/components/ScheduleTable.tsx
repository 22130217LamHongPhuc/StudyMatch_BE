import {
  Card,
  CardContent,
  Chip,
  Stack,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Typography,
} from "@mui/material";
import { ProfileViewModel, ScheduleClassVm } from "../types";

interface ScheduleTableProps {
  profile: ProfileViewModel;
}

function classStyle(item: ScheduleClassVm) {
  if (item.scheduleType === "MAIN_SUBJECT") {
    return { bgcolor: "#EEF0FF", color: "#4450C7" };
  }
  return { bgcolor: "#FFF5EA", color: "#A35E10" };
}

export default function ScheduleTable({ profile }: ScheduleTableProps) {
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
            Thời khóa biểu
          </Typography>

          <TableContainer sx={{ borderRadius: 2, border: "1px solid #E8EDF8" }}>
            <Table size="small">
              <TableHead>
                <TableRow sx={{ bgcolor: "#F4F8FF" }}>
                  <TableCell
                    sx={{ minWidth: 130, fontWeight: 700, color: "#223150" }}
                  >
                    Khung giờ
                  </TableCell>
                  {profile.dayHeaders.map((day) => (
                    <TableCell
                      key={day.id}
                      align="center"
                      sx={{ fontWeight: 700, minWidth: 140, color: "#223150" }}
                    >
                      {day.short}
                    </TableCell>
                  ))}
                </TableRow>
              </TableHead>

              <TableBody>
                {profile.scheduleRows.map((row) => (
                  <TableRow key={row.slot.id}>
                    <TableCell>
                      <Stack spacing={0.25}>
                        <Typography variant="body2" sx={{ fontWeight: 600 }}>
                          {row.slot.label}
                        </Typography>
                        <Typography variant="caption" color="text.secondary">
                          {row.slot.time}
                        </Typography>
                      </Stack>
                    </TableCell>

                    {row.cells.map((cell) => (
                      <TableCell key={`${row.slot.id}-${cell.dayId}`}>
                        <Stack
                          direction="row"
                          spacing={0.75}
                          useFlexGap
                          flexWrap="wrap"
                          justifyContent="center"
                        >
                          {cell.classes.map((item) => (
                            <Chip
                              key={item.id}
                              size="small"
                              label={`${item.subjectCode}`}
                              title={item.subjectName}
                              sx={classStyle(item)}
                            />
                          ))}
                          {cell.isFree && (
                            <Chip
                              size="small"
                              label="Rảnh"
                              sx={{ bgcolor: "#EAF8EE", color: "#1E7B34" }}
                            />
                          )}
                          {cell.classes.length === 0 && !cell.isFree && (
                            <Typography variant="caption" color="text.disabled">
                              -
                            </Typography>
                          )}
                        </Stack>
                      </TableCell>
                    ))}
                  </TableRow>
                ))}
              </TableBody>
            </Table>
          </TableContainer>
        </Stack>
      </CardContent>
    </Card>
  );
}
