import React from 'react';
import { Box, Typography, Avatar, IconButton } from '@mui/material';
import {
    MoreHoriz,
    CheckCircle,
    Public,
    ChatBubbleOutline,
    ReplyOutlined,

} from '@mui/icons-material';
import ThumbUpIcon from '@mui/icons-material/ThumbUp';
import ChatBubbleIcon from '@mui/icons-material/ChatBubble';
import ShareIcon from '@mui/icons-material/Share';

const Post = () => {
    return (
        <Box
            sx={{

                width: '100%',
                mx: 'auto',
                my: '20px',
                bgcolor: 'white',
                borderRadius: '8px',
                boxShadow: '0 8px 24px rgba(0,0,0,0.15)',
                overflow: 'hidden',

            }}
        >
            <Box sx={{ p: '12px 16px', display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
                <Box sx={{ display: 'flex', alignItems: 'center' }}>
                    <Avatar
                        src="https://storageaccountstudy9794.blob.core.windows.net/user/97b8aefd-ca2f-44d1-8be3-2deacbe2bf1d_thumb.jpg"
                        sx={{ width: 40, height: 40, mr: 1.5, border: '1px solid #f0f2f5' }}
                    />
                    <Box>
                        <Box sx={{ display: 'flex', alignItems: 'center', gap: 0.5 }}>
                            <Typography sx={{ fontWeight: 700, color: '#050505', lineHeight: 1.2 }}>
                                Cus Maz
                            </Typography>
                            {/* <CheckCircle sx={{ fontSize: 15, color: '#1877F2' }} /> */}
                        </Box>
                        <Box sx={{ display: 'flex', alignItems: 'center', gap: 0.5, mt: 0.2 }}>
                            <Typography sx={{ fontSize: '0.8125rem', color: '#65676b' }}>
                                2 giờ ·
                            </Typography>
                            <Public sx={{ fontSize: 12, color: '#65676b' }} />
                        </Box>
                    </Box>
                </Box>
                <IconButton size="small">
                    <MoreHoriz />
                </IconButton>
            </Box>

            <Box
                sx={{
                    height: 400,
                    bgcolor: '#fdf4e3',
                    display: 'flex',
                    alignItems: 'center',
                    justifyContent: 'center',
                    px: 4,
                    position: 'relative',
                    overflow: 'hidden'
                }}
            >



                <Typography
                    sx={{

                        textAlign: 'center',
                        color: '#4b4b4b',

                    }}
                >
                    đây là nội dung nè
                </Typography>
            </Box>

            <Box
                sx={{
                    p: '10px 16px',
                    display: 'flex',
                    alignItems: 'center',
                    justifyContent: 'space-between',
                }}
            >
                <Box sx={{ display: 'flex', gap: 2.5 }}>
                    <Box sx={{ display: 'flex', alignItems: 'center', gap: 0.8, cursor: 'pointer' }}>
                        <ThumbUpIcon sx={{ fontSize: 22, color: '#65676b' }} />
                        <Typography sx={{ fontSize: '0.9rem', color: '#65676b', fontWeight: 500 }}>1,3K</Typography>
                    </Box>

                    <Box sx={{ display: 'flex', alignItems: 'center', gap: 0.8, cursor: 'pointer' }}>
                        <ChatBubbleIcon sx={{ fontSize: 22, color: '#65676b' }} />
                        <Typography sx={{ fontSize: '0.9rem', color: '#65676b', fontWeight: 500 }}>37</Typography>
                    </Box>

                    <Box sx={{ display: 'flex', alignItems: 'center', gap: 0.8, cursor: 'pointer' }}>
                        <ShareIcon sx={{ fontSize: 24, color: '#65676b', transform: 'scaleX(-1)' }} />
                        <Typography sx={{ fontSize: '0.9rem', color: '#65676b', fontWeight: 500 }}>10</Typography>
                    </Box>
                </Box>
            </Box>
        </Box>
    );
};

export default Post;