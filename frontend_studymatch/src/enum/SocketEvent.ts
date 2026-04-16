import { Socket } from "dgram";


export enum SocketEvent {
    SEND_CHAT = 'SEND_CHAT',
    RECEIVE_CHAT = 'RECEIVE_CHAT',
    USER_JOINED = 'USER_JOINED',
    USER_LEFT = 'USER_LEFT',

}