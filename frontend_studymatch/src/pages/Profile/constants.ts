import { DayHeader, SlotMeta } from "./types";

export const PROFILE_DAYS: DayHeader[] = [
  { id: 0, label: "Thứ Hai", short: "T2" },
  { id: 1, label: "Thứ Ba", short: "T3" },
  { id: 2, label: "Thứ Tư", short: "T4" },
  { id: 3, label: "Thứ Năm", short: "T5" },
  { id: 4, label: "Thứ Sáu", short: "T6" },
  { id: 5, label: "Thứ Bảy", short: "T7" },
  { id: 6, label: "Chủ Nhật", short: "CN" },
];

export const PROFILE_SLOTS: SlotMeta[] = [
  { id: "ca1", label: "Ca 1", time: "07:00 - 09:15" },
  { id: "ca2", label: "Ca 2", time: "09:30 - 11:45" },
  { id: "ca3", label: "Ca 3", time: "12:15 - 14:30" },
  { id: "ca4", label: "Ca 4", time: "14:50 - 17:05" },
  { id: "ca5", label: "Ca 5", time: "17:30 - 19:45" },
  { id: "ca6", label: "Ca 6", time: "20:00 - 21:45" },
];

export const STUDY_MODE_LABELS: Record<string, string> = {
  mutual_support: "Học cùng người tương đồng",
  peer_support: "Học cùng người nhỉnh hơn",
  challenge: "Tìm thử thách bản thân",
  support: "Hỗ trợ người khác",
};

