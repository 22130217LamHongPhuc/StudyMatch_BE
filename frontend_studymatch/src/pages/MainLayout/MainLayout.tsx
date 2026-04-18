import { Box } from "@mui/system";
import React from "react";
import { Outlet } from "react-router-dom";
import SideBar from "../../components/sidebar/SideBar";
import Header from "../../components/header/Header";

export default function MainLayout() {
  return (
    <div>
      <Box sx={{ display: "flex" }}>
        <Box sx={{ flexShrink: 0 }}>
          <SideBar />
        </Box>

        <Box sx={{ flex: 1 }}>
          <Header />
          <Box>
            <Outlet />
          </Box>
        </Box>
      </Box>
    </div>
  );
}
