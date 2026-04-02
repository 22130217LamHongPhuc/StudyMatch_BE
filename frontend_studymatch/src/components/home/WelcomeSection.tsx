import { Button, Typography } from '@mui/material'
import { Box } from '@mui/system'
import React from 'react'

import ReportProblemOutlined from '@mui/icons-material/ReportProblemOutlined'
export default function WelcomeSection() {
    return (
        <div>
            <Box
                sx={{
                    backgroundColor: '#f6f9ff',
                    borderRadius: '12px',
                    padding: '24px 28px',
                    maxWidth: '900px',
                    marginX: 'auto',
                    marginTop: '40px'

                }}
            >
                <Typography
                    variant="h5"
                    sx={{
                        fontWeight: 700,
                        color: '#0b0b2c',
                        mb: 2,
                        display: 'flex',
                        marginX: 'auto'
                    }}
                >
                    Chào mừng bạn đến với StudyMatch
                </Typography>

                <Typography
                    variant="body1"
                    sx={{
                        color: '#1c1a32',
                        mb: 2,
                        lineHeight: 1.7,
                    }}
                >
                    Chúng tôi được thiết kế đặc biệt để nâng cao trải nghiệm học tập của bạn, giúp bạn có thêm động lực và trách nhiệm.
                </Typography>

                <Typography
                    variant="body1"
                    sx={{
                        color: '#1c1a32',
                        lineHeight: 1.7,
                    }}
                >
                    Các phòng học của chúng tôi sẽ kết nối bạn với những người cùng chung niềm đam mê.
                </Typography>

            </Box>
            <Box sx={{
                maxWidth: '900px',
                marginX: 'auto',
                marginTop: '20px',
                display: 'flex',
                justifyContent: 'right'


            }}>
                <Box
                    sx={{
                        display: 'inline-flex',
                        alignItems: 'center',
                        gap: '8px',
                        backgroundColor: '#eef4ff',
                        borderRadius: '10px',
                        padding: '6px 12px',
                        cursor: 'pointer',
                        '&:hover': {
                            backgroundColor: '#e3ecff',
                        },
                    }}
                >

                    <Box
                        sx={{
                            width: 20,
                            height: 20,
                            borderRadius: '50%',
                            backgroundColor: '#dbe8ff',
                            display: 'flex',
                            alignItems: 'center',
                            justifyContent: 'center',
                        }}
                    >
                        <ReportProblemOutlined
                            sx={{ fontSize: 14, color: '#2f6fed' }}
                        />
                    </Box>

                    <Typography
                        variant="body2"
                        sx={{ color: '#2f6fed', fontWeight: 500 }}
                    >
                        Báo cáo vấn đề
                    </Typography>
                </Box>
            </Box>
        </div>
    )
}
