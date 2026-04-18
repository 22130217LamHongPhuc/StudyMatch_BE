"use strict";
const API_BASE_URL2 = "http://localhost:8080"; // sửa lại nếu cần
const SUBJECT_POOL_YEAR4_SEM2_K48 = [60, 61, 62, 63, 64, 65, 66, 67, 68, 69];
const REGIONS = [
  "Thành phố Hồ Chí Minh",
  "Thành phố Cần Thơ",
  "Thành phố Đà Nẵng",
  "Thành phố Hà Nội",
  "Đồng Nai",
  "Khánh Hòa",
  "Gia Lai",
  "Cà Mau",
];
const FIRST_NAMES = [
  "Nguyễn",
  "Trần",
  "Lê",
  "Phạm",
  "Hoàng",
  "Huỳnh",
  "Phan",
  "Võ",
  "Đặng",
  "Bùi",
];
const MIDDLE_NAMES = [
  "Minh",
  "Thanh",
  "Quang",
  "Gia",
  "Ngọc",
  "Khánh",
  "Thành",
  "Hải",
  "Anh",
  "Bảo",
];
const LAST_NAMES = [
  "An",
  "Bình",
  "Duy",
  "Hưng",
  "Khang",
  "Lâm",
  "Long",
  "Nam",
  "Ngân",
  "Phúc",
  "Quỳnh",
  "Sơn",
  "Trang",
  "Vy",
];
const SLOT_CODES = ["ca1", "ca2", "ca3", "ca4", "ca5", "ca6"];
const DAYS = [0, 1, 2, 3, 4, 5, 6];
const MODES = ["mutual_support", "peer_support", "challenge", "support"];
function randomInt(min, max) {
  return Math.floor(Math.random() * (max - min + 1)) + min;
}
function randomFloat(min, max, digits = 2) {
  return Number((Math.random() * (max - min) + min).toFixed(digits));
}
function pickOne(arr) {
  return arr[randomInt(0, arr.length - 1)];
}
function shuffle(arr) {
  const copy = [...arr];
  for (let i = copy.length - 1; i > 0; i--) {
    const j = randomInt(0, i);
    [copy[i], copy[j]] = [copy[j], copy[i]];
  }
  return copy;
}
function pickMany(arr, count) {
  return shuffle(arr).slice(0, Math.min(count, arr.length));
}
function buildFullName() {
  return `${pickOne(FIRST_NAMES)} ${pickOne(MIDDLE_NAMES)} ${pickOne(LAST_NAMES)}`;
}
function buildStudentCode(index) {
  // ví dụ: 22130001 -> khóa 48
  return `2213${String(index).padStart(4, "0")}`;
}
function deriveStudyGoal(avgScore) {
  if (avgScore < 5.8) return "Survivor";
  if (avgScore < 6.9) return "Passive Learner";
  if (avgScore < 8.2) return "Standard Learner";
  return "High Achiever";
}
function getUniqueSlot(used) {
  let dayOfWeek;
  let slotCode;
  let key;
  do {
    dayOfWeek = pickOne(DAYS);
    slotCode = pickOne(SLOT_CODES);
    key = `${dayOfWeek}-${slotCode}`;
  } while (used.has(key));
  used.add(key);
  return { dayOfWeek, slotCode };
}
function buildSubjectScheduleSlots(mainSubjectId, currentSubjectIds) {
  const used = new Set();
  const result = [];
  // môn chính: 2 buổi
  for (let i = 0; i < 2; i++) {
    const slot = getUniqueSlot(used);
    result.push({
      subjectId: mainSubjectId,
      dayOfWeek: slot.dayOfWeek,
      slotCode: slot.slotCode,
      scheduleType: "MAIN_SUBJECT",
    });
  }
  // các môn còn lại: mỗi môn 1 hoặc 2 buổi
  for (const subjectId of currentSubjectIds) {
    const count = Math.random() < 0.4 ? 2 : 1;
    for (let i = 0; i < count; i++) {
      const slot = getUniqueSlot(used);
      result.push({
        subjectId,
        dayOfWeek: slot.dayOfWeek,
        slotCode: slot.slotCode,
        scheduleType: "CURRENT_TERM",
      });
    }
  }
  return result;
}
function buildFreeTimeSlots(subjectScheduleSlots) {
  const blocked = new Set(
    subjectScheduleSlots.map((x) => `${x.dayOfWeek}-${x.slotCode}`),
  );
  const available = [];
  for (const day of DAYS) {
    for (const slot of SLOT_CODES) {
      const key = `${day}-${slot}`;
      if (!blocked.has(key)) {
        available.push({
          dayOfWeek: day,
          slotCode: slot,
        });
      }
    }
  }
  return pickMany(available, randomInt(7, 12));
}
function generateOnePayload(index) {
  const mainSubjectId = pickOne(SUBJECT_POOL_YEAR4_SEM2_K48);
  const currentSubjectIds = pickMany(
    SUBJECT_POOL_YEAR4_SEM2_K48.filter((s) => s !== mainSubjectId),
    randomInt(3, 5),
  );
  const avgScore = randomFloat(5.0, 9.5, 2);
  const studyGoal = deriveStudyGoal(avgScore);
  const subjectScheduleSlots = buildSubjectScheduleSlots(
    mainSubjectId,
    currentSubjectIds,
  );
  const freeTimeSlots = buildFreeTimeSlots(subjectScheduleSlots);
  return {
    studentCode: buildStudentCode(index),
    fullName: buildFullName(),
    gender: Math.random() < 0.5 ? "male" : "female",
    ageGroup: "0-35",
    region: pickOne(REGIONS),
    cohortId: 1, // khóa 48
    termId: 8, // HK2 2025-2026
    studyYearNo: 4, // năm 4
    semesterNo: 2, // học kỳ 2
    avgScore,
    studiedCredits: randomInt(95, 140),
    studyGoal,
    studyMode: pickOne(MODES),
    mainSubjectId,
    currentSubjectIds,
    freeTimeSlots,
    subjectScheduleSlots,
  };
}
let userIdCounter = 1; // giả định có 1 user, nếu có nhiều user thì tăng dần
async function submitOnboardingForm(payload) {
  try {
    const response = await fetch(`${API_BASE_URL2}/onboarding/submit`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        "X-User-Id": userIdCounter++, // 👈 truyền ở đây
      },
      body: JSON.stringify(payload),
    });
    if (!response.ok) {
      const errorData = await response.json().catch(() => ({}));
      throw new Error(
        errorData.message ||
          `API Error: ${response.status} ${response.statusText}`,
      );
    }
    const data = await response.json().catch(() => null);
    return {
      success: true,
      data,
    };
  } catch (error) {
    const message =
      error instanceof Error
        ? error.message
        : "Lỗi không xác định khi gửi dữ liệu";
    return { success: false, error: message };
  }
}
async function seed50Profiles() {
  const payloads = Array.from({ length: 50 }, (_, i) =>
    generateOnePayload(i + 1),
  );
  let successCount = 0;
  let failCount = 0;
  for (const payload of payloads) {
    const result = await submitOnboardingForm(payload);
    if (result.success) {
      successCount++;
      console.log(
        `✅ ${payload.studentCode} | ${payload.fullName} | main=${payload.mainSubjectId} | current=[${payload.currentSubjectIds.join(", ")}]`,
      );
    } else {
      failCount++;
      console.error(
        `❌ ${payload.studentCode} | ${payload.fullName} | error=${result.error}`,
      );
    }
    await delay(200); // thêm delay 200ms giữa các request để tránh quá tải server
  }
  console.log("========== KẾT QUẢ ==========");
  console.log(`Thành công: ${successCount}`);
  console.log(`Thất bại   : ${failCount}`);
}
seed50Profiles().catch((err) => {
  console.error("Seed failed:", err);
});
// const payloads = Array.from({ length: 3 }, (_, i) => generateOnePayload(i + 1));
// console.dir(payloads, { depth: null });
