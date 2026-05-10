import { Avatar, Button, Tab, Tabs, Typography } from '@mui/material'
import { Box } from '@mui/system'
import React, { useState } from 'react'
import MoreHorizIcon from "@mui/icons-material/MoreHoriz";
import WorkIcon from "@mui/icons-material/Work";
import Post from './Post';
import EditIcon from '@mui/icons-material/Edit';
import EditProfileModal from '../../components/modal/user/EditProfileModal';
export default function ProfilePage() {
    const [modalEdit, setModalEdit] = useState<boolean>(false)
    console.warn(modalEdit, 'modal edit')
    return (
        <>
            <Box
                component='div'
                sx={{ display: 'flex', mt: '20px' }}
            >
                <Box sx={{ position: 'relative', height: 'fit-content', width: '30%', padding: '20px', mr: '40px', borderRadius: '20px', boxShadow: '0 8px 24px rgba(0,0,0,0.15)', ml: '20px' }}>
                    <Box sx={{ backgroundImage: 'linear-gradient(90deg, rgb(225, 193, 169) 0%, rgba(225, 193, 169, 0.314) 100%);', height: '100px' }}>

                    </Box>
                    <Box sx={{ borderRadius: '50%', width: 115, height: 115, position: 'absolute', top: '50px', ml: '10px', backgroundColor: '#fff', display: 'flex', justifyContent: 'center', alignItems: 'center' }}>
                        <Avatar
                            alt="avatar"
                            src="https://storageaccountstudy9794.blob.core.windows.net/user/97b8aefd-ca2f-44d1-8be3-2deacbe2bf1d_thumb.jpg"
                            sx={{ width: '90%', height: '90%' }}
                        />
                    </Box>

                    <Typography fontSize="32px" fontWeight="bold" color='black' mt='50px'>
                        Cus Maz
                    </Typography>

                    <Box
                        sx={{
                            mt: 2,
                            p: 2,
                            borderRadius: "12px",
                            backgroundColor: "#e9edf2",
                            color: "#6b7280",
                        }}
                    >
                        đây là bio
                    </Box>
                    <Box
                        sx={{
                            display: "flex",
                            justifyContent: "space-around",
                            alignItems: "center",
                            mt: 3,
                            mb: 3,
                        }}
                    >
                        <Box textAlign="center">
                            <Typography color="#6b7280">Bạn bè</Typography>
                            <Typography fontSize="20px" fontWeight="bold">
                                10
                            </Typography>
                        </Box>

                        <Box
                            sx={{
                                width: "1px",
                                height: "30px",
                                backgroundColor: "#d1d5db",
                            }}
                        />

                        <Box textAlign="center">
                            <Typography color="#6b7280">Bạn chung</Typography>
                            <Typography fontSize="20px" fontWeight="bold">
                                0
                            </Typography>
                        </Box>
                    </Box>
                    <Box display='flex' mt='20px'>
                        <Button

                            sx={{
                                borderRadius: "20px",
                                py: 1.5,
                                px: 6,
                                textTransform: "none",
                                fontWeight: "bold",
                                background: "linear-gradient(90deg, #4f8dfd, #3b82f6)",
                                color: "white", width: '100%',
                                display: 'flex',
                                justifyContent: 'space-around'
                            }}
                            onClick={() => setModalEdit(true)}
                        >
                            <EditIcon></EditIcon>
                            Chỉnh sửa thông tin cá nhân
                        </Button>

                        {/* <Button

                            sx={{
                                borderRadius: "20px",
                                py: 1.5,
                                textTransform: "none",
                                fontWeight: "bold",
                                background: "linear-gradient(90deg, #4f8dfd, #3b82f6)",
                                color: "white", width: '50%',
                                marginRight: '20px'
                            }}
                        >
                            Kết bạn
                        </Button>
                        <Button
                            variant="outlined"
                            sx={{
                                borderRadius: "20px",
                                py: 1.5,
                                textTransform: "none",
                                fontWeight: "bold",
                                width: '50%'
                            }}
                        >
                            Nhắn tin
                        </Button> */}

                    </Box>


                </Box>
                <Box width='70%' sx={{ px: '20px' }}>
                    <Box sx={{
                        backgroundColor: '#e9f0ff', fontSize: '10px', '& .MuiTab-root': {
                            fontSize: '12px',
                        },
                    }}  >
                        <Tabs aria-label="basic tabs example" centered>
                            <Tab label="Bản tin" />
                            <Tab label="Thành tích" />
                            <Tab label="Thống kê" />
                        </Tabs>
                    </Box>
                    <Post></Post>
                    <Post></Post>
                </Box>


            </Box >
            <EditProfileModal stateModal={modalEdit}
                setModalEdit={setModalEdit}

            ></EditProfileModal>
        </>
    )
}
