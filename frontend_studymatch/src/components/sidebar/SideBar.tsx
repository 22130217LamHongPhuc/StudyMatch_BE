import { List, ListItem, ListItemText } from '@mui/material'
import { borderRight, Box, margin } from '@mui/system'
import React from 'react'
import HomeIcon from '@mui/icons-material/Home';
import PersonIcon from '@mui/icons-material/Person';
import QuestionAnswerIcon from '@mui/icons-material/QuestionAnswer';
import Diversity2Icon from '@mui/icons-material/Diversity2';
import BarChartIcon from '@mui/icons-material/BarChart';
import FeedbackIcon from '@mui/icons-material/Feedback';
export default function SideBar() {
    return (
        <div>
            <Box sx={{ width: '200px', }}>


                <Box
                    component={'img'}
                    // src='https://app.studystream.live/assets/images/logo.svg'
                    src='https://app.studystream.live/assets/images/logo-full.svg'
                    alt='logo'
                    sx={{
                        width: "150px", display: "block",
                        mx: "auto",
                        margin: '30px'
                    }}

                ></Box>
                <List sx={{ borderRight: '1px' }}>
                    <ListItem disablePadding sx={{
                        padding: '15px 0',
                        cursor: 'pointer', display: 'flex', alignContent: 'center', paddingLeft: '20px', background: 'rgb(246, 249, 255)'

                    }}>
                        <HomeIcon
                            sx={{ color: ' rgb(55, 145, 250)' }}></HomeIcon>
                        <Box component={'p'} sx={{ marginLeft: '15px', color: 'rgb(28, 26, 50)', marginY: 'auto' }}>Trang chủ</Box>
                    </ListItem>
                    <ListItem sx={{
                        padding: '15px 0',
                        cursor: 'pointer', display: 'flex', alignContent: 'center', paddingLeft: '20px'

                    }}>
                        <PersonIcon
                            sx={{ color: ' rgb(55, 145, 250)' }}></PersonIcon>
                        <Box component={'p'} sx={{ marginLeft: '15px', color: 'rgb(28, 26, 50)', marginY: 'auto' }}>Bạn bè</Box>
                    </ListItem>
                    <ListItem sx={{
                        padding: '15px 0',
                        cursor: 'pointer', display: 'flex', alignContent: 'center', paddingLeft: '20px'

                    }}>
                        <QuestionAnswerIcon
                            sx={{ color: ' rgb(55, 145, 250)' }}></QuestionAnswerIcon>
                        <Box component={'p'} sx={{ marginLeft: '15px', color: 'rgb(28, 26, 50)', marginY: 'auto' }}>Cuộc hội thoại</Box>
                    </ListItem>
                    <ListItem sx={{
                        padding: '15px 0',
                        cursor: 'pointer', display: 'flex', alignContent: 'center', paddingLeft: '20px'

                    }}>
                        <Diversity2Icon
                            sx={{ color: ' rgb(55, 145, 250)' }}></Diversity2Icon>
                        <Box component={'p'} sx={{ marginLeft: '15px', color: 'rgb(28, 26, 50)', marginY: 'auto' }}>Nhóm</Box>
                    </ListItem>
                    <ListItem sx={{
                        padding: '15px 0',
                        cursor: 'pointer', display: 'flex', alignContent: 'center', paddingLeft: '20px'

                    }}>
                        <BarChartIcon
                            sx={{ color: ' rgb(55, 145, 250)' }}></BarChartIcon>
                        <Box component={'p'} sx={{ marginLeft: '15px', color: 'rgb(28, 26, 50)', marginY: 'auto' }}>Thống kê</Box>
                    </ListItem>
                    <ListItem sx={{
                        padding: '15px 0',
                        cursor: 'pointer', display: 'flex', alignContent: 'center', paddingLeft: '20px'

                    }}>
                        <FeedbackIcon
                            sx={{ color: ' rgb(55, 145, 250)' }}></FeedbackIcon>
                        <Box component={'p'} sx={{ marginLeft: '15px', color: 'rgb(28, 26, 50)', marginY: 'auto' }}>Phản hồi</Box>
                    </ListItem>
                </List>

            </Box>

        </div >
    )
}
