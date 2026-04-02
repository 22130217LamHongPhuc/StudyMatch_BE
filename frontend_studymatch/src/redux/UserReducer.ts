import { createSlice, PayloadAction } from "@reduxjs/toolkit"
interface UserInterface {
    username: string | null
    email: string | null
    token: string | null
}

const initialUser: UserInterface = {
    username: null,
    email: null,
    token: null
}

const userReducer = createSlice({
    name: 'auth',
    initialState: initialUser,
    reducers: {
        userAction(state, action: PayloadAction<{ username: string, email: string, token: string }>) {
            state.username = action.payload.username
            state.email = action.payload.email
            state.token = action.payload.token
            console.log('reducer nè', action.payload)
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