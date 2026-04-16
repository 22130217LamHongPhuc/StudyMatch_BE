
import CallIcon from "@mui/icons-material/Call";
import VideocamIcon from "@mui/icons-material/Videocam";
import MicIcon from "@mui/icons-material/Mic";
import ImageIcon from "@mui/icons-material/Image";
import GifBoxIcon from "@mui/icons-material/GifBox";
import SentimentSatisfiedAltIcon from "@mui/icons-material/SentimentSatisfiedAlt";
import SendIcon from "@mui/icons-material/Send";
import {
    Avatar,
    Box,
    IconButton,
    InputBase,
    Paper,
    Typography,
    TextField,
} from "@mui/material";
import AddPhotoAlternateIcon from "@mui/icons-material/AddPhotoAlternate";
import ArrowForwardIcon from "@mui/icons-material/ArrowForward";
import React, { useEffect, useRef, useState } from "react";
import SockJS from "sockjs-client";
import { Client } from "@stomp/stompjs";
import WebSocketManager from "../../socket/WebSocketManager";
import { sendText } from "../../services/ChatService";
import { South } from "@mui/icons-material";

export default function ConversationPage() {
    const users = [
        {
            id: 1,
            name: "okeeee",
            avatar: "https://i.pravatar.cc/100?img=1",
            verified: true,
            badge: "75",
            lastMessage: "Hello bro!",
        },
        {
            id: 2,
            name: "ZE Z",
            avatar: "https://i.pravatar.cc/100?img=2",
            verified: false,
            badge: "100",
            lastMessage: "",
        },
    ];

    const [messages, setMessages] = useState([]);
    const stompClient = useRef<Client | null>(null);

    useEffect(() => {
        let ws = WebSocketManager.getInstance()
        console.log("đây là userId", localStorage.getItem('userId'))

        ws.connect().then(() => {
            ws.onMessage("/queue/messages/" + localStorage.getItem('userId'), (msg: any) => {
                console.log('nghe nè', msg)

            })
        }).catch((err) => {
            console.error("Lỗi connect:", err);
        });
    }, [])

    const sendMessage = () => {
        console.log("gửi nè")
        sendText("Hello bạn ời", 1, 1)
    }

    return (
        <Box
            sx={{
                display: "flex",
                height: "calc(100vh - 73px)",
                bgcolor: "#f4f6fb",
                overflow: "hidden",
            }}
        >
            <Box
                sx={{
                    width: "75%",
                    display: "flex",
                    flexDirection: "column",
                    bgcolor: "#eef1f8",
                    overflow: "hidden",
                }}
            >
                <Box
                    sx={{
                        height: 78,
                        flexShrink: 0,
                        width: "100%",
                        px: 2,
                        display: "flex",
                        alignItems: "center",
                        justifyContent: "space-between",
                        borderBottom: "1px solid rgba(0,0,0,0.08)",
                        bgcolor: "#fff",
                    }}
                >
                    <Box sx={{ display: "flex", alignItems: "center", gap: 1.5 }}>
                        <Avatar
                            src="https://i.pravatar.cc/100?img=12"
                            sx={{ width: 52, height: 52 }}
                        />
                        <Box>
                            <Typography sx={{ fontWeight: 700, fontSize: 18, color: "#1f1f1f" }}>
                                Nguyễn Gia Bảo
                            </Typography>
                            <Typography sx={{ fontSize: 14, color: "#7f735e" }}>
                                Hoạt động 9 phút trước
                            </Typography>
                        </Box>
                    </Box>
                    <Box sx={{ display: "flex", alignItems: "center", gap: 1 }}>
                        <IconButton sx={{ color: "rgb(55, 145, 250)" }}>
                            <CallIcon />
                        </IconButton>
                        <IconButton sx={{ color: "rgb(55, 145, 250)" }}>
                            <VideocamIcon />
                        </IconButton>
                    </Box>
                </Box>

                <Box
                    sx={{
                        flex: 1,
                        display: "flex",
                        flexDirection: "column-reverse",
                        overflowY: "auto",
                        width: "100%",
                        px: 2,
                        py: 2,
                        background: "linear-gradient(180deg, #f7e19a, #f6885d)",
                    }}
                >
                    <Box sx={{ display: "flex", justifyContent: "flex-end", mb: 1 }}>
                        <Box
                            sx={{
                                bgcolor: "#b30000",
                                color: "#fff",
                                px: 2,
                                py: 1,
                                borderRadius: "18px 18px 4px 18px",
                                maxWidth: "70%",
                            }}
                        >
                            hello bạn
                        </Box>
                    </Box>

                    <Box
                        sx={{
                            display: "flex",
                            justifyContent: "flex-start",
                            mb: 1,
                            alignItems: "flex-end",
                            gap: 1,
                        }}
                    >
                        <Avatar
                            src="https://i.pravatar.cc/100?img=12"
                            sx={{ width: 30, height: 30 }}
                        />
                        <Box
                            sx={{
                                bgcolor: "#fff",
                                px: 2,
                                py: 1,
                                borderRadius: "18px 18px 18px 4px",
                                maxWidth: "70%",
                            }}
                        >
                            hell o nè
                        </Box>
                    </Box>
                </Box>
                <Box
                    sx={{
                        flexShrink: 0,
                        display: "flex",
                        alignItems: "center",
                        width: "100%",
                        gap: 1.5,
                        px: 2,
                        py: 1,
                        bgcolor: "#fff",
                        borderTop: "1px solid rgba(0,0,0,0.08)",
                    }}
                >
                    <IconButton sx={{ color: "#a40000", p: 0.5 }}>
                        <MicIcon />
                    </IconButton>
                    <IconButton sx={{ color: "#a40000", p: 0.5 }}>
                        <ImageIcon />
                    </IconButton>
                    <IconButton sx={{ color: "#a40000", p: 0.5 }}>
                        <AddPhotoAlternateIcon />
                    </IconButton>
                    <IconButton sx={{ color: "#a40000", p: 0.5 }}>
                        <GifBoxIcon />
                    </IconButton>

                    <Paper
                        elevation={0}
                        sx={{
                            flex: 1,
                            display: "flex",
                            alignItems: "center",
                            borderRadius: "999px",
                            px: 2,
                            py: 0.5,
                            bgcolor: "#f6e3de",
                        }}
                    >
                        <InputBase
                            placeholder="Aa"
                            sx={{ flex: 1, fontSize: 16, color: "#6b6b6b" }}
                        />
                        <IconButton sx={{ color: "#a40000", p: 0.5 }}>
                            <SentimentSatisfiedAltIcon />
                        </IconButton>
                    </Paper>

                    <IconButton
                        onClick={sendMessage}
                        sx={{
                            bgcolor: "#a40000",
                            color: "#fff",
                            p: 1.2,
                            "&:hover": { bgcolor: "#8a0000" },
                            flexShrink: 0,
                        }}
                    >
                        <SendIcon sx={{ fontSize: 22 }} />
                    </IconButton>
                </Box>
            </Box>

            <Box
                sx={{
                    width: "25%",
                    display: "flex",
                    flexDirection: "column",
                    height: "100%",
                    p: "10px",
                    overflow: "hidden",
                    borderLeft: "1px solid rgba(0,0,0,0.08)",
                    bgcolor: "#fff",
                }}
            >
                <Box
                    sx={{
                        display: "flex",
                        alignItems: "center",
                        justifyContent: "space-between",
                        mt: "10px",
                        mb: 2,
                        flexShrink: 0,
                    }}
                >
                    <ArrowForwardIcon sx={{ color: "#8d8fa3" }} />
                    <Typography sx={{ fontWeight: 700 }}>Bạn bè</Typography>
                    <Box sx={{ width: 24 }} />
                </Box>

                <TextField
                    fullWidth
                    placeholder="Tìm kiếm bạn bè"
                    variant="outlined"
                    size="small"
                    sx={{
                        mb: 2,
                        flexShrink: 0,
                        "& .MuiOutlinedInput-root": {
                            borderRadius: "10px",
                            bgcolor: "#f4f6fb",
                        },
                        "& .MuiOutlinedInput-input": {
                            padding: "10px",
                        },
                    }}
                />

                <Box sx={{ flex: 1, overflowY: "auto", minHeight: 0 }}>
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
                                    bgcolor: "#f0f2f8",
                                    cursor: "pointer",
                                },
                            }}
                        >
                            <Box sx={{ display: "flex", alignItems: "center", gap: 1.5 }}>
                                <Box sx={{ position: "relative" }}>
                                    <Avatar src={user.avatar} sx={{ width: 45, height: 45 }} />
                                    <Box
                                        sx={{
                                            position: "absolute",
                                            right: -2,
                                            bottom: -2,
                                            width: 14,
                                            height: 14,
                                            borderRadius: "50%",
                                            bgcolor: "#48d26d",
                                            border: "2px solid white",
                                        }}
                                    />
                                </Box>
                                <Box>
                                    <Typography
                                        sx={{ fontSize: 15, fontWeight: 600, color: "#1f2a44" }}
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
    );
}
