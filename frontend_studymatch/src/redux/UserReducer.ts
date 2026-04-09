import { createSlice, PayloadAction } from "@reduxjs/toolkit"
interface UserInterface {
    username: string | null
    email: string | null
    token: string | null
    avatar: string | null
}

const initialUser: UserInterface = {
    username: null,
    email: null,
    token: null,
    avatar: null
}

const userReducer = createSlice({
    name: 'auth',
    initialState: initialUser,
    reducers: {
        userAction(state, action: PayloadAction<{ username: string, email: string, token: string, avatar: string }>) {
            state.username = action.payload.username
            state.email = action.payload.email
            state.token = action.payload.token
            state.avatar = action.payload.avatar
        },

        logout(state) {
            state.username = null
            state.email = null
            state.token = null

        }
    }
})

export const { userAction, logout } = userReducer.actions
export default userReducer.reducer