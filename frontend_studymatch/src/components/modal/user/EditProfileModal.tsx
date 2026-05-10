import {
    Modal,
    Box,
    Typography,
    TextField,
    Avatar,
    Button,
    IconButton,
    Stack,
    styled
} from '@mui/material';
import CloseIcon from '@mui/icons-material/Close';
import PhotoCameraIcon from '@mui/icons-material/PhotoCamera';
import React, { useEffect, useState } from 'react'
import { useSelector } from 'react-redux';
import { RootState } from '../../../redux/store';
import { stat } from 'fs';

export default function EditProfileModal({ stateModal, setModalEdit }: { stateModal: boolean, setModalEdit: React.Dispatch<React.SetStateAction<boolean>>; }) {
    const user = useSelector((state: RootState) => state.user)
    return (
        <>
            <Modal
                open={stateModal}
                aria-labelledby="edit-profile-title"
                aria-describedby="edit-profile-description"
            >
                <Box sx={{
                    position: 'absolute',
                    top: '50%',
                    left: '50%',
                    transform: 'translate(-50%, -50%)',
                    width: { xs: '90%', sm: 500 },
                    bgcolor: 'background.paper',
                    boxShadow: 24,
                    p: 4,
                    borderRadius: 2,
                    outline: 'none',
                }}>
                    <Stack direction="row" justifyContent="space-between" alignItems="center" mb={3}>
                        <Typography id="edit-profile-title" variant="h6" component="h2" fontWeight="bold">
                            Chỉnh sửa thông tin
                        </Typography>
                        <IconButton size="medium" onClick={() => setModalEdit(false)}>
                            <CloseIcon />
                        </IconButton>
                    </Stack>


                    <Stack spacing={3}>
                        <Box textAlign="center" mb={2}>
                            <Avatar
                                src={user?.avatar || 'https://www.shutterstock.com/image-illustration/generic-image-default-avatar-profile-600nw-1902153229.jpg'}
                                sx={{ width: 100, height: 100, mx: 'auto', border: '2px solid #ddd' }}
                            />
                        </Box>

                        <TextField
                            label="Họ và tên"
                            name="name"
                            variant="outlined"
                            fullWidth
                            value={user.username}
                        />
                        <TextField
                            label="Bio"
                            name="bio"
                            variant="outlined"
                            fullWidth
                            multiline
                            rows={4}
                            value={'đây la mô tả'}
                        />
                    </Stack>

                    <Stack direction="row" justifyContent="flex-end" spacing={2} mt={4}>
                        <Button variant="outlined" onClick={() => setModalEdit(false)}>
                            Hủy
                        </Button>
                        <Button variant="contained" sx={{ px: 5, background: "linear-gradient(90deg, #4f8dfd, #3b82f6)" }}>
                            Lưu
                        </Button>
                    </Stack>
                </Box>
            </Modal>

        </>
    )
}
