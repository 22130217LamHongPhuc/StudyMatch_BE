import { Box, Button, Typography } from '@mui/material'
import { border, margin } from '@mui/system'
import React from 'react'

export default function Header() {
    return (
        <>
            <Box sx={{ width: '100%', height: 'fit-content', padding: '15px', display: 'flex', justifyContent: 'space-between', borderBottom: '1px solid #e0e0e0' }}>

                <Typography component={'h1'} sx={{ marginY: 'auto', fontSize: '17px', fontWeight: '400' }}>Trang chủ</Typography>
                <Box>
                    <Button variant='text' sx={{ marginRight: '20px', paddingX: '10px', fontSize: "14px" }}>Đăng kí</Button>
                    <Button sx={{ borderRadius: "10px", background: 'rgb(55, 145, 250)', color: '#fff', fontSize: "14px" }}>Đăng nhập</Button>

                </Box>
            </Box >
        </>
    )
}
