import { Button, IconButton, Menu, MenuItem, Table, TableBody, TableCell, TableContainer, TableHead, TableRow, Typography } from "@mui/material";
import { Box, Stack, textAlign } from "@mui/system";
import React, { useState } from "react";
import CalendarTodayOutlinedIcon from "@mui/icons-material/CalendarTodayOutlined";
import KeyboardArrowDownRoundedIcon from "@mui/icons-material/KeyboardArrowDownRounded";
import NoSchedule from "../../components/schedule/NoSchedule";
import ArrowBackIcon from "@mui/icons-material/ArrowBack";
import ArrowForwardIcon from "@mui/icons-material/ArrowForward";
import AddRoundedIcon from "@mui/icons-material/AddRounded";
import AccessTimeOutlinedIcon from "@mui/icons-material/AccessTimeOutlined";
import TentativeSchedule from "../../components/schedule/TentativeSchedule";
export default function SchedulePage() {
    const [anchorWeek, setAnchorWeek] = useState<null | HTMLElement>(null);
    const [anchorSemester, setAnchorSemester] = useState<null | HTMLElement>(null);
    const styleCellOuter = { py: 1, textAlign: 'center', backgroundColor: 'rgb(142, 193, 252)', color: '#fff', mb: 0.1 }
    const styleTableHead = { textAlign: 'center' }
    const styleRowCell = { p: 0 }
    return (
        <div>
            <Box
                sx={{
                    width: "100%",
                    px: 3,
                    py: 1,
                    bgcolor: "#F8FAFF",
                    borderBottom: "1px solid #E8EEF8",
                    display: "flex",
                    alignItems: "center",
                    justifyContent: "space-between",
                }}
            >
                <Typography
                    sx={{
                        fontSize: 18,
                        fontWeight: 600,
                        color: "#1F2A44",
                    }}
                >
                    Thời khóa biểu
                </Typography>


                <Button
                    endIcon={<KeyboardArrowDownRoundedIcon />}
                    variant="outlined"
                    sx={{
                        height: 36,
                        px: 1.5,
                        minWidth: "auto",
                        borderRadius: "10px",
                        textTransform: "none",
                        fontSize: 14,
                        fontWeight: 500,
                        color: "#6B7280",
                        borderColor: "#E3E8F2",
                        bgcolor: "#FFFFFF",
                        "&:hover": {
                            borderColor: "#D7DEEA",
                            bgcolor: "#FAFCFF",
                        },
                    }}
                >
                    15/04 - 21/04
                </Button>
            </Box>
            <TentativeSchedule></TentativeSchedule>

        </div >
    );
}