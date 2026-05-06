import { AutoAwesomeMosaicOutlined } from "@mui/icons-material";
import { useMemo, useState } from "react";
import { DAYS } from "../../Onboarding/components/constants";
import type {
  FreeTime,
  StudyGoal,
  StudyMode,
} from "../../Onboarding/components/types";
import type { MemberSuggestion } from "../types/createGroup";

const members: MemberSuggestion[] = [
  {
    id: 1,
    name: "Nguyễn Văn An",
    description: "Trùng 90% thời gian",
    avatarUrl: "https://i.pravatar.cc/100?img=11",
    colorClass: "text-emerald-600",
  },
  {
    id: 2,
    name: "Trần Thị Mai",
    description: "Cùng môn & mục tiêu",
    avatarUrl: "https://i.pravatar.cc/100?img=5",
    colorClass: "text-indigo-600",
  },
  {
    id: 3,
    name: "Lê Minh Khang",
    description: "Người cầu tiến",
    avatarUrl: "https://i.pravatar.cc/100?img=15",
    colorClass: "text-blue-600",
  },
];

const GOAL_LABEL: Record<StudyGoal, string> = {
  Survivor: "Cần cải thiện",
  "Passive Learner": "Học thiếu chủ động",
  "Standard Learner": "Học ổn định",
  "High Achiever": "Học nổi bật",
};

const MODE_LABEL: Record<StudyMode, string> = {
  mutual_support: "Người tương đồng",
  peer_support: "Người nhỉnh hơn",
  challenge: "Người thử thách",
  support: "Người cần hỗ trợ",
};

interface CreateGroupDraft {
  groupName: string;
  goalDescription: string;
  mainSubject: string;
  studyGoal: StudyGoal | "";
  studyMode: StudyMode | "";
  maxMembers: number;
  visibility: "public" | "private";
  freeTime: FreeTime;
}

export default function GroupPreviewSidebar({
  draft,
}: {
  draft: CreateGroupDraft;
}) {
  const [invitedIds, setInvitedIds] = useState<number[]>([]);

  const totalSelectedSlots = useMemo(() => {
    return DAYS.reduce(
      (acc, d) =>
        acc + Object.values(draft.freeTime[d.id]).filter(Boolean).length,
      0,
    );
  }, [draft.freeTime]);

  const invitedMembers = useMemo(
    () => members.filter((m) => invitedIds.includes(m.id)),
    [invitedIds],
  );

  const visibilityLabel =
    draft.visibility === "public" ? "Công khai" : "Riêng tư";
  const previewName = draft.groupName.trim() || "Tên nhóm của bạn";

  return (
    <aside className="lg:col-span-4">
      <section className="sticky top-24 rounded-2xl border border-slate-200 bg-white p-5">
        <h3 className="text-base font-semibold text-slate-900">
          Xem trước nhóm
        </h3>

        <div className="mt-4 rounded-xl border border-slate-100 bg-slate-50 p-4">
          <div className="flex gap-3">
            <div className="flex size-10 items-center justify-center rounded-xl bg-orange-50 text-orange-600">
              <AutoAwesomeMosaicOutlined fontSize="small" />
            </div>

            <div>
              <p className="text-sm font-semibold text-slate-900">
                {previewName}
              </p>
              <p className="mt-0.5 text-sm text-slate-500">
                {draft.maxMembers} thành viên · {visibilityLabel}
              </p>
            </div>
          </div>

          {draft.goalDescription.trim() && (
            <p className="mt-3 line-clamp-2 text-sm leading-relaxed text-slate-600">
              {draft.goalDescription.trim()}
            </p>
          )}

          <div className="mt-4 flex flex-wrap gap-2">
            {draft.mainSubject ? (
              <Badge>{draft.mainSubject}</Badge>
            ) : (
              <Badge tone="muted">Chưa chọn môn</Badge>
            )}

            {draft.studyGoal ? (
              <Badge>{GOAL_LABEL[draft.studyGoal]}</Badge>
            ) : (
              <Badge tone="muted">Chưa chọn trình độ</Badge>
            )}

            {draft.studyMode ? (
              <Badge>{MODE_LABEL[draft.studyMode]}</Badge>
            ) : (
              <Badge tone="muted">Chưa chọn kiểu ghép</Badge>
            )}

            {totalSelectedSlots > 0 ? (
              <Badge>{totalSelectedSlots} slot rảnh</Badge>
            ) : (
              <Badge tone="muted">Chưa chọn thời gian</Badge>
            )}
          </div>
        </div>

        <div className="mt-6">
          <div className="mb-3 flex items-center justify-between">
            <h3 className="text-sm font-semibold text-slate-900">
              Gợi ý thành viên
            </h3>

            <button className="text-sm font-medium text-orange-600 hover:text-orange-700">
              Xem thêm
            </button>
          </div>

          {invitedMembers.length > 0 && (
            <div className="mb-3">
              <p className="mb-2 text-xs font-semibold text-slate-700">
                Đang mời ({invitedMembers.length})
              </p>
              <div className="flex flex-wrap gap-2">
                {invitedMembers.map((m) => (
                  <span
                    key={m.id}
                    className="rounded-full border border-orange-200 bg-orange-50 px-2 py-1 text-xs font-medium text-orange-700"
                  >
                    {m.name}
                  </span>
                ))}
              </div>
            </div>
          )}

          <div className="space-y-2">
            {members.map((member) => (
              <MemberCard
                key={member.id}
                member={member}
                invited={invitedIds.includes(member.id)}
                onInvite={() =>
                  setInvitedIds((prev) =>
                    prev.includes(member.id) ? prev : [...prev, member.id],
                  )
                }
              />
            ))}
          </div>
        </div>
      </section>
    </aside>
  );
}

function Badge({
  children,
  tone = "default",
}: {
  children: React.ReactNode;
  tone?: "default" | "muted";
}) {
  return (
    <span
      className={
        "rounded-full border px-3 py-1 text-xs font-medium " +
        (tone === "muted"
          ? "border-slate-200 bg-white text-slate-500"
          : "border-slate-200 bg-white text-slate-700")
      }
    >
      {children}
    </span>
  );
}

function MemberCard({
  member,
  invited,
  onInvite,
}: {
  member: MemberSuggestion;
  invited: boolean;
  onInvite: () => void;
}) {
  return (
    <div className="flex items-center justify-between rounded-xl border border-slate-100 bg-white p-3">
      <div className="flex min-w-0 items-center gap-3">
        <div
          className="size-10 shrink-0 rounded-full bg-slate-200 bg-cover bg-center"
          style={{ backgroundImage: `url('${member.avatarUrl}')` }}
        />

        <div className="min-w-0">
          <p className="truncate text-sm font-medium text-slate-900">
            {member.name}
          </p>
          <p className="truncate text-xs text-slate-500">
            {member.description}
          </p>
        </div>
      </div>

      <button
        type="button"
        disabled={invited}
        onClick={onInvite}
        className={
          "ml-3 rounded-lg border px-3 py-1.5 text-xs font-medium transition " +
          (invited
            ? "cursor-not-allowed border-orange-200 bg-orange-50 text-orange-700"
            : "border-slate-200 text-slate-700 hover:border-orange-300 hover:text-orange-600")
        }
      >
        {invited ? "Đang mời" : "Mời"}
      </button>
    </div>
  );
}
