import { createBrowserRouter } from "react-router-dom";
import MainLayout from "../pages/MainLayout/MainLayout";
import HomePage from "../pages/HomePage";
import FriendsPage from "../pages/FriendsLayout/FriendsPage";
import SchedulePage from "../pages/SchedulePage/SchedulePage";
import ProfilePage from "../pages/ProfilePage/ProfilePage";
import ConversationPage from "../pages/Conversation/ConversationPage";

export const router = createBrowserRouter([
    {
        element: <MainLayout />,
        children: [
            { path: "/", element: <HomePage /> },
            { path: "/friends", element: <HomePage /> },
            { path: "/schedule", element: <SchedulePage /> },
            { path: '/profile', element: <ProfilePage></ProfilePage> },
            { path: '/conversations', element: <ConversationPage /> }
        ],
    },
    // {
    //     element: <FriendsPage></FriendsPage>,
    //     children: [
    //         { path: "/friends", element: <HomePage /> }
    //     ]
    // },
    // {
    //     element: <SchedulePage></SchedulePage>,
    //     children: [
    //         { path: "/schedule", element: <SchedulePage /> }
    //     ]
    // }
]);