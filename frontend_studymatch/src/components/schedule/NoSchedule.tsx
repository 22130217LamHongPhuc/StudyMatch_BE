import { Button, Typography } from '@mui/material'
import { Box } from '@mui/system'
import React from 'react'

import AddRoundedIcon from "@mui/icons-material/AddRounded";
export default function NoSchedule() {
    return (
        <div>
            <Box sx={{ width: '100%', display: 'flex', alignItems: 'center', flexDirection: 'column', }}>
                <Box
                    sx={{
                        fontSize: 32,
                        fontWeight: 700,
                        color: "#23395D",
                        lineHeight: 1.2,
                        marginTop: 10
                    }}
                >
                    Chào mừng đến với thời khóa biểu!
                </Box>
                <Box
                    sx={{
                        fontSize: 16,
                        color: "#6B7280",
                        lineHeight: 1.7,
                        maxWidth: 500,
                        marginTop: 5
                    }}
                >
                    Công cụ này giúp bạn quản lý lịch học và hoạt động theo tuần.
                    <br />
                    Hãy bắt đầu bằng cách nhập thời khóa biểu cho học kỳ này.
                </Box>
                <Box sx={{ marginTop: 5 }}>
                    <Button
                        startIcon={<AddRoundedIcon />}
                        sx={{
                            background: "linear-gradient(180deg, #66A8FF 0%, #4B8DF8 100%)", color: '#fff', py: 1, px: 1.5
                        }}>Nhập thời khóa biểu</Button>
                    <Button
                        sx={{
                            marginLeft: 5,
                            color: "#5E8FEF",
                            borderColor: "#CFE0FF",
                            py: 1, px: 1.5
                        }}
                    >Xem chính sách</Button>
                </Box>
                <Box
                    component='img'
                    src="https://cdn-icons-png.flaticon.com/512/9934/9934429.png"
                    sx={{ width: 150, height: 150, mt: 5 }}
                >
                </Box>

                <Typography
                    sx={{
                        fontSize: 14,
                        color: "#8A94A6",
                        pt: 3,
                    }}
                >
                    Bằng việc tiếp tục, bạn đồng ý với{" "}
                    <Box
                        component="span"
                        sx={{
                            color: "#4C8DF6",
                            fontWeight: 600,
                            cursor: "pointer",
                        }}
                    >
                        Điều khoản
                    </Box>{" "}
                    của chúng tôi.
                </Typography>
            </Box>
        </div>
    )
}
