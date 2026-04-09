import {
    Box,
    Avatar,
    TextField,
    Typography,
    InputAdornment,
    IconButton,
} from "@mui/material";
import ArrowForwardIcon from "@mui/icons-material/ArrowForward";
import GroupIcon from "@mui/icons-material/Group";
import DiamondIcon from "@mui/icons-material/Diamond";
import KeyboardArrowDownIcon from "@mui/icons-material/KeyboardArrowDown";
import VerifiedIcon from "@mui/icons-material/Verified";
import React from 'react'
import WelcomeConversion from "../../components/conversation/WelcomeConversion";

export default function ConversationPage() {

    const users = [
        {
            id: 1,
            name: "Roberta",
            avatar: "https://i.pravatar.cc/100?img=1",
            verified: true,
            badge: "75",
            lastMessage: "Hello bro!"
        },
        {
            id: 2,
            name: "Junel234",
            avatar: "https://i.pravatar.cc/100?img=2",
            verified: false,
            badge: "100",
        },
        {
            id: 3,
            name: "Mai101525",
            avatar: "https://i.pravatar.cc/100?img=3",
            verified: true,
            badge: "100",
        },
        {
            id: 4,
            name: "always hungry",
            avatar: "https://i.pravatar.cc/100?img=4",
            verified: false,
            badge: "300",
        },
        {
            id: 5,
            name: "ZADDY",
            avatar: "https://i.pravatar.cc/100?img=5",
            verified: true,
            badge: "400",
        },
        {
            id: 6,
            name: "Ameer Andri",
            avatar: "https://i.pravatar.cc/100?img=6",
            verified: true,
            badge: "75",
        },
    ];





    return (
        <div>
            <Box
                sx={{
                    display: "flex",
                    height: "100vh",
                    bgcolor: "#f4f6fb",
                    overflow: "hidden",
                }}
            >
                <Box
                    sx={{
                        width: '75%',
                        display: "flex",
                        alignItems: "center",
                        justifyContent: "center",
                        bgcolor: "#eef1f8",
                        position: "relative",
                    }}
                >
                    <WelcomeConversion></WelcomeConversion>
                </Box>
                <Box sx={{ width: '25%', p: '10px' }}>
                    <Box
                        sx={{
                            display: "flex",
                            alignItems: "center",
                            justifyContent: "space-between",
                            mt: '10px',
                            mb: 3,
                        }}
                    >
                        <ArrowForwardIcon sx={{ color: "#8d8fa3" }} />
                        <Typography sx={{ fontWeight: 700 }}>
                            Bạn bè
                        </Typography>
                        <Box sx={{ width: 24 }} />
                    </Box>
                    <TextField
                        fullWidth
                        placeholder="Tìm kiếm bạn bè"
                        variant="outlined"
                        size="medium"
                        sx={{
                            mb: 2.5,
                            height: '10px',
                            "& .MuiOutlinedInput-root": {
                                borderRadius: "10px",
                                bgcolor: "#fff",
                                // height: "40px",

                            },
                            "& .MuiOutlinedInput-input": {
                                padding: "10px",

                            }
                        }}
                    />

                    <Box
                        sx={{
                            flex: 1,
                            overflowY: "auto",
                            py: 2,
                            pt: '20px'
                        }}
                    >
                        {users.map((user) => (
                            <Box
                                key={user.id}
                                sx={{
                                    display: "flex",
                                    alignItems: "center",
                                    justifyContent: "space-between",
                                    py: 1.5,
                                    px: 1,
                                    borderRadius: "14px",
                                    "&:hover": {
                                        bgcolor: "#dadada",
                                        cursor: "pointer",
                                    },
                                }}
                            >
                                <Box sx={{ display: "flex", alignItems: "center", gap: 1.5 }}>
                                    <Box sx={{ position: "relative" }}>
                                        <Avatar
                                            src={user.avatar}
                                            sx={{ width: 45, height: 45 }}
                                        />
                                        <Box
                                            sx={{
                                                position: "absolute",
                                                right: -2,
                                                bottom: -2,
                                                width: 16,
                                                height: 16,
                                                borderRadius: "50%",
                                                bgcolor: "#48d26d",
                                                border: "2px solid white",
                                            }}
                                        />
                                    </Box>

                                    <Box sx={{ alignItems: "center", gap: 0.5 }}>
                                        <Typography
                                            sx={{
                                                fontSize: 16,
                                                fontWeight: 600,
                                                color: "#1f2a44",
                                            }}
                                        >
                                            {user.name}
                                        </Typography>
                                        <Typography
                                            sx={{
                                                fontSize: 13,
                                                color: "#8d8fa3",
                                                mt: "2px",
                                                whiteSpace: "nowrap",
                                                overflow: "hidden",
                                                textOverflow: "ellipsis",
                                                maxWidth: "160px",
                                            }}
                                        >
                                            {user.lastMessage}
                                        </Typography>
                                    </Box>
                                </Box>


                            </Box>
                        ))}
                    </Box>

                </Box>



            </Box>



        </div >
    )
}
