import React, { useEffect, useRef, useState } from "react";
import { Client } from "@stomp/stompjs";
import WebSocketManager from "../../socket/WebSocketManager";

export default function TestSocket() {
    const clientRef = useRef<any>(null);
    const [connected, setConnected] = useState(false);
    const [messages, setMessages] = useState<any>([]);

    useEffect(() => {
        let ws = WebSocketManager.getInstance()
        ws.connect()

    })

    const sendMessage = () => {
        if (!clientRef.current || !connected) return;

        clientRef.current.publish({
            destination: "/app/chat.echo",
            body: "",
        });
    };

    return (
        <div style={{ padding: 20 }}>
            <h2>Test WebSocket STOMP</h2>
            <p>Trạng thái: {connected ? "Đã kết nối" : "Chưa kết nối"}</p>
            <button onClick={sendMessage}>Gửi</button>
            <div style={{ marginTop: 20 }}>
                {messages.map((msg: any, index: any) => (
                    <div key={index}>{msg}</div>
                ))}
            </div>
        </div>
    );
}