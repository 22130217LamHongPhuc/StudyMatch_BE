

export const hello = 12


export type UserModel = {
    username: string,
    email: string
}

export interface LoginSuccess {
    userId: number,
    email: string,
    token: string,
    username: string,
    avatar: string
}