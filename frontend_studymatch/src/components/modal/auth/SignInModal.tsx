import { Close, Email, Lock, VisibilityOff } from '@mui/icons-material'
import { Button, IconButton, InputAdornment, Modal, TextField, Typography } from '@mui/material'
import { Box } from '@mui/system'
import React, { useState } from 'react'
import { loginRequest } from '../../../services/UserService'
import { useDispatch } from 'react-redux'
import { userAction } from '../../../redux/UserReducer'
import { APIResponse } from '../../../model/APIResponse'
import { LoginSuccess } from '../../../model/UserModel'

type UserForm = {
    email: string,
    password: string
}

export default function SignInModal({ open, setModal }: { open: boolean, setModal: React.Dispatch<React.SetStateAction<boolean>> }) {
    const [user, setUser] = useState<UserForm>({
        email: '',
        password: ''
    })
    const dispatch = useDispatch()
    const [error, setError] = useState<{ email: string; password: string, message?: string }>({ email: '', password: '' });
    const [loading, setLoading] = useState<boolean>(false);
    const [responseLogin, setResponseLogin] = useState<string>('');
    const handleForm = (): boolean => {
        setError({ email: '', password: '' });
        let isValid = true;
        if (user.email.trim() === '') {
            console.log('err name');
            setError((err) => ({ ...err, email: 'Email không được bỏ trống' }));
            isValid = false;
        }
        if (user.password.trim() === '') {
            console.log('err pass');
            setError((err) => ({ ...err, password: 'Mật khẩu không được bỏ trống' }));
            isValid = false;
        }
        return isValid;
    };

    const submitForm = async (e: React.FormEvent<HTMLFormElement>) => {
        e.preventDefault();
        if (!handleForm()) return;
        try {
            setLoading(true);
            // await loginRequest(user)
            const responeWS: APIResponse = await loginRequest(user);
            if (responeWS.code === 200) {
                localStorage.setItem('userId', responeWS.data.userId.toString());
                let userRS: LoginSuccess = responeWS.data
                dispatch(userAction({ username: userRS.username, email: userRS.email, token: userRS.token, avatar: userRS.avatar }))
                setModal(false);
            }
            else {
                setError((err) => ({ ...err, message: responeWS.message }))
            }
            console.log('đây là response', responeWS)

            console.warn(responeWS)

        } finally {
            setLoading(false);
        }

    };

    return (
        <div>
            <Modal open={open} >
                <Box sx={{
                    position: 'absolute',
                    top: '50%',
                    left: '50%',
                    transform: 'translate(-50%, -50%)',
                    width: 500,
                    height: 450,
                    bgcolor: '#fff',

                    borderRadius: 2,
                    outline: 'none',
                    display: 'flex',
                    flexDirection: 'column',
                    padding: '30px',
                }}>
                    <form onSubmit={(e) => submitForm(e)}>
                        <Box
                            sx={{

                                display: 'flex',
                                justifyContent: 'space-between',
                                alignItems: 'center',
                                width: '100%',
                                borderBottom: '1px solid #e5e7eb'
                            }}
                        >
                            <Typography fontSize={22} fontWeight={600}>
                                Chào mừng bạn
                            </Typography>
                            <IconButton onClick={() => { setModal(false) }}>
                                <Close />
                            </IconButton>
                        </Box>
                        {error.message && <Typography>{error.message} </Typography>}
                        <Typography fontSize={15} mb={1} mt={'20px'} fontWeight={'400'}>
                            Email:
                        </Typography>
                        {error.email && <>  <Typography> Vui lòng nhập email</Typography></>}

                        <TextField
                            fullWidth
                            placeholder="Nhập Email"
                            onChange={(e) => setUser((user) => ({ ...user, email: e.target.value }))}
                            InputProps={{
                                sx: {
                                    height: 45,
                                    fontSize: '14px'
                                },
                                startAdornment: (
                                    <InputAdornment position="start">
                                        <Email color="disabled" sx={{ fontSize: '20px' }} />
                                    </InputAdornment>
                                )
                            }}
                            sx={{ mb: 3 }}
                        />
                        <Typography fontSize={15} mb={1} fontWeight={'400'}>
                            Mật khẩu:
                        </Typography>
                        {error.password && <>  <Typography> Vui lòng nhập mật khẩu</Typography></>}
                        <TextField
                            fullWidth
                            placeholder="Mật khẩu"
                            type="password"
                            onChange={(e) => setUser((user) => ({ ...user, password: e.target.value }))}
                            InputProps={{
                                sx: {
                                    height: 45,
                                    fontSize: '14px'
                                },
                                startAdornment: (
                                    <InputAdornment position="start">
                                        <Lock color="disabled" sx={{ fontSize: '20px' }} />
                                    </InputAdornment>
                                ),
                            }}
                        />
                        <Typography
                            fontSize={14}
                            color="primary"
                            sx={{ mt: 1.5, cursor: 'pointer' }}
                        >
                            Quên mật khẩu?
                        </Typography>
                        <Button
                            fullWidth

                            sx={{
                                mt: 3,
                                height: 48,
                                borderRadius: '12px',
                                cursor: 'pointer',
                                textTransform: 'none',
                                fontSize: 16,
                                backgroundColor: 'rgb(55, 145, 250)',
                                color: '#fff'
                            }}
                            type='submit'
                        >Đăng nhập
                        </Button>
                        <Button
                            fullWidth

                            sx={{
                                mt: 2,
                                height: 48,
                                borderRadius: '12px',
                                cursor: 'pointer',
                                textTransform: 'none',
                                fontSize: 16,
                                backgroundColor: '',
                                color: 'rgb(55, 145, 250)'
                            }}
                        >
                            Đăng kí tài khoản
                        </Button>
                    </form>
                    <Typography
                        fontSize={14}
                        textAlign="center"
                        mt={3}
                    ></Typography>
                </Box>
            </Modal>
        </div>
    )
}

