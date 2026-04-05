

export const hello = 12


export type UserModel = {
    username: string,
    email: string
}

export interface LoginSuccess {
    email: string,
    token: string,
    username: string
}