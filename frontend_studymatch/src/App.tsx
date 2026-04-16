import React from "react";
import { BrowserRouter, Route, RouterProvider, Routes } from "react-router-dom";
// import '../src/assets/css/resetcss.css'
import MainLayout from "./pages/MainLayout/MainLayout";
import { router } from "./router/Router";
import { Provider } from "react-redux";
import store from "./redux/store";
import OnboardingFlow from "./pages/Onboarding/Onboarding";
import ProfilePage from "./pages/Profile";
function App() {
  return (
    // <>
    //   <Provider store={store}>
    //     <RouterProvider router={router} />
    //   </Provider>
    // </>
    <ProfilePage></ProfilePage>
  );
}

export default App;
