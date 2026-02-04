
import React from 'react'
import { Box, Button, Container, } from '@mui/material'
import Header from '../components/header/Header'
import SideBar from '../components/sidebar/SideBar'
import WelcomeSection from '../components/home/WelcomeSection'
import ReportProblemOutlinedIcon from '@mui/icons-material/ReportProblemOutlined'
export default function HomePage() {
    return (

        <Box sx={{ display: 'flex' }}>
            <SideBar></SideBar>
            <Box sx={{ width: '100%' }}>
                <Header></Header>
                <Box sx={{ display: 'block' }}>

                    <WelcomeSection></WelcomeSection>

                </Box>

            </Box>


        </Box>
    )
}