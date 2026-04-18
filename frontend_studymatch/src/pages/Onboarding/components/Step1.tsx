import { User, IdCard, BookOpen, Info } from "lucide-react";
import { FormData, Cohort } from "./types";
import { getCohortLabel } from "./constants";
import { FieldLabel, TInput } from "./Shared";

interface Step1Props {
  data: FormData;
  update: (key: keyof FormData, value: FormData[keyof FormData]) => void;
  cohorts: Cohort[];
  cohortsLoading: boolean;
  cohortsError: string;
  onRetry: () => void;
}

export function Step1({
  data,
  update,
  cohorts,
  cohortsLoading,
  cohortsError,
  onRetry,
}: Step1Props) {
  const sortedCohorts = [...cohorts].sort(
    (a, b) =>
      b.startAcademicYear - a.startAcademicYear ||
      Number(b.cohortCode) - Number(a.cohortCode),
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
