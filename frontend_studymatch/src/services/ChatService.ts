import { Socket } from "dgram"
import WebSocketManager from "../socket/WebSocketManager"
import { SocketEvent } from "../enum/SocketEvent"
import { SOCKET_SEND_MESSAGE } from "../config/BaseConfig"


export const sendText = (content: string, senderId: number, conversationId: number) => {

    let ws = WebSocketManager.getInstance()
    ws.connect().then(() => {
        ws.sendMessage(SOCKET_SEND_MESSAGE, {
            event: SocketEvent.SEND_CHAT,
            data: {
                conversationId: 1,
                senderId: 1,
                type: "text",
                content: content,
            }
        })
    }).catch((err) => {
        console.error("Lỗi connect:", err);
    });

}


export const sendFirstMessage = (content: string, to: number) => {
    let ws = WebSocketManager.getInstance()
    ws.connect().then(() => {
        ws.sendMessage(SOCKET_SEND_MESSAGE, {
            event: SocketEvent.FIRST_PRIVATE_MESS,
            data: {
                senderId: 1,
                to: to,
                type: "text",
                content: content,
            }
        })
    }).catch((err) => {
        console.error("Lỗi connect:", err);
    });

}