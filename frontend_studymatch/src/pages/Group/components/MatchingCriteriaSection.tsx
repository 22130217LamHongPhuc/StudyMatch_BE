import { useEffect, useMemo } from "react";
import { Handshake, Sprout, TrendingUp, Zap, Users } from "lucide-react";
import type { StudyGoal, StudyMode } from "../../Onboarding/components/types";
import { VALID_MODES } from "../../Onboarding/components/constants";
import OptionCard from "./OptionCard";
import PurposeCard from "./PurposeCard";
import SectionCard from "./SectionCard";

const MODE_CONFIG: Record<
  StudyMode,
  {
    label: string;
    description: string;
    icon: React.ReactNode;
  }
> = {
  mutual_support: {
    icon: <Handshake className="h-5 w-5 text-blue-600" />,
    label: "Người tương đồng",
    description: "Cùng trình độ, cùng mục tiêu để học ổn định và tiến bộ đều.",
  },
  peer_support: {
    icon: <TrendingUp className="h-5 w-5 text-violet-600" />,
    label: "Người nhỉnh hơn",
    description: "Cao hơn một chút để kéo nhóm lên mà không quá chênh lệch.",
  },
  challenge: {
    icon: <Zap className="h-5 w-5 text-orange-600" />,
    label: "Người thử thách",
    description: "Tìm bạn học giỏi hơn để tạo áp lực tích cực và bứt phá.",
  },
  support: {
    icon: <Sprout className="h-5 w-5 text-teal-600" />,
    label: "Người cần hỗ trợ",
    description:
      "Tìm bạn yếu hơn để nhóm chia sẻ kiến thức và củng cố lại bài.",
  },
};

interface MatchingCriteriaSectionProps {
  goal: StudyGoal | "";
  mode: StudyMode | "";
  onChange: (next: { goal: StudyGoal | ""; mode: StudyMode | "" }) => void;
}

export default function MatchingCriteriaSection({
  goal,
  mode,
  onChange,
}: MatchingCriteriaSectionProps) {
  const availableModes = useMemo<StudyMode[]>(
    () => (goal ? (VALID_MODES[goal] ?? []) : []),
    [goal],
  );

  useEffect(() => {
    if (!goal) {
      if (mode) onChange({ goal: "", mode: "" });
      return;
    }

    if (mode && availableModes.includes(mode)) return;
    onChange({ goal, mode: availableModes[0] ?? "" });
  }, [goal, mode, availableModes, onChange]);

  return (
    <SectionCard
      icon={<Users className="h-5 w-5" />}
      title="Tiêu chí ghép nhóm"
    >
      <div className="space-y-8">
        <div>
          <p className="mb-1 text-sm font-semibold text-slate-800">
            Trình độ hiện tại của nhóm
          </p>
          <p className="mb-4 text-sm text-slate-500">
            Chọn mức học tập phản ánh đúng tình trạng hiện tại của nhóm.
          </p>

          <div className="grid grid-cols-1 gap-4 md:grid-cols-2">
            <OptionCard
              name="learner_type"
              value="Survivor"
              checked={goal === "Survivor"}
              onChange={(value) => onChange({ goal: value as StudyGoal, mode })}
              title="Cần cải thiện"
              description="Kiến thức nền tảng còn yếu hoặc chỉ ở mức cơ bản. Nhóm cần thêm định hướng và hỗ trợ để theo kịp môn học."
            />

            <OptionCard
              name="learner_type"
              value="Passive Learner"
              checked={goal === "Passive Learner"}
              onChange={(value) => onChange({ goal: value as StudyGoal, mode })}
              title="Học thiếu chủ động"
              description="Kiến thức khá ổn nhưng thường học thiếu chủ động. Nhóm cần động lực và người cùng học để duy trì nhịp học tập."
            />

            <OptionCard
              name="learner_type"
              value="Standard Learner"
              checked={goal === "Standard Learner"}
              onChange={(value) => onChange({ goal: value as StudyGoal, mode })}
              title="Học ổn định"
              description="Có kiến thức ổn và tham gia học tập khá tích cực. Nhóm muốn trao đổi, luyện tập và tiến bộ đều đặn."
            />

            <OptionCard
              name="learner_type"
              value="High Achiever"
              checked={goal === "High Achiever"}
              onChange={(value) => onChange({ goal: value as StudyGoal, mode })}
              title="Học nổi bật"
              description="Có nền tảng kiến thức tốt và rất chủ động trong học tập. Nhóm hướng tới thành tích cao và thử thách bản thân."
            />
          </div>
        </div>

        {goal && (
          <div>
            <p className="mb-1 text-sm font-semibold text-slate-800">
              Kiểu thành viên muốn ghép
            </p>
            <p className="mb-4 text-sm text-slate-500">
              Chọn kiểu sinh viên mà nhóm muốn kết nối để hệ thống gợi ý phù hợp
              hơn.
            </p>

            <div className="grid grid-cols-2 gap-3 md:grid-cols-4">
              {availableModes.map((availableMode) => {
                const config = MODE_CONFIG[availableMode];
                return (
                  <PurposeCard
                    key={availableMode}
                    icon={config.icon}
                    label={config.label}
                    description={config.description}
                    value={availableMode}
                    checked={mode === availableMode}
                    onChange={(value) =>
                      onChange({ goal, mode: value as StudyMode })
                    }
                  />
                );
              })}
            </div>
          </div>
        )}
      </div>
    </SectionCard>
  );
}
