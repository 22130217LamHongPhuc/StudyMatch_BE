import { Typography } from '@mui/material'
import { Box } from '@mui/system'
import React from 'react'

export default function WelcomeConversion() {
    return (
        <>

            <Box sx={{ textAlign: "center" }}>
                <Box
                    component="img"
                    src="https://app.studystream.live/assets/images/select-conversation-img.svg"
                    alt="empty state"
                    sx={{
                        opacity: 0.9,
                        mb: 3,
                    }}
                />


                <Typography
                    sx={{
                        fontWeight: 700,
                        fontSize: 18,
                        mt: '20px'

                    }}
                >
                    Hãy bắt đầu cuộc hội thoại nào
                </Typography>
            </Box>

        </>
    )
}
