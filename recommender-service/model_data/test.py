

important_cols = [
    "user_id",
    "term_id",
    "main_subject_id",
    "study_year_no",
    "semester_no",
    "avg_score",
    "studied_credits",
    "total_clicks",
    "study_goal",
    "study_mode",
    "gender",
    "region",
    "age_group",
    "age_level",
    "cohort_id",
    "curriculum_id",
    "curriculum_subject_count",
    "enrolled_subject_count",
    "semester_subject_count"
]

# print(profiles[important_cols].head(20))

# sample_user_id = profiles["user_id"].iloc[0]

# sample_profile = profiles[profiles["user_id"] == sample_user_id].copy()

# print(f"=== PROFILE OF USER {sample_user_id} ===")
# print(sample_profile[important_cols].head())

# time_cols = [f"time_slot_{i}" for i in range(42)]
# print("\nTime slots:")
# print(sample_profile[time_cols].head(1).T)

# print("\nSemester context:")
# print(sample_profile[["curriculum_subject_ids", "enrolled_subject_ids", "semester_context_subject_ids"]].head(1))
