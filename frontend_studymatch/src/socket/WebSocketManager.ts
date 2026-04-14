import { Client, IMessage, StompSubscription } from '@stomp/stompjs';
import { SOCKET_URL } from '../config/BaseConfig';

class WebSocketManager {
    private static instance: WebSocketManager;
    private client: Client | null = null;
    private connected = false;
    private subscriptions: Map<string, StompSubscription> = new Map();

    private constructor() { }

    public static getInstance(): WebSocketManager {
        if (!WebSocketManager.instance) {
            WebSocketManager.instance = new WebSocketManager();
        }
        return WebSocketManager.instance;
    }

    public connect(): Promise<void> {
        return new Promise((resolve, reject) => {
            if (this.client?.active || this.connected) {
                resolve();
                return;
            }

            this.client = new Client({
                brokerURL: SOCKET_URL,
                reconnectDelay: 5000,
                debug: (str) => console.log('[STOMP]', str),

                onConnect: () => {
                    this.connected = true;
                    console.log('STOMP connected');
                    resolve();
                },

                onDisconnect: () => {
                    this.connected = false;
                    console.log('STOMP disconnected');
                },

                onStompError: (frame) => {
                    console.error('STOMP error:', frame);
                },

                onWebSocketError: (error) => {
                    console.error('WebSocket error:', error);
                    reject(error);
                },
            });

            this.client.activate();
        });
    }

    public onMessage(destination: string, cb: (msg: string) => void) {
        if (!this.client || !this.connected) return;
        if (this.subscriptions.has(destination)) return;
        const subscription = this.client.subscribe(destination, (message: IMessage) => {
            cb(message.body);
        });
        this.subscriptions.set(destination, subscription);
    }

    public sendMessage(destination: string, body: any = '') {
        if (!this.client || !this.connected) {
            throw new Error('WebSocket chưa kết nối');
        }

        this.client.publish({
            destination,
            body: typeof body === 'string' ? body : JSON.stringify(body),
        });
    }

    public disconnect() {
        this.subscriptions.forEach((sub) => sub.unsubscribe());
        this.subscriptions.clear();

        if (this.client) {
            this.client.deactivate();
            this.client = null;
        }

        this.connected = false;
    }
}

export default WebSocketManager;