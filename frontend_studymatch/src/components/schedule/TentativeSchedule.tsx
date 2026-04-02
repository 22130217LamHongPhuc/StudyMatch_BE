import React from 'react'
import CalendarTodayOutlinedIcon from "@mui/icons-material/CalendarTodayOutlined";
import KeyboardArrowDownRoundedIcon from "@mui/icons-material/KeyboardArrowDownRounded";
import NoSchedule from "../../components/schedule/NoSchedule";
import ArrowBackIcon from "@mui/icons-material/ArrowBack";
import ArrowForwardIcon from "@mui/icons-material/ArrowForward";
import AddRoundedIcon from "@mui/icons-material/AddRounded";
import AccessTimeOutlinedIcon from "@mui/icons-material/AccessTimeOutlined";
import { Button, IconButton, Menu, MenuItem, Table, TableBody, TableCell, TableContainer, TableHead, TableRow, Typography } from "@mui/material";
import { Box, Stack, textAlign } from "@mui/system";
export default function TentativeSchedule() {
    const styleCellOuter = { py: 1, textAlign: 'center', backgroundColor: 'rgb(142, 193, 252)', color: '#fff', mb: 0.1 }
    const styleTableHead = { textAlign: 'center' }
    const styleRowCell = { p: 0 }

    return (
        <div>

            <TableContainer>
                <Table sx={{ tableLayout: "fixed", width: "100%" }}>
                    <TableHead>
                        <TableRow >
                            <TableCell sx={styleTableHead}>
                                <IconButton size="small">
                                    <ArrowBackIcon sx={{ color: "#14233c" }} />
                                </IconButton>
                            </TableCell>
                            <TableCell>
                                <Typography sx={styleTableHead}>
                                    Thứ 2
                                </Typography>
                            </TableCell>
                            <TableCell sx={styleTableHead}>
                                <Typography>
                                    Thứ 3
                                </Typography>
                            </TableCell>
                            <TableCell sx={styleTableHead}>
                                <Typography>
                                    Thứ 4
                                </Typography>
                            </TableCell>
                            <TableCell sx={styleTableHead}>
                                <Typography>
                                    Thứ 5
                                </Typography>
                            </TableCell>
                            <TableCell sx={styleTableHead}>
                                <Typography>
                                    Thứ 6
                                </Typography>
                            </TableCell>
                            <TableCell sx={styleTableHead}>
                                <Typography>
                                    Thứ 7
                                </Typography>
                            </TableCell>
                            <TableCell sx={styleTableHead}>
                                <Typography>
                                    Chủ nhật
                                </Typography>
                            </TableCell>
                            <TableCell sx={styleTableHead}>
                                <IconButton size="small">
                                    <ArrowForwardIcon sx={{ color: "#14233c" }} />
                                </IconButton>
                            </TableCell>
                        </TableRow>
                    </TableHead>
                    <TableBody>
                        <TableRow sx={{}}>
                            <TableCell sx={styleRowCell}>
                                <Box sx={styleCellOuter}>
                                    Tiết 1
                                </Box>
                                <Box sx={styleCellOuter}>
                                    Tiết 2
                                </Box>
                                <Box sx={styleCellOuter}>
                                    Tiết 3
                                </Box>
                            </TableCell>

                            <TableCell sx={styleRowCell}>
                            </TableCell>
                            <TableCell sx={{ p: 0 }}>
                                <Box
                                    sx={{
                                        display: 'flex',
                                        flexDirection: 'column',
                                        justifyContent: 'space-between',
                                        height: '120px',
                                        p: 1,
                                        backgroundColor: 'rgb(207, 226, 255)',
                                        border: '2px solid rgb(55, 189, 116)'
                                    }}
                                >
                                    <Box sx={{ fontSize: 15, fontWeight: 600 }}>
                                        Cấu trúc dữ liệu
                                    </Box>

                                    <Box>(214483)</Box>

                                    <Box sx={{ display: 'flex', alignItems: 'center', gap: 0.5 }}>
                                        <AccessTimeOutlinedIcon sx={{ fontSize: 17 }} />
                                        <Typography sx={{ fontSize: 12 }}>
                                            07:00 → 8:40
                                        </Typography>
                                    </Box>
                                </Box>
                            </TableCell>
                            <TableCell
                                sx={{
                                    ...styleRowCell,
                                    p: 0,
                                }}
                            >
                                <Box
                                    sx={{
                                        display: 'flex',
                                        flexDirection: 'column',
                                        justifyContent: 'space-between',
                                        height: 120,
                                        p: 1,
                                        backgroundColor: 'rgb(248, 215, 218)',
                                        border: '2px solid rgb(232, 40, 107)',
                                        boxSizing: 'border-box'
                                    }}
                                >
                                    <Box sx={{ fontSize: 15, fontWeight: 600, color: 'black' }}>
                                        Thương mại điện tử
                                    </Box>

                                    <Box>(214483)</Box>

                                    <Box sx={{ display: "flex", alignItems: "center", gap: 0.5 }}>
                                        <AccessTimeOutlinedIcon sx={{ fontSize: 17, color: "#000" }} />
                                        <Typography sx={{ fontSize: 12 }}>
                                            07:00 → 8:40
                                        </Typography>
                                    </Box>
                                </Box>
                            </TableCell>
                            <TableCell sx={styleRowCell}></TableCell>
                            <TableCell sx={styleRowCell}></TableCell>
                            <TableCell sx={styleRowCell}></TableCell>
                            <TableCell sx={styleRowCell}></TableCell>
                            <TableCell sx={styleRowCell}>
                                <Box sx={styleCellOuter}>
                                    7:00
                                </Box>
                                <Box sx={styleCellOuter}>
                                    7:50
                                </Box>
                                <Box sx={styleCellOuter}>
                                    8:40
                                </Box>
                            </TableCell>
                        </TableRow>
                        {/* ca 2 */}
                        <TableRow sx={{}}>
                            <TableCell sx={styleRowCell}>
                                <Box sx={styleCellOuter}>
                                    Tiết 4
                                </Box>
                                <Box sx={styleCellOuter}>
                                    Tiết 5
                                </Box>
                                <Box sx={styleCellOuter}>
                                    Tiết 6
                                </Box>
                            </TableCell>

                            <TableCell sx={styleRowCell}></TableCell>
                            <TableCell sx={styleRowCell}></TableCell>
                            <TableCell sx={styleRowCell}></TableCell>
                            <TableCell sx={styleRowCell}></TableCell>
                            <TableCell sx={styleRowCell}></TableCell>
                            <TableCell sx={styleRowCell}></TableCell>
                            <TableCell sx={styleRowCell}></TableCell>
                            <TableCell sx={styleRowCell}>
                                <Box sx={styleCellOuter}>
                                    9:35
                                </Box>
                                <Box sx={styleCellOuter}>
                                    10:25
                                </Box>
                                <Box sx={styleCellOuter}>
                                    11:15
                                </Box>
                            </TableCell>
                        </TableRow>
                        {/* ca 3 */}

                        <TableRow sx={{}}>
                            <TableCell sx={styleRowCell}>
                                <Box sx={styleCellOuter}>
                                    Tiết 7
                                </Box>
                                <Box sx={styleCellOuter}>
                                    Tiết 8
                                </Box>
                                <Box sx={styleCellOuter}>
                                    Tiết 9
                                </Box>
                            </TableCell>

                            <TableCell sx={styleRowCell}></TableCell>
                            <TableCell sx={styleRowCell}></TableCell>
                            <TableCell sx={styleRowCell}></TableCell>
                            <TableCell sx={styleRowCell}></TableCell>
                            <TableCell sx={styleRowCell}></TableCell>
                            <TableCell sx={styleRowCell}></TableCell>
                            <TableCell sx={styleRowCell}></TableCell>
                            <TableCell sx={styleRowCell}>
                                <Box sx={styleCellOuter}>
                                    12:15
                                </Box>
                                <Box sx={styleCellOuter}>
                                    13:05
                                </Box>
                                <Box sx={styleCellOuter}>
                                    13:55
                                </Box>
                            </TableCell>
                        </TableRow>
                        {/* ca 4
                         */}
                        <TableRow sx={{}}>
                            <TableCell sx={styleRowCell}>
                                <Box sx={styleCellOuter}>
                                    Tiết 10
                                </Box>
                                <Box sx={styleCellOuter}>
                                    Tiết 11
                                </Box>
                                <Box sx={styleCellOuter}>
                                    Tiết 12
                                </Box>
                            </TableCell>

                            <TableCell sx={styleRowCell}></TableCell>
                            <TableCell sx={styleRowCell}></TableCell>
                            <TableCell sx={styleRowCell}></TableCell>
                            <TableCell sx={styleRowCell}></TableCell>
                            <TableCell sx={styleRowCell}></TableCell>
                            <TableCell sx={styleRowCell}></TableCell>
                            <TableCell sx={styleRowCell}></TableCell>
                            <TableCell sx={styleRowCell}>
                                <Box sx={styleCellOuter}>
                                    14:50
                                </Box>
                                <Box sx={styleCellOuter}>
                                    15:40
                                </Box>
                                <Box sx={styleCellOuter}>
                                    16:30
                                </Box>
                            </TableCell>
                        </TableRow>
                        {/* ca 5 */}
                        <TableRow sx={{}}>
                            <TableCell sx={styleRowCell}>
                                <Box sx={styleCellOuter}>
                                    Tiết 13
                                </Box>
                                <Box sx={styleCellOuter}>
                                    Tiết 14
                                </Box>
                                <Box sx={styleCellOuter}>
                                    Tiết 15
                                </Box>
                            </TableCell>

                            <TableCell sx={styleRowCell}></TableCell>
                            <TableCell sx={styleRowCell}></TableCell>
                            <TableCell sx={styleRowCell}></TableCell>
                            <TableCell sx={styleRowCell}></TableCell>
                            <TableCell sx={styleRowCell}></TableCell>
                            <TableCell sx={styleRowCell}></TableCell>
                            <TableCell sx={styleRowCell}></TableCell>
                            <TableCell sx={styleRowCell}>
                                <Box sx={styleCellOuter}>
                                    17:30
                                </Box>
                                <Box sx={styleCellOuter}>
                                    18:20
                                </Box>
                                <Box sx={styleCellOuter}>
                                    19:20
                                </Box>
                            </TableCell>
                        </TableRow>
                    </TableBody>
                </Table>
            </TableContainer>

        </div>
    )
}
