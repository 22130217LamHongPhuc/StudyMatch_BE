import { useState, useEffect, useCallback } from "react";
import {
  User,
  IdCard,
  Info,
  Users,
  Calendar,
  MapPin,
  Shield,
  BookOpen,
  NotebookPen,
  Rocket,
  Handshake,
  TrendingUp,
  Zap,
  Sprout,
  Sunrise,
  Sun,
  MoonStar,
  Target,
  CalendarDays,
  BarChart3,
  CircleCheckBig,
  Venus,
  Mars,
  Check,
} from "lucide-react";

/* ════════════════════════════════════════════════
   DATA CONSTANTS
════════════════════════════════════════════════ */
const API_BASE_URL = "http://localhost:8081/api";

function getSortedSubjects(subjects = []) {
  return [...subjects].sort((a, b) => {
    const orderDiff = (a.recommendedOrder ?? 0) - (b.recommendedOrder ?? 0);
    if (orderDiff !== 0) return orderDiff;
    return String(a.subjectCode).localeCompare(String(b.subjectCode));
  });
}

function getSubjectLabel(subject) {
  if (!subject) return "";
  return `${subject.subjectCode} - ${subject.subjectName}`;
}

function getCohortLabel(cohort) {
  if (!cohort) return "";
  return `Khóa ${cohort.cohortCode} • Bắt đầu ${cohort.startYear}`;
}

function getStudyPlanTitle(plan) {
  if (!plan) return "";
  return (
    plan.termFullName ||
    `Học kỳ ${plan.semesterNo} - Năm học ${plan.academicYearStart} - ${plan.academicYearEnd}`
  );
}

const GOALS = [
  {
    key: "Survivor",
    icon: Shield,
    title: "Sinh tồn",
    desc: "Mục tiêu qua môn, không bị trượt. Cần bạn học cùng vực dậy.",
    ring: "ring-red-400",
    bg: "bg-red-50",
    border: "border-red-300",
    text: "text-red-700",
    badge: "bg-red-100 text-red-700 border-red-200",
  },
  {
    key: "Passive Learner",
    icon: BookOpen,
    title: "Học bị động",
    desc: "Học theo lịch, không đặt mục tiêu cao. Cần người nhắc nhở.",
    ring: "ring-yellow-400",
    bg: "bg-yellow-50",
    border: "border-yellow-300",
    text: "text-yellow-700",
    badge: "bg-yellow-100 text-yellow-700 border-yellow-200",
  },
  {
    key: "Standard Learner",
    icon: NotebookPen,
    title: "Học chuẩn",
    desc: "Học đều đặn, muốn hiểu sâu. Tìm bạn để thảo luận và tiến bộ cùng.",
    ring: "ring-blue-400",
    bg: "bg-blue-50",
    border: "border-blue-300",
    text: "text-blue-700",
    badge: "bg-blue-100 text-blue-700 border-blue-200",
  },
  {
    key: "High Achiever",
    icon: Rocket,
    title: "Vươn tới đỉnh",
    desc: "Luôn phấn đấu điểm cao. Tìm nhóm elite để cạnh tranh.",
    ring: "ring-green-400",
    bg: "bg-green-50",
    border: "border-green-300",
    text: "text-green-700",
    badge: "bg-green-100 text-green-700 border-green-200",
  },
];

const VALID_MODES = {
  Survivor: ["mutual_support", "peer_support", "challenge"],
  "Passive Learner": ["mutual_support", "peer_support", "challenge"],
  "Standard Learner": ["mutual_support", "peer_support", "support"],
  "High Achiever": ["mutual_support", "support"],
};

const MODES = {
  mutual_support: {
    icon: Handshake,
    label: "Học cùng người tương đồng",
    desc: "Kết nối với sinh viên có trình độ và mục tiêu tương đương để cùng tiến bộ.",
    bg: "bg-blue-50",
    border: "border-blue-300",
    text: "text-blue-700",
  },
  peer_support: {
    icon: TrendingUp,
    label: "Tìm người nhỉnh hơn một chút",
    desc: "Học cùng người có điểm cao hơn 5–15 điểm để được kéo lên.",
    bg: "bg-violet-50",
    border: "border-violet-300",
    text: "text-violet-700",
  },
  challenge: {
    icon: Zap,
    label: "Tìm thử thách bản thân",
    desc: "Kết nối với High Achiever để được thách thức và phát triển mạnh mẽ.",
    bg: "bg-orange-50",
    border: "border-orange-300",
    text: "text-orange-700",
  },
  support: {
    icon: Sprout,
    label: "Hỗ trợ người khác",
    desc: "Chia sẻ kiến thức, giúp người học yếu hơn để củng cố bản thân.",
    bg: "bg-teal-50",
    border: "border-teal-300",
    text: "text-teal-700",
  },
};

const DAYS = [
  { id: 0, label: "Thứ Hai", short: "T2" },
  { id: 1, label: "Thứ Ba", short: "T3" },
  { id: 2, label: "Thứ Tư", short: "T4" },
  { id: 3, label: "Thứ Năm", short: "T5" },
  { id: 4, label: "Thứ Sáu", short: "T6" },
  { id: 5, label: "Thứ Bảy", short: "T7" },
  { id: 6, label: "Chủ Nhật", short: "CN" },
];

const SLOTS = [
  { id: "morning", label: "Sáng", time: "6–12h", icon: Sunrise },
  { id: "afternoon", label: "Chiều", time: "12–18h", icon: Sun },
  { id: "evening", label: "Tối", time: "18–24h", icon: MoonStar },
];

const STEPS_META = [
  { id: 1, label: "Thông tin cơ bản", icon: User },
  { id: 2, label: "Nhân khẩu học", icon: MapPin },
  { id: 3, label: "Mục tiêu học tập", icon: Target },
  { id: 4, label: "Môn đang học", icon: BookOpen },
  { id: 5, label: "Thời gian rảnh", icon: CalendarDays },
  { id: 6, label: "Kết quả học tập", icon: BarChart3 },
  { id: 7, label: "Xác nhận", icon: CircleCheckBig },
];

const initFreeTime = () =>
  Object.fromEntries(
    DAYS.map((d) => [
      d.id,
      { morning: false, afternoon: false, evening: false },
    ]),
  );

function FieldLabel({ children, className = "" }) {
  return (
    <p
      className={`text-xs font-semibold text-gray-400 uppercase tracking-widest mb-2 ${className}`}
    >
      {children}
    </p>
  );
}

function TInput({ value, onChange, placeholder, type = "text" }) {
  const [focused, setFocused] = useState(false);
  return (
    <input
      type={type}
      value={value}
      placeholder={placeholder}
      onChange={(e) => onChange(e.target.value)}
      onFocus={() => setFocused(true)}
      onBlur={() => setFocused(false)}
      className={`w-full px-4 py-3 rounded-xl border bg-gray-50 text-sm text-gray-800 outline-none transition-all
        ${focused ? "border-blue-400 ring-2 ring-blue-100 bg-white" : "border-gray-200"}`}
    />
  );
}

function Chip({
  active,
  onClick,
  children,
  activeClass = "border-blue-500 bg-blue-50 text-blue-700",
}) {
  return (
    <button
      onClick={onClick}
      className={`px-4 py-2 rounded-xl border text-sm text-gray-700 font-medium transition-all
        ${active ? activeClass : "border-gray-200 bg-gray-50 text-gray-500 hover:bg-gray-100"}`}
    >
      {children}
    </button>
  );
}

/* ════════════════════════════════════════════════
   STEP 1 – Basic Info
════════════════════════════════════════════════ */

function Step1({
  data,
  update,
  cohorts,
  cohortsLoading,
  cohortsError,
  onRetry,
}) {
  const sortedCohorts = [...cohorts].sort(
    (a, b) =>
      b.startYear - a.startYear || Number(b.cohortCode) - Number(a.cohortCode),
  );

  return (
    <div className="space-y-5">
      <div>
        <FieldLabel className="flex items-center gap-2">
          <User size={16} /> Họ và tên
        </FieldLabel>
        <TInput
          value={data.fullName}
          onChange={(v) => update("fullName", v)}
          placeholder="Nguyễn Văn A"
        />
      </div>

      <div>
        <FieldLabel className="flex items-center gap-2">
          <IdCard size={16} /> Mã số sinh viên
        </FieldLabel>
        <TInput
          value={data.studentId}
          onChange={(v) => update("studentId", v)}
          placeholder="2151..."
        />
      </div>

      <div>
        <FieldLabel className="flex items-center gap-2">
          <BookOpen size={16} /> Khóa hiện tại
        </FieldLabel>

        {cohortsLoading ? (
          <div className="rounded-2xl border border-gray-200 bg-gray-50 px-4 py-3 text-sm text-gray-500">
            Đang tải danh sách khóa học...
          </div>
        ) : cohortsError ? (
          <div className="space-y-3 rounded-2xl border border-red-200 bg-red-50 px-4 py-3">
            <p className="text-sm text-red-600">{cohortsError}</p>
            <button
              type="button"
              onClick={onRetry}
              className="text-xs font-semibold text-red-700 underline underline-offset-2"
            >
              Thử tải lại
            </button>
          </div>
        ) : (
          <div className="grid gap-3">
            {sortedCohorts.map((cohort) => {
              const active = data.cohortCode === String(cohort.cohortCode);
              return (
                <button
                  key={cohort.cohortId}
                  type="button"
                  onClick={() =>
                    update("cohortCode", String(cohort.cohortCode))
                  }
                  className={`w-full text-left p-4 rounded-2xl border-2 transition-all duration-150 ${active ? "border-blue-500 bg-blue-50 ring-2 ring-blue-100" : "bg-white border-gray-100 hover:border-gray-200 hover:bg-gray-50"}`}
                >
                  <div className="flex items-center gap-3">
                    <div
                      className={`w-12 h-12 rounded-xl flex items-center justify-center ${active ? "bg-blue-600 text-white" : "bg-gray-100 text-gray-500"}`}
                    >
                      <span className="text-sm font-bold">
                        {cohort.cohortCode}
                      </span>
                    </div>
                    <div className="flex-1 min-w-0">
                      <div className="flex items-center gap-2 flex-wrap">
                        <span
                          className={`font-semibold text-sm ${active ? "text-blue-700" : "text-gray-700"}`}
                        >
                          {getCohortLabel(cohort)}
                        </span>
                        {active && (
                          <span className="text-xs font-medium px-2 py-0.5 rounded-full border bg-blue-100 text-blue-700 border-blue-200">
                            Đã chọn
                          </span>
                        )}
                      </div>
                      <p className="text-xs text-gray-400 mt-0.5 leading-relaxed">
                        Mã khóa: {cohort.cohortCode}
                      </p>
                    </div>
                  </div>
                </button>
              );
            })}
          </div>
        )}
      </div>

      <div className="flex gap-3 bg-blue-50 border border-blue-100 rounded-xl p-4">
        <Info className="text-blue-400 shrink-0" size={20} />
        <p className="text-xs text-blue-600 leading-relaxed">
          Thông tin sẽ được dùng để xây dựng hồ sơ học tập và ghép nhóm tự động.
          Dữ liệu chỉ dùng nội bộ trong hệ thống <strong>StudyMatch</strong>.
        </p>
      </div>
    </div>
  );
}
/* ════════════════════════════════════════════════
   STEP 2 – Demographics
════════════════════════════════════════════════ */

function Step2({ data, update }) {
  const [cities, setCities] = useState([]);

  useEffect(() => {
    //https://provinces.open-api.vn/api/v2/p/
    fetch("https://provinces.open-api.vn/api/v2/p/")
      .then((res) => res.json())
      .then((data) => {
        const cityNames = data.map((c) => c.name);
        setCities(cityNames);
      });
  }, []);
  return (
    <div className="space-y-5">
      <div>
        <FieldLabel className="flex items-center gap-2">
          <Users size={16} /> Giới tính
        </FieldLabel>
        <div className="flex gap-3">
          {[
            ["M", "Nam", Mars],
            ["F", "Nữ", Venus],
          ].map(([val, lbl, Icon]) => {
            const GenderIcon = Icon;
            return (
              <Chip
                key={val}
                active={data.gender === val}
                onClick={() => update("gender", val)}
              >
                <span className="inline-flex items-center gap-2 text-gray-700">
                  <GenderIcon size={16} />
                  {lbl}
                </span>
              </Chip>
            );
          })}
        </div>
      </div>

      <div>
        <FieldLabel className="flex items-center gap-2">
          <Calendar size={16} /> Nhóm tuổi
        </FieldLabel>
        <div className="flex gap-3">
          {["0-35", "35-55", "55<="].map((ag) => (
            <Chip
              key={ag}
              active={data.ageGroup === ag}
              onClick={() => update("ageGroup", ag)}
            >
              {ag}
            </Chip>
          ))}
        </div>
      </div>

      {cities.length > 0 ? (
        <div className="w-full max-w-md">
          <FieldLabel>Chọn khu vực</FieldLabel>
          <div className="relative mt-1">
            <select
              value={data.region || ""}
              onChange={(e) => update("region", e.target.value)}
              className="block w-full rounded-lg border border-gray-300 bg-white py-2 px-3 pr-10 text-gray-800 text-sm truncate
          focus:outline-none focus:border-blue-500 focus:ring-1 focus:ring-blue-200"
              title={data.region || ""}
            >
              <option value="" disabled>
                -- Chọn khu vực --
              </option>
              {cities.map((c) => (
                <option key={c} value={c} title={c}>
                  {c}
                </option>
              ))}
            </select>
          </div>
        </div>
      ) : (
        <p className="text-sm text-gray-500">Đang tải danh sách khu vực...</p>
      )}
    </div>
  );
}

/* ════════════════════════════════════════════════
   STEP 3a – Select Goal / Proficiency Level
════════════════════════════════════════════════ */
function Step3Goal({ data, update }) {
  return (
    <div className="space-y-3">
      <p className="text-sm text-gray-500 mb-1">
        Chọn trình độ học tập phù hợp nhất với bạn hiện tại. Điều này quyết định
        cách hệ thống ghép bạn học.
      </p>
      {GOALS.map((g) => {
        const active = data.studyGoal === g.key;
        const Icon = g.icon;
        return (
          <button
            key={g.key}
            onClick={() => update("studyGoal", g.key)}
            className={`w-full text-left p-4 rounded-2xl border-2 transition-all duration-150
              ${active ? `${g.bg} ${g.border} ring-2 ${g.ring} ring-opacity-30` : "bg-white border-gray-100 hover:border-gray-200 hover:bg-gray-50"}`}
          >
            <div className="flex items-center gap-3">
              <div
                className={`w-11 h-11 rounded-xl flex items-center justify-center ${active ? g.bg : "bg-gray-50"}`}
              >
                <Icon size={22} className={active ? g.text : "text-gray-500"} />
              </div>
              <div className="flex-1 min-w-0">
                <div className="flex items-center gap-2">
                  <span
                    className={`font-semibold text-sm ${active ? g.text : "text-gray-700"}`}
                  >
                    {g.title}
                  </span>
                  {active && (
                    <span
                      className={`text-xs font-medium px-2 py-0.5 rounded-full border ${g.badge}`}
                    >
                      Đã chọn
                    </span>
                  )}
                </div>
                <p className="text-xs text-gray-400 mt-0.5 leading-relaxed">
                  {g.desc}
                </p>
              </div>
              <div
                className={`w-5 h-5 rounded-full border-2 shrink-0 flex items-center justify-center
                ${active ? `${g.border} ${g.bg}` : "border-gray-200"}`}
              >
                {active && (
                  <div
                    className="w-2.5 h-2.5 rounded-full bg-current"
                    style={{ color: active ? "inherit" : "transparent" }}
                  ></div>
                )}
              </div>
            </div>
          </button>
        );
      })}
    </div>
  );
}

/* ════════════════════════════════════════════════
   STEP 3b – Select Mode (based on goal)
════════════════════════════════════════════════ */
function Step3Mode({ data, update }) {
  const goalObj = GOALS.find((g) => g.key === data.studyGoal);
  const availModes = VALID_MODES[data.studyGoal] || [];
  return (
    <div className="space-y-4">
      <div
        className={`inline-flex items-center gap-2 px-3 py-1.5 rounded-xl text-xs font-semibold border ${goalObj?.badge}`}
      >
        {goalObj?.icon && <goalObj.icon size={14} />} {goalObj?.title}
      </div>
      <p className="text-sm text-gray-500">
        Dựa trên trình độ của bạn, hãy chọn{" "}
        <strong>cách bạn muốn học cùng người khác</strong>:
      </p>
      <div className="space-y-3">
        {availModes.map((mKey) => {
          const m = MODES[mKey];
          const active = data.studyMode === mKey;
          const Icon = m.icon;
          return (
            <button
              key={mKey}
              onClick={() => update("studyMode", mKey)}
              className={`w-full text-left p-4 rounded-2xl border-2 transition-all duration-150
                ${active ? `${m.bg} ${m.border}` : "bg-white border-gray-100 hover:border-gray-200 hover:bg-gray-50"}`}
            >
              <div className="flex items-start gap-3">
                <div
                  className={`w-10 h-10 rounded-xl flex items-center justify-center ${active ? m.bg : "bg-gray-50"}`}
                >
                  <Icon
                    size={20}
                    className={active ? m.text : "text-gray-500"}
                  />
                </div>
                <div className="flex-1">
                  <p
                    className={`font-semibold text-sm ${active ? m.text : "text-gray-700"}`}
                  >
                    {m.label}
                  </p>
                  <p className="text-xs text-gray-400 mt-0.5 leading-relaxed">
                    {m.desc}
                  </p>
                </div>
                {active && (
                  <div className="w-5 h-5 rounded-full bg-blue-500 flex items-center justify-center shrink-0 mt-0.5">
                    <Check className="w-3 h-3 text-white" strokeWidth={3} />
                  </div>
                )}
              </div>
            </button>
          );
        })}
      </div>
    </div>
  );
}

/* ════════════════════════════════════════════════
   STEP 4 – Current Cohort Study Plan
════════════════════════════════════════════════ */
function Step4CurrentPlan({
  data,
  update,
  studyPlan,
  studyPlanLoading,
  studyPlanError,
}) {
  const subjects = getSortedSubjects(studyPlan?.subjects || []);

  const toggleSubject = (subjectCode) => {
    const current = data.enrolledModules;
    update(
      "enrolledModules",
      current.includes(subjectCode)
        ? current.filter((code) => code !== subjectCode)
        : [...current, subjectCode],
    );
  };

  return (
    <div className="space-y-4">
      <div className="flex flex-wrap gap-2">
        {data.cohortCode && (
          <span className="bg-blue-100 text-blue-700 text-xs font-semibold px-3 py-1 rounded-full">
            Khóa {data.cohortCode}
          </span>
        )}
        {studyPlan?.termFullName && (
          <span className="bg-indigo-100 text-indigo-700 text-xs font-semibold px-3 py-1 rounded-full">
            {studyPlan.termFullName}
          </span>
        )}
        {studyPlan?.studyYearNo ? (
          <span className="bg-gray-100 text-gray-700 text-xs font-semibold px-3 py-1 rounded-full">
            Năm học {studyPlan.studyYearNo}
          </span>
        ) : null}
      </div>

      <p className="text-sm text-gray-500">
        Hệ thống tự tải chương trình học hiện tại của khóa bạn đã chọn. Chọn môn
        chính trước, sau đó có thể chọn thêm các môn khác đang học.
      </p>

      {studyPlanLoading ? (
        <div className="rounded-2xl border border-gray-200 bg-gray-50 px-4 py-3 text-sm text-gray-500">
          Đang tải danh sách môn học hiện tại...
        </div>
      ) : studyPlanError ? (
        <div className="rounded-2xl border border-red-200 bg-red-50 px-4 py-3 text-sm text-red-600">
          {studyPlanError}
        </div>
      ) : !studyPlan ? (
        <div className="rounded-2xl border border-gray-200 bg-gray-50 px-4 py-3 text-sm text-gray-500">
          Hãy quay lại step 1 để chọn khóa, sau đó hệ thống sẽ load môn học
          tương ứng.
        </div>
      ) : (
        <>
          <div className="rounded-2xl border border-blue-100 bg-blue-50 px-4 py-3 space-y-1">
            <p className="text-sm font-semibold text-blue-700 truncate">
              {studyPlan.curriculumName}
            </p>
            <p className="text-xs text-blue-500">
              {getStudyPlanTitle(studyPlan)}
            </p>
          </div>

          <div className="space-y-2">
            <p className="text-xs font-semibold text-gray-400 uppercase tracking-widest">
              Chọn môn chính
            </p>
            {subjects.map((subject) => {
              const active = data.mainModule === subject.subjectCode;
              return (
                <button
                  key={subject.subjectId}
                  type="button"
                  onClick={() =>
                    update("mainModule", String(subject.subjectCode))
                  }
                  className={`w-full text-left px-4 py-3.5 rounded-xl border-2 transition-all flex items-center gap-4 ${active ? "border-blue-500 bg-blue-50" : "border-gray-200 bg-white hover:border-gray-300 hover:bg-gray-50"}`}
                >
                  <span
                    className={`text-xs font-bold w-16 py-1 text-center rounded-lg ${active ? "bg-blue-500 text-white" : "bg-gray-100 text-gray-500"}`}
                  >
                    {subject.subjectCode}
                  </span>
                  <span
                    className={`text-sm flex-1 ${active ? "text-blue-700 font-medium" : "text-gray-700"}`}
                  >
                    {subject.subjectName}
                  </span>
                  <div className="flex items-center gap-2">
                    {active && (
                      <div className="w-5 h-5 rounded-full bg-blue-500 flex items-center justify-center shrink-0">
                        <Check className="w-3 h-3 text-white" strokeWidth={3} />
                      </div>
                    )}
                  </div>
                </button>
              );
            })}
          </div>

          <div className="space-y-2">
            <p className="text-xs font-semibold text-gray-400 uppercase tracking-widest">
              Môn khác đang học
            </p>
            {subjects
              .filter((subject) => subject.subjectCode !== data.mainModule)
              .map((subject) => {
                const active = data.enrolledModules.includes(
                  subject.subjectCode,
                );
                return (
                  <button
                    key={subject.subjectId}
                    type="button"
                    onClick={() => toggleSubject(String(subject.subjectCode))}
                    className={`w-full text-left px-4 py-3.5 rounded-xl border-2 transition-all flex items-center gap-3 ${active ? "border-emerald-500 bg-emerald-50" : "border-gray-200 bg-white hover:border-gray-300 hover:bg-gray-50"}`}
                  >
                    <div className="flex items-center gap-2">
                      {active && (
                        <div className="w-5 h-5 rounded-full bg-blue-500 flex items-center justify-center shrink-0">
                          <Check
                            className="w-3 h-3 text-white"
                            strokeWidth={3}
                          />
                        </div>
                      )}
                    </div>
                    <span
                      className={`text-xs font-bold w-16 py-1 text-center rounded-lg `}
                    >
                      {subject.subjectCode}
                    </span>
                    <span
                      className={`text-sm flex-1 ${active ? "text-emerald-700 font-medium" : "text-gray-700"}`}
                    >
                      {subject.subjectName}
                    </span>
                  </button>
                );
              })}
          </div>

          {data.enrolledModules.length > 0 && (
            <div className="flex items-center gap-2 flex-wrap bg-emerald-50 border border-emerald-200 rounded-xl px-4 py-2.5">
              <span className="text-xs text-emerald-600 font-semibold">
                Đã chọn:
              </span>
              {data.enrolledModules.map((code) => (
                <span
                  key={code}
                  className="text-xs font-bold text-emerald-700 bg-emerald-200 px-2 py-0.5 rounded-md"
                >
                  {code}
                </span>
              ))}
            </div>
          )}

          <div className="text-xs text-gray-400">
            Tổng số môn hiện tại: {subjects.length}
          </div>
        </>
      )}
    </div>
  );
}

/* ════════════════════════════════════════════════
   STEP 5 – Free Time (3 slots per day)
════════════════════════════════════════════════ */
function Step5({ data, update }) {
  const toggle = (dayId, slotId) => {
    update("freeTime", {
      ...data.freeTime,
      [dayId]: {
        ...data.freeTime[dayId],
        [slotId]: !data.freeTime[dayId][slotId],
      },
    });
  };

  const selectAll = (slotId) => {
    const allOn = DAYS.every((d) => data.freeTime[d.id][slotId]);
    const updated = { ...data.freeTime };
    DAYS.forEach((d) => {
      updated[d.id] = { ...updated[d.id], [slotId]: !allOn };
    });
    update("freeTime", updated);
  };

  const totalSelected = DAYS.reduce(
    (acc, d) => acc + Object.values(data.freeTime[d.id]).filter(Boolean).length,
    0,
  );

  return (
    <div className="space-y-4">
      <div className="flex items-center justify-between">
        <p className="text-sm text-gray-500">
          Chọn các khung giờ rảnh trong tuần để hệ thống ghép bạn học cùng lịch.
        </p>
        {totalSelected > 0 && (
          <span className="text-xs font-semibold text-blue-600 bg-blue-50 border border-blue-200 px-2 py-1 rounded-full whitespace-nowrap">
            {totalSelected} slot
          </span>
        )}
      </div>

      {/* Table */}
      <div className="overflow-x-auto -mx-1">
        <table className="w-full min-w-max">
          <thead>
            <tr>
              <th className="w-16 pb-3"></th>
              {SLOTS.map((s) => {
                const Icon = s.icon;
                return (
                  <th key={s.id} className="pb-3 text-center">
                    <button
                      onClick={() => selectAll(s.id)}
                      className="flex flex-col items-center gap-1 mx-auto group"
                      title={`Chọn tất cả ${s.label}`}
                    >
                      <Icon
                        size={18}
                        className="text-gray-500 group-hover:text-blue-600"
                      />
                      <span className="text-xs font-semibold text-gray-700 group-hover:text-blue-600">
                        {s.label}
                      </span>
                      <span className="text-xs text-gray-400">{s.time}</span>
                    </button>
                  </th>
                );
              })}
            </tr>
          </thead>
          <tbody className="space-y-1">
            {DAYS.map((d, di) => {
              const daySlots = data.freeTime[d.id];
              const dayCount = Object.values(daySlots).filter(Boolean).length;
              const isWeekend = d.id >= 5;
              return (
                <tr
                  key={d.id}
                  className={di % 2 === 0 ? "bg-gray-50 rounded-xl" : ""}
                >
                  <td className="py-2 pr-3">
                    <div className="flex items-center gap-1.5">
                      <span
                        className={`text-sm font-semibold ${isWeekend ? "text-orange-500" : "text-gray-700"}`}
                      >
                        {d.short}
                      </span>
                      {dayCount > 0 && (
                        <span className="w-4 h-4 rounded-full bg-blue-500 text-white text-xs flex items-center justify-center font-bold">
                          {dayCount}
                        </span>
                      )}
                    </div>
                  </td>
                  {SLOTS.map((s) => {
                    const active = daySlots[s.id];
                    const colors = {
                      morning: active
                        ? "bg-blue-300 border-amber-400 text-white"
                        : "border-gray-200 bg-white text-gray-300 hover:border-amber-300 hover:bg-amber-50",
                      afternoon: active
                        ? "bg-blue-400 border-blue-400 text-white"
                        : "border-gray-200 bg-white text-gray-300 hover:border-blue-300 hover:bg-blue-50",
                      evening: active
                        ? "bg-indigo-500 border-indigo-500 text-white"
                        : "border-gray-200 bg-white text-gray-300 hover:border-indigo-300 hover:bg-indigo-50",
                    };
                    return (
                      <td key={s.id} className="py-2 px-2 text-center">
                        <button
                          onClick={() => toggle(d.id, s.id)}
                          className={`w-full py-2.5 rounded-xl border-2 text-xs font-semibold transition-all duration-100 ${colors[s.id]}`}
                        >
                          {active ? "✓" : "—"}
                        </button>
                      </td>
                    );
                  })}
                </tr>
              );
            })}
          </tbody>
        </table>
      </div>

      {/* Legend */}
      <div className="flex items-center gap-4 pt-1">
        <span className="text-xs text-gray-400">
          Nhấn vào tiêu đề cột để chọn cả ngày •
        </span>
        {[
          { color: "bg-blue-300", label: "Sáng" },
          { color: "bg-blue-400", label: "Chiều" },
          { color: "bg-indigo-500", label: "Tối" },
        ].map((l) => (
          <div key={l.label} className="flex items-center gap-1.5">
            <div className={`w-3 h-3 rounded ${l.color}`}></div>
            <span className="text-xs text-gray-500">{l.label}</span>
          </div>
        ))}
      </div>
    </div>
  );
}

/* ════════════════════════════════════════════════
   STEP 6 – Academic Background
════════════════════════════════════════════════ */

function getScoreLabel(score) {
  if (score < 5.0) {
    return { text: "Yếu", color: "text-red-500" };
  }
  if (score < 6.5) {
    return { text: "Trung bình", color: "text-orange-500" };
  }
  if (score < 8.0) {
    return { text: "Khá", color: "text-yellow-600" };
  }
  if (score < 9.0) {
    return { text: "Giỏi", color: "text-blue-600" };
  }
  return { text: "Xuất sắc", color: "text-green-600" };
}

function Step6({ data, update }) {
  const score = data.avgScore;
  const scoreLabel = getScoreLabel(score);

  return (
    <div className="space-y-6">
      <div>
        <FieldLabel>Điểm trung bình hiện tại</FieldLabel>

        <div className="flex items-center gap-4 mt-3">
          <input
            type="range"
            min={0}
            max={10}
            step={0.1}
            value={score}
            onChange={(e) => update("avgScore", Number(e.target.value))}
            className="flex-1 accent-blue-500"
          />

          <div className="text-right shrink-0">
            <div className="text-2xl font-bold text-gray-800">
              {score.toFixed(1)}
            </div>
            <div className={`text-xs font-semibold ${scoreLabel.color}`}>
              {scoreLabel.text}
            </div>
          </div>
        </div>
      </div>

      <div>
        <FieldLabel>Số tín chỉ đã tích lũy</FieldLabel>
        <TInput
          value={data.studiedCredits}
          onChange={(v) => update("studiedCredits", v)}
          placeholder="Ví dụ: 60"
          type="number"
        />
      </div>
    </div>
  );
}
/* ════════════════════════════════════════════════
   STEP 7 – Review
════════════════════════════════════════════════ */
function Step7({ data, studyPlan }) {
  const goalObj = GOALS.find((g) => g.key === data.studyGoal);
  const modeObj = data.studyMode ? MODES[data.studyMode] : null;
  const allMods = [data.mainModule, ...data.enrolledModules].filter(Boolean);

  const freeTimeNormalized = Object.fromEntries(
    DAYS.map((d) => {
      const slots = data.freeTime[d.id];
      const count = Object.values(slots).filter(Boolean).length;
      return [`day_${d.id}_activity`, parseFloat((count / 3).toFixed(4))];
    }),
  );
  const subjectFlags = Object.fromEntries(
    allMods.map((code) => [`subject_${code}`, 1]),
  );
  const vector = {
    cohort_code: data.cohortCode,
    gender_F: data.gender === "F" ? 1 : 0,
    gender_M: data.gender === "M" ? 1 : 0,
    age_encoded: { "0-35": 1, "35-55": 2, "55<=": 3 }[data.ageGroup] || 0,
    num_of_prev_attempts: data.prevAttempts,
    avg_score: data.avgScore,
    ...freeTimeNormalized,
    ...subjectFlags,
    study_goal: data.studyGoal,
    mode: data.studyMode,
    code_module: data.mainModule,
    code_presentation: studyPlan?.termFullName || data.cohortCode,
  };

  const topDays = DAYS.filter((d) =>
    Object.values(data.freeTime[d.id]).some(Boolean),
  );

  return (
    <div className="space-y-4">
      {/* Header card */}
      <div
        className={`rounded-2xl border-2 p-4 ${goalObj?.bg} ${goalObj?.border}`}
      >
        <div className="flex items-center gap-3 mb-4">
          <div
            className={`w-12 h-12 rounded-2xl flex items-center justify-center text-2xl border-2 ${goalObj?.border} bg-white`}
          >
            {goalObj?.icon && (
              <goalObj.icon size={24} className={goalObj?.text} />
            )}
          </div>
          <div className="flex-1 min-w-0">
            <p className="font-bold text-gray-800 truncate">
              {data.fullName || "—"}
            </p>
            <p className="text-xs text-gray-500">
              MSSV: {data.studentId || "—"}
            </p>
          </div>
          <div className="text-right shrink-0">
            <span
              className={`text-xs font-semibold px-2 py-1 rounded-lg border ${goalObj?.badge}`}
            >
              {goalObj?.title}
            </span>
            {modeObj && (
              <p className="text-xs text-gray-500 mt-1">
                {modeObj.icon && (
                  <modeObj.icon size={14} className="inline mr-1.5" />
                )}{" "}
                {modeObj.label}
              </p>
            )}
          </div>
        </div>
        <div className="grid grid-cols-2 gap-x-4 gap-y-0 text-sm border-t border-white border-opacity-60 pt-3">
          {[
            [
              "Giới tính",
              data.gender === "M" ? "Nam" : data.gender === "F" ? "Nữ" : "—",
            ],
            ["Khu vực", data.region || "—"],
            ["Khóa hiện tại", data.cohortCode || "—"],
            [
              "Môn chính",
              data.mainModule
                ? getSubjectLabel(
                    studyPlan?.subjects?.find(
                      (subject) => subject.subjectCode === data.mainModule,
                    ) || {
                      subjectCode: data.mainModule,
                      subjectName: data.mainModule,
                    },
                  )
                : "—",
            ],
            ["Điểm TB", `${data.avgScore}/100`],
            ["Lần học lại", `${data.prevAttempts} lần`],
          ].map(([k, v]) => (
            <div
              key={k}
              className="flex justify-between py-1.5 border-b border-white border-opacity-40 gap-2"
            >
              <span className="text-gray-500 shrink-0">{k}</span>
              <span
                className={`font-medium text-right truncate ${goalObj?.text}`}
              >
                {v}
              </span>
            </div>
          ))}
        </div>
      </div>

      {/* 2-col cards */}
      <div className="grid grid-cols-2 gap-3">
        <div className="bg-green-50 border border-green-200 rounded-xl p-3">
          <p className="text-xs font-semibold text-green-700 uppercase tracking-wider mb-2">
            Môn đang học
          </p>
          <div className="flex flex-wrap gap-1.5">
            {data.mainModule && (
              <span className="text-xs font-bold text-blue-700 bg-blue-100 border border-blue-200 px-2 py-0.5 rounded-md">
                ★ {data.mainModule}
              </span>
            )}
            {data.enrolledModules.map((m) => (
              <span
                key={m}
                className="text-xs font-medium text-green-700 bg-green-100 px-2 py-0.5 rounded-md"
              >
                {m}
              </span>
            ))}
            {allMods.length === 0 && (
              <span className="text-xs text-gray-400">Chưa chọn</span>
            )}
          </div>
          {studyPlan?.termFullName && (
            <p className="text-[11px] text-green-600 mt-2">
              {studyPlan.termFullName}
            </p>
          )}
        </div>

        <div className="bg-indigo-50 border border-indigo-200 rounded-xl p-3">
          <p className="text-xs font-semibold text-indigo-700 uppercase tracking-wider mb-2">
            Thời gian rảnh
          </p>
          {topDays.slice(0, 5).map((d) => {
            const ft = data.freeTime[d.id];
            return (
              <div key={d.id} className="flex items-center gap-1.5 mb-1">
                <span className="text-xs font-medium text-indigo-600 w-6">
                  {d.short}
                </span>
                {ft.morning && <Sunrise size={14} className="text-amber-500" />}
                {ft.afternoon && <Sun size={14} className="text-blue-500" />}
                {ft.evening && (
                  <MoonStar size={14} className="text-indigo-500" />
                )}
              </div>
            );
          })}
          {topDays.length > 5 && (
            <p className="text-xs text-indigo-400">
              +{topDays.length - 5} ngày khác
            </p>
          )}
          {topDays.length === 0 && (
            <p className="text-xs text-gray-400">Chưa chọn</p>
          )}
        </div>
      </div>

      {/* Feature vector */}
      <details className="bg-gray-50 border border-gray-200 rounded-xl overflow-hidden">
        <summary className="px-4 py-3 text-xs font-semibold text-gray-500 cursor-pointer select-none hover:text-gray-700">
          🔬 Xem vector đặc trưng gửi vào mô hình gợi ý
        </summary>
        <pre className="px-4 pb-4 pt-1 text-xs text-gray-400 overflow-x-auto whitespace-pre-wrap leading-relaxed">
          {JSON.stringify(vector, null, 2)}
        </pre>
      </details>
    </div>
  );
}

/* ════════════════════════════════════════════════
   MAIN COMPONENT
════════════════════════════════════════════════ */
export default function OnboardingFlow() {
  const [step, setStep] = useState(1);
  const [goalSub, setGoalSub] = useState(1); // 1=pick goal, 2=pick mode
  const [submitted, setSubmitted] = useState(false);
  const [cohorts, setCohorts] = useState([]);
  const [cohortsLoading, setCohortsLoading] = useState(false);
  const [cohortsError, setCohortsError] = useState("");
  const [studyPlan, setStudyPlan] = useState(null);
  const [studyPlanLoading, setStudyPlanLoading] = useState(false);
  const [studyPlanError, setStudyPlanError] = useState("");
  const [data, setData] = useState({
    fullName: "",
    studentId: "",
    gender: "",
    ageGroup: "",
    region: "",
    studyGoal: "",
    studyMode: "",
    cohortCode: "",
    mainModule: "",
    enrolledModules: [],
    freeTime: initFreeTime(),
    avgScore: 8.0,
    prevAttempts: 0,
    studiedCredits: "",
  });

  // Inject Tailwind CDN
  useEffect(() => {
    if (!document.getElementById("tw-cdn")) {
      const link = document.createElement("link");
      link.id = "tw-cdn";
      link.rel = "stylesheet";
      link.href =
        "https://cdn.jsdelivr.net/npm/tailwindcss@2.2.19/dist/tailwind.min.css";
      document.head.appendChild(link);
    }
  }, []);

  const loadCohorts = useCallback(async () => {
    setCohortsLoading(true);
    setCohortsError("");
    try {
      const res = await fetch(`${API_BASE_URL}/cohorts`);
      if (!res.ok) {
        throw new Error(`Không tải được danh sách khóa học (${res.status})`);
      }
      const json = await res.json();
      setCohorts(Array.isArray(json) ? json : []);
    } catch (error) {
      setCohorts([]);
      setCohortsError(
        error instanceof Error
          ? error.message
          : "Không tải được danh sách khóa học",
      );
    } finally {
      setCohortsLoading(false);
    }
  }, []);

  const loadStudyPlan = useCallback(async (cohortCode) => {
    if (!cohortCode) {
      setStudyPlan(null);
      setStudyPlanError("");
      setStudyPlanLoading(false);
      return;
    }

    setStudyPlanLoading(true);
    setStudyPlanError("");
    try {
      const res = await fetch(
        `${API_BASE_URL}/cohorts/${cohortCode}/study-plan/current`,
      );
      if (!res.ok) {
        throw new Error(
          `Không tải được môn học của khóa ${cohortCode} (${res.status})`,
        );
      }
      const json = await res.json();
      setStudyPlan(json);
      setData((prev) => ({
        ...prev,
        mainModule: "",
        enrolledModules: [],
      }));
    } catch (error) {
      setStudyPlan(null);
      setData((prev) => ({
        ...prev,
        mainModule: "",
        enrolledModules: [],
      }));
      setStudyPlanError(
        error instanceof Error
          ? error.message
          : "Không tải được môn học hiện tại",
      );
    } finally {
      setStudyPlanLoading(false);
    }
  }, []);

  useEffect(() => {
    loadCohorts();
  }, [loadCohorts]);

  useEffect(() => {
    loadStudyPlan(data.cohortCode);
  }, [data.cohortCode, loadStudyPlan]);

  const update = useCallback(
    (key, value) =>
      setData((p) => {
        if (key === "cohortCode") {
          return {
            ...p,
            cohortCode: value,
            mainModule: "",
            enrolledModules: [],
          };
        }
        if (key === "mainModule") {
          return {
            ...p,
            mainModule: value,
            enrolledModules: p.enrolledModules.filter((code) => code !== value),
          };
        }
        if (key === "enrolledModules") {
          return {
            ...p,
            enrolledModules: Array.isArray(value)
              ? value.filter((code) => code !== p.mainModule)
              : value,
          };
        }
        return { ...p, [key]: value };
      }),
    [],
  );

  const canProceed = () => {
    if (step === 1)
      return data.fullName.trim() && data.studentId.trim() && data.cohortCode;
    if (step === 2) return data.gender && data.region;
    if (step === 3) return goalSub === 1 ? !!data.studyGoal : !!data.studyMode;
    if (step === 4) return !!data.mainModule && !!studyPlan;
    if (step === 5)
      return DAYS.some((d) => Object.values(data.freeTime[d.id]).some(Boolean));
    if (step === 6) return data.studiedCredits !== "";
    return true;
  };

  const handleNext = () => {
    if (step === 3) {
      if (goalSub === 1) return setGoalSub(2);
      setGoalSub(1);
      return setStep(4);
    }
    if (step === 4) return setStep(5);
    if (step === 7) return setSubmitted(true);
    setStep((s) => s + 1);
  };

  const handleBack = () => {
    if (step === 3 && goalSub === 2) return setGoalSub(1);
    if (step === 4) return setStep(3);
    if (step > 1) setStep((s) => s - 1);
  };

  const isBackDisabled = step === 1 && goalSub === 1;

  const stepTitle = () => {
    if (step === 3)
      return goalSub === 1
        ? "Trình độ học tập của bạn?"
        : "Cách bạn muốn học cùng người khác?";
    return {
      1: "Xin chào! Hãy bắt đầu nào 👋",
      2: "Thông tin cá nhân",
      4: "Khóa hiện tại và môn học của bạn",
      5: "Thời gian rảnh của bạn",
      6: "Kết quả học tập",
      7: "Xem lại hồ sơ của bạn",
    }[step];
  };

  const subStepLabel = () => {
    if (step === 3)
      return goalSub === 1
        ? "Bước 1/2 – Chọn trình độ"
        : "Bước 2/2 – Chọn phương thức";
    return null;
  };

  const renderContent = () => {
    switch (step) {
      case 1:
        return (
          <Step1
            data={data}
            update={update}
            cohorts={cohorts}
            cohortsLoading={cohortsLoading}
            cohortsError={cohortsError}
            onRetry={loadCohorts}
          />
        );
      case 2:
        return <Step2 data={data} update={update} />;
      case 3:
        return goalSub === 1 ? (
          <Step3Goal data={data} update={update} />
        ) : (
          <Step3Mode data={data} update={update} />
        );
      case 4:
        return (
          <Step4CurrentPlan
            data={data}
            update={update}
            studyPlan={studyPlan}
            studyPlanLoading={studyPlanLoading}
            studyPlanError={studyPlanError}
          />
        );
      case 5:
        return <Step5 data={data} update={update} />;
      case 6:
        return <Step6 data={data} update={update} />;
      case 7:
        return <Step7 data={data} studyPlan={studyPlan} />;
      default:
        return null;
    }
  };

  // Micro-step progress (11 total micro-steps)
  const microStep = step <= 2 ? step : step === 3 ? 2 + goalSub : step + 1;
  const progress = Math.round(((microStep - 1) / 7) * 100);

  /* ── Submitted screen ── */
  if (submitted) {
    return (
      <div className="min-h-screen flex items-center justify-center bg-gradient-to-br from-blue-50 to-indigo-50">
        <div className="bg-white rounded-3xl p-10 max-w-sm w-full mx-4 text-center shadow-sm border border-blue-100">
          <div className="text-6xl mb-4">🎉</div>
          <h2 className="text-2xl font-bold text-gray-800 mb-2">
            Hồ sơ hoàn tất!
          </h2>
          <p className="text-sm text-gray-500 leading-relaxed mb-6">
            StudyMatch đang phân tích hồ sơ và tìm kiếm bạn học phù hợp với mục
            tiêu của bạn.
          </p>
          <div className="bg-blue-50 border border-blue-100 rounded-2xl p-4 mb-6 text-left">
            <p className="text-xs font-semibold text-blue-600 mb-1">
              Mục tiêu: {data.studyGoal}
            </p>
            <p className="text-xs text-blue-500">
              Môn chính: {data.mainModule} • Phương thức: {data.studyMode}
            </p>
          </div>
          <button
            onClick={() => setSubmitted(false)}
            className="w-full py-3 bg-blue-600 hover:bg-blue-700 text-white rounded-xl font-semibold text-sm transition-colors mb-3"
          >
            Xem gợi ý bạn học →
          </button>
          <button
            onClick={() => {
              setSubmitted(false);
              setStep(1);
              setGoalSub(1);
              setData({
                fullName: "",
                studentId: "",
                gender: "",
                ageGroup: "",
                region: "",
                studyGoal: "",
                studyMode: "",
                cohortCode: "",
                mainModule: "",
                enrolledModules: [],
                freeTime: initFreeTime(),
                avgScore: 65,
                prevAttempts: 0,
                studiedCredits: "",
              });
            }}
            className="w-full py-2.5 text-gray-400 border border-gray-200 rounded-xl text-sm hover:bg-gray-50 transition-colors"
          >
            Bắt đầu lại
          </button>
        </div>
      </div>
    );
  }

  /* ── Main layout ── */
  return (
    <div
      className="min-h-screen flex bg-gray-50"
      style={{ fontFamily: "system-ui, -apple-system, sans-serif" }}
    >
      {/* ── Sidebar ── */}
      <aside className="w-56 bg-gray-900 flex flex-col shrink-0 sticky top-0 h-screen">
        <div className="px-5 pt-6 pb-4 border-b border-gray-800">
          <div className="text-lg font-bold text-white tracking-tight">
            Study<span className="text-blue-400">Match</span>
          </div>
          <div className="text-xs text-gray-500 mt-0.5">
            Nông Lâm · Khoa CNTT
          </div>
        </div>

        <nav className="flex-1 px-3 py-4 space-y-1 overflow-y-auto">
          {STEPS_META.map((s) => {
            const active = s.id === step;
            const Icon = s.icon;
            return (
              <div
                key={s.id}
                className={`flex items-center gap-2.5 px-3 py-2.5 rounded-xl transition-all
                  ${active ? "bg-blue-600 bg-opacity-20" : "hover:bg-gray-800"}`}
              >
                <div
                  className={`w-7 h-7 rounded-full flex items-center justify-center text-xs font-bold shrink-0 transition-all
                  ${
                    s.id < step
                      ? "bg-green-500 text-white"
                      : active
                        ? "bg-blue-500 text-white ring-2 ring-blue-400 ring-opacity-40"
                        : "bg-gray-800 text-gray-500"
                  }`}
                >
                  {s.id < step ? (
                    <Check className="w-4 h-4" strokeWidth={3} />
                  ) : (
                    <Icon className="w-4 h-4" />
                  )}
                </div>
                <span
                  className={`text-xs font-medium transition-colors
                  ${active ? "text-white" : s.id < step ? "text-gray-400" : "text-gray-600"}`}
                >
                  {s.label}
                </span>
              </div>
            );
          })}
        </nav>

        <div className="px-5 py-4 border-t border-gray-800">
          <div className="flex justify-between text-xs mb-1.5">
            <span className="text-gray-500">Tiến độ</span>
            <span className="text-blue-400 font-semibold">{progress}%</span>
          </div>
          <div className="h-1.5 bg-gray-800 rounded-full overflow-hidden">
            <div
              className="h-full bg-blue-500 rounded-full transition-all duration-500"
              style={{ width: `${progress}%` }}
            />
          </div>
        </div>
      </aside>

      {/* ── Main content ── */}
      <main className="flex-1 flex items-start justify-center py-10 px-6 overflow-y-auto">
        <div className="w-full max-w-lg">
          {/* Top progress bar */}
          <div className="h-1 bg-gray-200 rounded-full mb-8 overflow-hidden">
            <div
              className="h-full bg-blue-500 rounded-full transition-all duration-500"
              style={{ width: `${progress}%` }}
            />
          </div>

          {/* Sub-step label */}
          {subStepLabel() && (
            <div className="flex items-center gap-2 mb-3">
              <span className="text-xs font-semibold text-blue-600 bg-blue-50 border border-blue-200 px-3 py-1 rounded-full">
                {subStepLabel()}
              </span>
            </div>
          )}

          {/* Step counter */}
          <p className="text-xs font-bold text-gray-400 tracking-widest uppercase mb-2">
            Bước {step} / {STEPS_META.length}
          </p>

          {/* Title */}
          <h1 className="text-xl font-bold text-gray-800 mb-1 leading-snug">
            {stepTitle()}
          </h1>
          <p className="text-sm text-gray-400 mb-6">
            {STEPS_META[step - 1]?.label}
          </p>

          {/* Content card */}
          <div className="bg-white rounded-2xl border border-gray-100 shadow-sm p-6 mb-5">
            {renderContent()}
          </div>

          {/* Navigation */}
          <div className="flex gap-3">
            <button
              onClick={handleBack}
              disabled={isBackDisabled}
              className={`px-6 py-3 rounded-xl border text-sm font-medium transition-all
                ${
                  isBackDisabled
                    ? "border-gray-100 text-gray-300 cursor-not-allowed bg-gray-50"
                    : "border-gray-200 text-gray-600 bg-white hover:bg-gray-50 hover:border-gray-300"
                }`}
            >
              ← Quay lại
            </button>
            <button
              onClick={handleNext}
              disabled={!canProceed()}
              className={`flex-1 py-3 rounded-xl text-sm font-bold transition-all
                ${
                  canProceed()
                    ? "bg-blue-600 hover:bg-blue-700 text-white shadow-sm"
                    : "bg-blue-100 text-blue-300 cursor-not-allowed"
                }`}
            >
              {step === 7 ? "Hoàn tất & Tìm bạn học" : "Tiếp theo →"}
            </button>
          </div>

          {step < 7 && (
            <p className="text-center text-xs text-gray-300 mt-3">
              Dữ liệu bạn nhập giúp mô hình gợi ý chính xác hơn
            </p>
          )}
        </div>
      </main>
    </div>
  );
}
