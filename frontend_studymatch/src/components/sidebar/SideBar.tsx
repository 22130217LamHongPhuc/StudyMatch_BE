import { List, ListItemButton } from "@mui/material";
import { Box } from "@mui/system";
import React from "react";
import HomeIcon from "@mui/icons-material/Home";
import PersonIcon from "@mui/icons-material/Person";
import QuestionAnswerIcon from "@mui/icons-material/QuestionAnswer";
import Diversity2Icon from "@mui/icons-material/Diversity2";
import BarChartIcon from "@mui/icons-material/BarChart";
import FeedbackIcon from "@mui/icons-material/Feedback";
import CalendarMonthIcon from "@mui/icons-material/CalendarMonth";
import BadgeIcon from "@mui/icons-material/Badge";
import { useLocation, useNavigate } from "react-router-dom";

interface NavItem {
  label: string;
  icon: React.ReactNode;
  path?: string;
}

const navItems: NavItem[] = [
  { label: "Trang chu", icon: <HomeIcon />, path: "/" },
  { label: "Ho so", icon: <BadgeIcon />, path: "/profile" },
  { label: "Lich hoc", icon: <CalendarMonthIcon />, path: "/schedule" },
  { label: "Goi y ban hoc", icon: <BarChartIcon />, path: "/recommendation" },
  { label: "Ban be", icon: <PersonIcon />, path: "/friends" },
  { label: "Cuoc hoi thoai", icon: <QuestionAnswerIcon /> },
  { label: "Nhom", icon: <Diversity2Icon /> },
  { label: "Phan hoi", icon: <FeedbackIcon /> },
];

export default function SideBar() {
  const navigate = useNavigate();
  const location = useLocation();

  return (
    <Box sx={{ width: "220px" }}>
      <Box
        component="img"
        src="https://app.studystream.live/assets/images/logo-full.svg"
        alt="logo"
        sx={{ width: "150px", display: "block", mx: "auto", my: "30px" }}
      />

      <List sx={{ borderRight: "1px solid #E8EEF8", pr: 1 }}>
        {navItems.map((item) => {
          const isActive =
            Boolean(item.path) && location.pathname === item.path;
          return (
            <ListItemButton
              key={item.label}
              onClick={() => item.path && navigate(item.path)}
              sx={{
                py: 1.6,
                px: 2,
                borderRadius: 2,
                mb: 0.3,
                cursor: item.path ? "pointer" : "default",
                bgcolor: isActive ? "rgb(246, 249, 255)" : "transparent",
                color: "rgb(28, 26, 50)",
                "& .MuiSvgIcon-root": {
                  color: "rgb(55, 145, 250)",
                  mr: 1.5,
                },
              }}
            >
              {item.icon}
              <Box component="span">{item.label}</Box>
            </ListItemButton>
          );
        })}
      </List>
    </Box>
  );
}
