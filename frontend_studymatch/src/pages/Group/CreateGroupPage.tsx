import { useState } from "react";
import { initFreeTime } from "../Onboarding/components/constants";
import type {
  FreeTime,
  StudyGoal,
  StudyMode,
} from "../Onboarding/components/types";
import BasicInfoSection from "./components/BasicInfoSection";
import AcademicInfoSection from "./components/AcademicInfoSection";
import MatchingCriteriaSection from "./components/MatchingCriteriaSection";
import GroupSettingsSection from "./components/GroupSettingsSection";
import GroupPreviewSidebar from "./components/GroupPreviewSidebar";
import BottomActionBar from "./components/BottomActionBar";

export default function CreateGroupPage() {
  const [groupName, setGroupName] = useState<string>("");
  const [goalDescription, setGoalDescription] = useState<string>("");
  const [mainSubject, setMainSubject] = useState<string>("");

  const [studyGoal, setStudyGoal] = useState<StudyGoal | "">("");
  const [studyMode, setStudyMode] = useState<StudyMode | "">("");

  const [maxMembers, setMaxMembers] = useState<number>(5);
  const [visibility, setVisibility] = useState<"public" | "private">("public");

  const [freeTime, setFreeTime] = useState<FreeTime>(() => initFreeTime());

  return (
    <div className="min-h-screen bg-transparent font-sans text-slate-900">
      <main className="mx-auto max-w-6xl px-6 py-8 pb-32">
        <header className="mb-10">
          <h1 className="text-3xl font-semibold tracking-tight text-slate-900 md:text-4xl">
            Tạo nhóm học tập
          </h1>
          <p className="mt-2 max-w-2xl text-base leading-relaxed text-slate-600">
            Thiết lập nhóm học tập dựa trên môn học, mục tiêu, hình thức và thời
            gian rảnh.
          </p>
        </header>

        <div className="grid grid-cols-1 gap-8 lg:grid-cols-12">
          <div className="space-y-8 lg:col-span-8">
            <BasicInfoSection
              groupName={groupName}
              goalDescription={goalDescription}
              onChange={(next) => {
                setGroupName(next.groupName);
                setGoalDescription(next.goalDescription);
              }}
            />

            <AcademicInfoSection
              mainSubject={mainSubject}
              onMainSubjectChange={setMainSubject}
            />

            <MatchingCriteriaSection
              goal={studyGoal}
              mode={studyMode}
              onChange={(next) => {
                setStudyGoal(next.goal);
                setStudyMode(next.mode);
              }}
            />

            <GroupSettingsSection
              maxMembers={maxMembers}
              onMaxMembersChange={setMaxMembers}
              visibility={visibility}
              onVisibilityChange={setVisibility}
              freeTime={freeTime}
              onFreeTimeChange={setFreeTime}
            />
          </div>

          <GroupPreviewSidebar
            draft={{
              groupName,
              goalDescription,
              mainSubject,
              studyGoal,
              studyMode,
              maxMembers,
              visibility,
              freeTime,
            }}
          />
        </div>
      </main>

      <BottomActionBar />
    </div>
  );
}
