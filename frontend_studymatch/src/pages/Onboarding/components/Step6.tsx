import { FormData } from "./types";
import { FieldLabel, TInput } from "./Shared";

interface Step6Props {
  data: FormData;
  update: (key: keyof FormData, value: FormData[keyof FormData]) => void;
}

function getScoreLabel(score: number): { text: string; color: string } {
  if (score < 5.0) return { text: "Yếu", color: "text-red-500" };
  if (score < 6.5) return { text: "Trung bình", color: "text-orange-500" };
  if (score < 8.0) return { text: "Khá", color: "text-yellow-600" };
  if (score < 9.0) return { text: "Giỏi", color: "text-blue-600" };
  return { text: "Xuất sắc", color: "text-green-600" };
}

export function Step6({ data, update }: Step6Props) {
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
