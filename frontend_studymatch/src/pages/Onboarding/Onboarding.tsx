import { useState, useEffect, useCallback, ReactNode } from "react";
import { Check, AlertCircle, Loader } from "lucide-react";
import {
  Cohort,
  FormData,
  StudyPlan,
  StudyPlanOptions,
  TermSelection,
  DAYS,
  STEPS_META,
  initFreeTime,
  syncModuleSlots,
  Step1,
  Step2,
  Step3Goal,
  Step3Mode,
  Step4CurrentPlan,
  Step5,
  Step6,
  Step7,
} from "./components";
import {
  submitOnboardingForm,
  transformFormDataToPayload,
  createSubjectCodeToIdMap,
} from "../../services/OnboardingService";

const API_BASE_URL = "http://localhost:8082/api";

export default function OnboardingFlow() {
  const [step, setStep] = useState<number>(1);
  const [goalSub, setGoalSub] = useState<number>(1);
  const [submitted, setSubmitted] = useState<boolean>(false);
  const [submissionLoading, setSubmissionLoading] = useState<boolean>(false);
  const [submissionError, setSubmissionError] = useState<string>("");
  const [submissionResult, setSubmissionResult] = useState<unknown>(null);
  const [cohorts, setCohorts] = useState<Cohort[]>([]);
  const [cohortsLoading, setCohortsLoading] = useState<boolean>(false);
  const [cohortsError, setCohortsError] = useState<string>("");
  const [studyPlan, setStudyPlan] = useState<StudyPlan | null>(null);
  const [studyPlanLoading, setStudyPlanLoading] = useState<boolean>(false);
  const [studyPlanError, setStudyPlanError] = useState<string>("");
  const [studyPlanOptions, setStudyPlanOptions] =
    useState<StudyPlanOptions | null>(null);
  const [studyPlanOptionsLoading, setStudyPlanOptionsLoading] =
    useState<boolean>(false);
  const [studyPlanOptionsError, setStudyPlanOptionsError] =
    useState<string>("");
  const [mainTermSelection, setMainTermSelection] =
    useState<TermSelection | null>(null);
  const [enrolledTermSelection, setEnrolledTermSelection] =
    useState<TermSelection | null>(null);
  const [mainTermStudyPlan, setMainTermStudyPlan] = useState<StudyPlan | null>(
    null,
  );
  const [mainTermStudyPlanLoading, setMainTermStudyPlanLoading] =
    useState<boolean>(false);
  const [mainTermStudyPlanError, setMainTermStudyPlanError] =
    useState<string>("");
  const [enrolledTermStudyPlan, setEnrolledTermStudyPlan] =
    useState<StudyPlan | null>(null);
  const [enrolledTermStudyPlanLoading, setEnrolledTermStudyPlanLoading] =
    useState<boolean>(false);
  const [enrolledTermStudyPlanError, setEnrolledTermStudyPlanError] =
    useState<string>("");

  const [data, setData] = useState<FormData>({
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
    moduleSlots: {},
    freeTime: initFreeTime(),
    avgScore: 8.0,
    prevAttempts: 0,
    studiedCredits: "",
  });

  const loadCohorts = useCallback(async (): Promise<void> => {
    setCohortsLoading(true);
    setCohortsError("");
    try {
      const res = await fetch(`${API_BASE_URL}/cohorts`);
      if (!res.ok) {
        throw new Error(`Không tải được danh sách khóa học (${res.status})`);
      }
      const json: Cohort[] = await res.json();
      console.log("Cohorts loaded:", json);
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

  const loadStudyPlan = useCallback(
    async (cohortCode: string): Promise<void> => {
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
        const json: StudyPlan = await res.json();
        setStudyPlan(json);
        setData((prev) => ({
          ...prev,
          mainModule: "",
          enrolledModules: [],
          moduleSlots: {},
        }));
      } catch (error) {
        setStudyPlan(null);
        setData((prev) => ({
          ...prev,
          mainModule: "",
          enrolledModules: [],
          moduleSlots: {},
        }));
        setStudyPlanError(
          error instanceof Error
            ? error.message
            : "Không tải được môn học hiện tại",
        );
      } finally {
        setStudyPlanLoading(false);
      }
    },
    [],
  );

  const loadStudyPlanOptions = useCallback(
    async (cohortCode: string): Promise<void> => {
      if (!cohortCode) {
        setStudyPlanOptions(null);
        setStudyPlanOptionsError("");
        setStudyPlanOptionsLoading(false);
        return;
      }

      setStudyPlanOptionsLoading(true);
      setStudyPlanOptionsError("");
      try {
        const res = await fetch(
          `${API_BASE_URL}/cohorts/${cohortCode}/study-plan-options`,
        );
        if (!res.ok) {
          throw new Error(
            `Không tải được danh sách học kỳ của khóa ${cohortCode} (${res.status})`,
          );
        }

        const json: StudyPlanOptions = await res.json();
        setStudyPlanOptions(json);
      } catch (error) {
        setStudyPlanOptions(null);
        setStudyPlanOptionsError(
          error instanceof Error
            ? error.message
            : "Không tải được danh sách học kỳ",
        );
      } finally {
        setStudyPlanOptionsLoading(false);
      }
    },
    [],
  );

  const loadStudyPlanByTerm = useCallback(
    async (
      cohortCode: string,
      selection: TermSelection,
    ): Promise<StudyPlan> => {
      const params = new URLSearchParams({
        studyYearNo: String(selection.studyYearNo),
        semesterNo: String(selection.semesterNo),
        startYearTerm: String(selection.startYearTerm),
        endYearTerm: String(selection.endYearTerm),
      });

      const res = await fetch(
        `${API_BASE_URL}/cohorts/${cohortCode}/study-plan-options/subject?${params.toString()}`,
      );
      if (!res.ok) {
        throw new Error(
          `Không tải được môn học theo học kỳ đã chọn (${res.status})`,
        );
      }
      const json: StudyPlan = await res.json();
      return json;
    },
    [],
  );

  useEffect(() => {
    loadCohorts();
  }, [loadCohorts]);

  useEffect(() => {
    loadStudyPlan(data.cohortCode);
  }, [data.cohortCode, loadStudyPlan]);

  useEffect(() => {
    loadStudyPlanOptions(data.cohortCode);
  }, [data.cohortCode, loadStudyPlanOptions]);

  useEffect(() => {
    if (!data.cohortCode || !mainTermSelection) {
      setMainTermStudyPlan(null);
      setMainTermStudyPlanLoading(false);
      setMainTermStudyPlanError("");
      return;
    }

    setMainTermStudyPlanLoading(true);
    setMainTermStudyPlanError("");
    loadStudyPlanByTerm(data.cohortCode, mainTermSelection)
      .then((plan) => {
        setMainTermStudyPlan(plan);
      })
      .catch((error) => {
        setMainTermStudyPlan(null);
        setMainTermStudyPlanError(
          error instanceof Error
            ? error.message
            : "Không tải được môn chính theo học kỳ đã chọn",
        );
      })
      .finally(() => {
        setMainTermStudyPlanLoading(false);
      });
  }, [data.cohortCode, mainTermSelection, loadStudyPlanByTerm]);

  useEffect(() => {
    if (!data.cohortCode || !enrolledTermSelection) {
      setEnrolledTermStudyPlan(null);
      setEnrolledTermStudyPlanLoading(false);
      setEnrolledTermStudyPlanError("");
      return;
    }

    setEnrolledTermStudyPlanLoading(true);
    setEnrolledTermStudyPlanError("");
    loadStudyPlanByTerm(data.cohortCode, enrolledTermSelection)
      .then((plan) => {
        setEnrolledTermStudyPlan(plan);
      })
      .catch((error) => {
        setEnrolledTermStudyPlan(null);
        setEnrolledTermStudyPlanError(
          error instanceof Error
            ? error.message
            : "Không tải được môn phụ theo học kỳ đã chọn",
        );
      })
      .finally(() => {
        setEnrolledTermStudyPlanLoading(false);
      });
  }, [data.cohortCode, enrolledTermSelection, loadStudyPlanByTerm]);

  const update = useCallback(
    (key: keyof FormData, value: FormData[keyof FormData]): void =>
      setData((p) => {
        if (key === "cohortCode") {
          setMainTermSelection(null);
          setEnrolledTermSelection(null);
          setMainTermStudyPlan(null);
          setMainTermStudyPlanError("");
          setEnrolledTermStudyPlan(null);
          setEnrolledTermStudyPlanError("");
          return {
            ...p,
            cohortCode: value as string,
            mainModule: "",
            enrolledModules: [],
            moduleSlots: {},
          };
        }

        if (key === "mainModule") {
          const nextMainModule = value as string;
          const nextSelectedModules = [
            nextMainModule,
            ...(p.enrolledModules as string[]).filter(
              (code) => code !== nextMainModule,
            ),
          ].filter(Boolean);

          return {
            ...p,
            mainModule: nextMainModule,
            enrolledModules: (p.enrolledModules as string[]).filter(
              (code) => code !== nextMainModule,
            ),
            moduleSlots: syncModuleSlots(p.moduleSlots, nextSelectedModules),
          };
        }

        if (key === "enrolledModules") {
          const normalizedEnrolledModules = Array.isArray(value)
            ? value.filter(
                (moduleCode): moduleCode is string =>
                  typeof moduleCode === "string",
              )
            : [];

          const nextSelectedModules = [
            p.mainModule,
            ...normalizedEnrolledModules.filter(
              (code) => code !== p.mainModule,
            ),
          ].filter(Boolean);

          return {
            ...p,
            enrolledModules: normalizedEnrolledModules.filter(
              (code) => code !== p.mainModule,
            ),
            moduleSlots: syncModuleSlots(p.moduleSlots, nextSelectedModules),
          };
        }

        return { ...p, [key]: value };
      }),
    [],
  );

  const canProceed = (): boolean => {
    if (step === 1) {
      return !!(
        data.fullName.trim() &&
        data.studentId.trim() &&
        data.cohortCode
      );
    }
    if (step === 2) return !!(data.gender && data.region);
    if (step === 3) return goalSub === 1 ? !!data.studyGoal : !!data.studyMode;
    if (step === 4) {
      if (!(data.mainModule && studyPlan)) return false;
      const selectedModules = [data.mainModule, ...data.enrolledModules].filter(
        Boolean,
      );
      return selectedModules.every((moduleCode) =>
        DAYS.some((day) =>
          Object.values(data.moduleSlots[moduleCode]?.[day.id] ?? {}).some(
            Boolean,
          ),
        ),
      );
    }
    if (step === 5) {
      return DAYS.some((d) => Object.values(data.freeTime[d.id]).some(Boolean));
    }
    if (step === 6) return data.studiedCredits !== "";
    return true;
  };

  const handleNext = (): void => {
    if (step === 3) {
      if (goalSub === 1) return setGoalSub(2);
      setGoalSub(1);
      return setStep(4);
    }
    if (step === 4) return setStep(5);
    if (step === 7) {
      // Submit the form
      setSubmissionLoading(true);
      setSubmissionError("");
      setSubmissionResult(null);

      try {
        const subjectCodeToIdMap = createSubjectCodeToIdMap([
          studyPlan,
          mainTermStudyPlan,
          enrolledTermStudyPlan,
        ]);
        const payload = transformFormDataToPayload(
          data,
          cohorts,
          studyPlan,
          subjectCodeToIdMap,
        );

        submitOnboardingForm(payload).then((result) => {
          setSubmissionLoading(false);
          if (result.success) {
            setSubmissionResult(result.data);
            setSubmitted(true);
          } else {
            setSubmissionError(
              result.error || "Lỗi không xác định khi gửi dữ liệu",
            );
          }
        });
      } catch (error) {
        setSubmissionLoading(false);
        setSubmissionError(
          error instanceof Error
            ? error.message
            : "Lỗi không xác định khi gửi dữ liệu",
        );
      }
      return;
    }
    setStep((s) => s + 1);
  };

  const handleBack = (): void => {
    if (step === 3 && goalSub === 2) return setGoalSub(1);
    if (step === 4) return setStep(3);
    if (step > 1) setStep((s) => s - 1);
  };

  const isBackDisabled = step === 1 && goalSub === 1;

  const stepTitle = (): string => {
    if (step === 3) {
      return goalSub === 1
        ? "Trình độ học tập của bạn?"
        : "Cách bạn muốn học cùng người khác?";
    }

    return (
      (
        {
          1: "Xin chào! Hãy bắt đầu nào",
          2: "Thông tin cá nhân",
          4: "Khóa hiện tại và môn học của bạn",
          5: "Thời gian rảnh của bạn",
          6: "Kết quả học tập",
          7: "Xem lại hồ sơ của bạn",
        } as Record<number, string>
      )[step] ?? ""
    );
  };

  const subStepLabel = (): string | null => {
    if (step !== 3) return null;
    return goalSub === 1
      ? "Bước 1/2 – Chọn trình độ"
      : "Bước 2/2 – Chọn phương thức";
  };

  const renderContent = (): ReactNode => {
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
            studyPlanOptions={studyPlanOptions}
            studyPlanOptionsLoading={studyPlanOptionsLoading}
            studyPlanOptionsError={studyPlanOptionsError}
            mainTermSelection={mainTermSelection}
            enrolledTermSelection={enrolledTermSelection}
            setMainTermSelection={setMainTermSelection}
            setEnrolledTermSelection={setEnrolledTermSelection}
            mainTermStudyPlan={mainTermStudyPlan}
            mainTermStudyPlanLoading={mainTermStudyPlanLoading}
            mainTermStudyPlanError={mainTermStudyPlanError}
            enrolledTermStudyPlan={enrolledTermStudyPlan}
            enrolledTermStudyPlanLoading={enrolledTermStudyPlanLoading}
            enrolledTermStudyPlanError={enrolledTermStudyPlanError}
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

  const microStep = step <= 2 ? step : step === 3 ? 2 + goalSub : step + 1;
  const progress = Math.round(((microStep - 1) / 7) * 100);

  if (submitted) {
    return (
      <div className="min-h-screen flex items-center justify-center bg-gradient-to-br from-blue-50 to-indigo-50">
        <div className="bg-white rounded-3xl p-10 max-w-sm w-full mx-4 text-center shadow-sm border border-blue-100">
          {submissionError ? (
            <>
              <div className="text-5xl mb-4"></div>
              <h2 className="text-2xl font-bold text-red-600 mb-2">
                Lỗi gửi dữ liệu
              </h2>
              <p className="text-sm text-gray-600 leading-relaxed mb-6 bg-red-50 border border-red-200 rounded-2xl p-4">
                {submissionError}
              </p>
              <div className="flex gap-3">
                <button
                  onClick={() => {
                    setSubmissionError("");
                    setStep(7);
                  }}
                  className="flex-1 py-3 bg-blue-600 hover:bg-blue-700 text-white rounded-xl font-semibold text-sm transition-colors"
                >
                  Quay lại
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
                      moduleSlots: {},
                      freeTime: initFreeTime(),
                      avgScore: 65,
                      prevAttempts: 0,
                      studiedCredits: "",
                    });
                    setMainTermSelection(null);
                    setEnrolledTermSelection(null);
                    setMainTermStudyPlan(null);
                    setMainTermStudyPlanError("");
                    setEnrolledTermStudyPlan(null);
                    setEnrolledTermStudyPlanError("");
                    setSubmissionError("");
                  }}
                  className="flex-1 py-3 border border-gray-200 text-gray-600 rounded-xl font-semibold text-sm hover:bg-gray-50 transition-colors"
                >
                  Bắt đầu lại
                </button>
              </div>
            </>
          ) : (
            <>
              <div className="text-6xl mb-4">🎉</div>
              <h2 className="text-2xl font-bold text-gray-800 mb-2">
                Hồ sơ hoàn tất!
              </h2>
              <p className="text-sm text-gray-500 leading-relaxed mb-6">
                StudyMatch đang phân tích hồ sơ và tìm kiếm bạn học phù hợp với
                mục tiêu của bạn.
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
                    moduleSlots: {},
                    freeTime: initFreeTime(),
                    avgScore: 65,
                    prevAttempts: 0,
                    studiedCredits: "",
                  });
                  setMainTermSelection(null);
                  setEnrolledTermSelection(null);
                  setMainTermStudyPlan(null);
                  setMainTermStudyPlanError("");
                  setEnrolledTermStudyPlan(null);
                  setEnrolledTermStudyPlanError("");
                  setSubmissionResult(null);
                }}
                className="w-full py-2.5 text-gray-400 border border-gray-200 rounded-xl text-sm hover:bg-gray-50 transition-colors"
              >
                Bắt đầu lại
              </button>
            </>
          )}
        </div>
      </div>
    );
  }

  if (submissionLoading) {
    return (
      <div className="min-h-screen flex items-center justify-center bg-gradient-to-br from-blue-50 to-indigo-50">
        <div className="bg-white rounded-3xl p-10 max-w-sm w-full mx-4 text-center shadow-sm border border-blue-100">
          <Loader className="w-12 h-12 mx-auto text-blue-600 animate-spin mb-4" />
          <h2 className="text-2xl font-bold text-gray-800 mb-2">
            Đang gửi dữ liệu...
          </h2>
          <p className="text-sm text-gray-500">
            Vui lòng đợi trong khi chúng tôi xử lý hồ sơ của bạn
          </p>
        </div>
      </div>
    );
  }

  return (
    <div
      className="min-h-screen flex bg-gray-50"
      style={{ fontFamily: "system-ui, -apple-system, sans-serif" }}
    >
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
                className={`flex items-center gap-2.5 px-3 py-2.5 rounded-xl transition-all ${active ? "bg-blue-600 bg-opacity-20" : "hover:bg-gray-800"}`}
              >
                <div
                  className={`w-7 h-7 rounded-full flex items-center justify-center text-xs font-bold shrink-0 transition-all ${
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
                  className={`text-xs font-medium transition-colors ${
                    active
                      ? "text-white"
                      : s.id < step
                        ? "text-gray-400"
                        : "text-gray-600"
                  }`}
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

      <main className="flex-1 flex items-start justify-center py-10 px-6 overflow-y-auto">
        <div className="w-full max-w-xl">
          <div className="h-1 bg-gray-200 rounded-full mb-8 overflow-hidden">
            <div
              className="h-full bg-blue-500 rounded-full transition-all duration-500"
              style={{ width: `${progress}%` }}
            />
          </div>

          {subStepLabel() && (
            <div className="flex items-center gap-2 mb-3">
              <span className="text-xs font-semibold text-blue-600 bg-blue-50 border border-blue-200 px-3 py-1 rounded-full">
                {subStepLabel()}
              </span>
            </div>
          )}

          <p className="text-xs font-bold text-gray-400 tracking-widest uppercase mb-2">
            Bước {step} / {STEPS_META.length}
          </p>

          <h1 className="text-xl font-bold text-gray-800 mb-1 leading-snug">
            {stepTitle()}
          </h1>
          <p className="text-sm text-gray-400 mb-6">
            {STEPS_META[step - 1]?.label}
          </p>

          <div className="bg-white rounded-2xl border border-gray-100 shadow-sm p-6 mb-5">
            {renderContent()}
          </div>

          <div className="flex gap-3">
            <button
              onClick={handleBack}
              disabled={isBackDisabled}
              className={`px-6 py-3 rounded-xl border text-sm font-medium transition-all ${
                isBackDisabled
                  ? "border-gray-100 text-gray-300 cursor-not-allowed bg-gray-50"
                  : "border-gray-200 text-gray-600 bg-white hover:bg-gray-50 hover:border-gray-300"
              }`}
            >
              ← Quay lại
            </button>
            <button
              onClick={handleNext}
              disabled={!canProceed() || submissionLoading}
              className={`flex-1 py-3 rounded-xl text-sm font-bold transition-all ${
                !canProceed() || submissionLoading
                  ? "bg-blue-100 text-blue-300 cursor-not-allowed"
                  : "bg-blue-600 hover:bg-blue-700 text-white shadow-sm"
              }`}
            >
              {submissionLoading ? (
                <span className="flex items-center justify-center gap-2">
                  <Loader className="w-4 h-4 animate-spin" />
                  Đang gửi...
                </span>
              ) : step === 7 ? (
                "Hoàn tất & Tìm bạn học"
              ) : (
                "Tiếp theo →"
              )}
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
