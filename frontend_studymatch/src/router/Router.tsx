import { createBrowserRouter, Outlet } from "react-router-dom";
import MainLayout from "../pages/MainLayout/MainLayout";
import HomePage from "../pages/HomePage";
import FriendsPage from "../pages/FriendsLayout/FriendsPage";
import SchedulePage from "../pages/SchedulePage/SchedulePage";
import ProfilePage from "../pages/Profile";
import RecommendationPage from "../pages/Recommendation";
import { Login } from "@mui/icons-material";
import { AuthLayout } from "../pages/MainLayout/AuthLayout";
import LoginPage from "../pages/Auth/LoginPage";
import RegisterPage from "../pages/Auth/RegisterPage";
import OnboardingFlow from "../pages/Onboarding/Onboarding";
import CreateGroupPage from "../pages/Group/CreateGroupPage";
export const router = createBrowserRouter([
  // Auth routes
  {
    element: <AuthLayout />,
    children: [
      { path: "/login", element: <LoginPage /> },
      { path: "/register", element: <RegisterPage /> },
      { path: "/onboarding", element: <OnboardingFlow /> },
    ],
  },

  {
    element: <MainLayout />,
    children: [
      { path: "/", element: <HomePage /> },
      { path: "/friends", element: <HomePage /> },
      { path: "/schedule", element: <SchedulePage /> },
      { path: "/profile", element: <ProfilePage /> },
      { path: "/recommendation", element: <RecommendationPage /> },
    ],
  },

  {
    element: <Outlet />,
    children: [{ path: "/create-group", element: <CreateGroupPage /> }],
  },
]);
