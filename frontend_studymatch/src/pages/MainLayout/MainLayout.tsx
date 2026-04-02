

import { Box } from '@mui/system'
import React from 'react'
import { Outlet } from 'react-router-dom'
import SideBar from '../../components/sidebar/SideBar'
import Header from '../../components/header/Header'

export default function MainLayout() {
    return (
        <div>
            <Box sx={{ display: 'flex' }}>
                <SideBar></SideBar>
                <Box sx={{ width: '100%' }}>
                    <Header></Header>
                    <Box sx={{ display: 'block' }}>
                        <Outlet></Outlet>
                    </Box>
                </Box>
            </Box>

        </div>
    )
}
