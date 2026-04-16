import { Socket } from "dgram"
import WebSocketManager from "../socket/WebSocketManager"
import { SocketEvent } from "../enum/SocketEvent"
import { SOCKET_SEND_MESSAGE } from "../config/BaseConfig"



export const sendText = (content: string, senderId: number, conversationId: number) => {
    let ws = WebSocketManager.getInstance()
    ws.sendMessage(SOCKET_SEND_MESSAGE, {
        event: SocketEvent.SEND_CHAT,
        data: {
            conversationId: conversationId,
            senderId: senderId,
            messageType: "text",
            content: content,
        }
    })
}