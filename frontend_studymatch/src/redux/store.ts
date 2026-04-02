import { configureStore } from '@reduxjs/toolkit'
import { userAction } from './UserReducer';
import userReducer from '../redux/UserReducer'
const store = configureStore({
    reducer: {
        user: userReducer
    }
});
export default store;
export type RootState = ReturnType<typeof store.getState>;
export type AppDispatch = typeof store.dispatch;