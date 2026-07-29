import pandas as pd
import numpy as np
from sqlalchemy import create_engine, text
from typing import Optional, List, Dict, Any

# =========================================================
import os

DATABASE_URL = os.getenv("DATABASE_URL")
if not DATABASE_URL:
    DB_USER = os.getenv("DB_USER", "root")
    DB_PASSWORD = os.getenv("DB_PASSWORD", "")
    DB_HOST = os.getenv("DB_HOST", "localhost")
    DB_PORT = os.getenv("DB_PORT", "3306")
    DB_NAME = os.getenv("DB_NAME", "db_profile_service")
    import urllib.parse
    encoded_user = urllib.parse.quote_plus(DB_USER)
    encoded_password = urllib.parse.quote_plus(DB_PASSWORD)
    if DB_HOST.startswith("/"):
        DATABASE_URL = f"mysql+pymysql://{encoded_user}:{encoded_password}@/{DB_NAME}?unix_socket={DB_HOST}&charset=utf8mb4"
    else:
        DATABASE_URL = f"mysql+pymysql://{encoded_user}:{encoded_password}@{DB_HOST}:{DB_PORT}/{DB_NAME}?charset=utf8mb4"
else:
    # Ensure pymysql dialect is specified
    if DATABASE_URL.startswith("mysql://"):
        DATABASE_URL = DATABASE_URL.replace("mysql://", "mysql+pymysql://", 1)



engine = create_engine(DATABASE_URL, echo=False)

print("✅ Database engine created successfully")


# =========================================================
# 2) LOAD TABLES FROM DATABASE
# =========================================================
def load_tables_from_db(engine):
    tables = {}

    query_map = {
        "student_profiles": "SELECT * FROM student_profiles",
        "student_term_profiles": "SELECT * FROM student_term_profiles",
        "student_free_time_slots": "SELECT * FROM student_free_time_slots",
        "student_subject_enrollments": "SELECT * FROM student_subject_enrollments",
        "cohorts": "SELECT * FROM cohorts",
        "curriculum_term_subjects": "SELECT * FROM curriculum_term_subjects",
        "academic_terms": "SELECT * FROM academic_terms",
        "subjects": "SELECT * FROM subjects"
    }

    with engine.connect() as conn:
        for table_name, query in query_map.items():
            tables[table_name] = pd.read_sql(text(query), conn)
            print(f"✅ Loaded {table_name}: {tables[table_name].shape}")

    return tables

tables = load_tables_from_db(engine)

student_profiles = tables["student_profiles"]
student_term_profiles = tables["student_term_profiles"]
student_free_time_slots = tables["student_free_time_slots"]
student_subject_enrollments = tables["student_subject_enrollments"]
cohorts = tables["cohorts"]
curriculum_term_subjects = tables["curriculum_term_subjects"]
academic_terms = tables["academic_terms"]
subjects = tables["subjects"]


# =========================================================
# 2.1) RELOAD USER-SCOPED DATA FROM DATABASE
# =========================================================
def reload_user_from_db(user_id: int) -> dict:
    """
    Reload dữ liệu theo user_id từ DB và cập nhật các DataFrame global.

    Tables cập nhật:
    - student_profiles
    - student_term_profiles
    - student_free_time_slots
    - student_subject_enrollments
    """
    if user_id is None or int(user_id) <= 0:
        raise ValueError("user_id phải là số nguyên dương.")

    uid = int(user_id)

    queries = {
        "student_profiles": ("SELECT * FROM student_profiles WHERE user_id = :uid", True),
        "student_term_profiles": ("SELECT * FROM student_term_profiles WHERE user_id = :uid", False),
        "student_free_time_slots": ("SELECT * FROM student_free_time_slots WHERE user_id = :uid", False),
        "student_subject_enrollments": ("SELECT * FROM student_subject_enrollments WHERE user_id = :uid", False),
    }

    fresh = {}
    with engine.connect() as conn:
        db_info = pd.read_sql(
            text("""
                SELECT
                    DATABASE() AS current_database,
                    @@hostname AS mysql_host,
                    @@port AS mysql_port,
                    USER() AS mysql_user
            """),
            conn,
        )

        print("========== PYTHON REAL DB ==========")
        print(db_info)
        print("====================================")

        for name, (sql, required) in queries.items():
            df = pd.read_sql(text(sql), conn, params={"uid": uid})

            print(f"========== FRESH {name} FROM DB ==========")
            print(df)
            print("==========================================")

            if required and df.empty:
                raise ValueError(f"Không tìm thấy {name} cho user_id={uid}.")

            fresh[name] = df

    def replace_user_rows(df: pd.DataFrame, new_rows: pd.DataFrame) -> pd.DataFrame:
        if df is None or df.empty:
            return new_rows.copy()
        if "user_id" not in df.columns:
            return df
        kept = df[df["user_id"] != uid].copy()
        if new_rows is None or new_rows.empty:
            return kept.reset_index(drop=True)
        return pd.concat([kept, new_rows], ignore_index=True)

    global student_profiles, student_term_profiles, student_free_time_slots, student_subject_enrollments
    student_profiles = replace_user_rows(student_profiles, fresh["student_profiles"])
    student_term_profiles = replace_user_rows(student_term_profiles, fresh["student_term_profiles"])
    student_free_time_slots = replace_user_rows(student_free_time_slots, fresh["student_free_time_slots"])
    student_subject_enrollments = replace_user_rows(student_subject_enrollments, fresh["student_subject_enrollments"])

    return {k: int(v.shape[0]) for k, v in fresh.items()}

# =========================================================
# 3) BUILD BASE RECORDS
# =========================================================
def fetch_total_study_minutes_map() -> Dict[int, float]:
    """
    Calls StudySessionController endpoint to get total study minutes for all users.
    Returns a mapping of {user_id: total_minutes}.
    """
    total_minutes_map = {}
    try:
        import requests
        url = "http://localhost:8080/api/study-sessions/attendance/users/total-minutes"
        resp = requests.get(url, timeout=5)
        if resp.status_code == 200:
            body = resp.json()
            if body.get("success") and isinstance(body.get("data"), list):
                for item in body["data"]:
                    u_id = item.get("userId")
                    t_mins = item.get("totalMinutes")
                    if u_id is not None and t_mins is not None:
                        total_minutes_map[int(u_id)] = float(t_mins)
    except Exception as exc:
        print(f"⚠️ Cannot fetch total study minutes from API: {exc}")
    return total_minutes_map


def build_base_records(
    student_term_profiles: pd.DataFrame,
    student_profiles: pd.DataFrame,
    cohorts: pd.DataFrame,
    academic_terms: pd.DataFrame,
    subjects: Optional[pd.DataFrame] = None
) -> pd.DataFrame:
    """
    Mỗi dòng = 1 record recommender:
    (user_id, term_id, main_subject_id)
    """

    required_cols = [
        "user_id",
        "term_id",
        "study_year_no",
        "semester_no",
        "avg_score",
        "studied_credits",
        "study_goal",
        "study_mode",
        "main_subject_id"
    ]

    base = student_term_profiles[required_cols].copy()

    # ---- B2. loại record chưa có môn chính
    base = base[base["main_subject_id"].notna()].copy()

    # ---- B2.1. nối tên môn học chính (main_subject_name) từ table subjects
    if subjects is not None and not subjects.empty:
        subj_df = subjects[["subject_id", "subject_name"]].copy().rename(
            columns={"subject_name": "main_subject_name"}
        )
        try:
            base["main_subject_id"] = base["main_subject_id"].astype(float)
            subj_df["subject_id"] = subj_df["subject_id"].astype(float)
        except Exception:
            pass
        base = base.merge(
            subj_df,
            left_on="main_subject_id",
            right_on="subject_id",
            how="left"
        )
        if "subject_id" in base.columns:
            base = base.drop(columns=["subject_id"])
    else:
        base["main_subject_name"] = None

    # ---- B3. nối demographic + full_name + avatar_url từ student_profiles
    sp_cols = ["user_id", "gender", "age_group", "region", "cohort_id"]
    if "avatar_url" in student_profiles.columns:
        sp_cols.append("avatar_url")

    profile_name_col = next(
        (c for c in ["full_name", "fullname", "name"] if c in student_profiles.columns),
        None,
    )
    if profile_name_col is not None:
        sp_cols.append(profile_name_col)

    student_profile_df = student_profiles[sp_cols].copy()
    if profile_name_col is not None and profile_name_col != "full_name":
        student_profile_df = student_profile_df.rename(columns={profile_name_col: "full_name"})
    elif profile_name_col is None:
        student_profile_df["full_name"] = None

    base = base.merge(
        student_profile_df,
        on="user_id",
        how="left"
    )

    if "avatar_url" not in base.columns:
        base["avatar_url"] = None

    # ---- B4. nối cohort -> curriculum
    cohort_cols = ["cohort_id", "curriculum_id", "start_academic_year", "total_study_years"]
    base = base.merge(
        cohorts[cohort_cols],
        on="cohort_id",
        how="left"
    )

    # ---- B5. nối metadata của term
    term_cols = [
        "term_id",
        "academic_year_start",
        "academic_year_end",
        "semester_no",
        "full_name",
        "status"
    ]
    available_term_cols = [c for c in term_cols if c in academic_terms.columns]
    term_df = academic_terms[available_term_cols].copy().rename(
        columns={"semester_no": "term_semester_no", "full_name": "term_full_name"}
    )

    base = base.merge(
        term_df,
        on="term_id",
        how="left"
    )

    # ---- B6. total_clicks lấy từ tổng số phút học tập của từng người dùng
    total_minutes_map = fetch_total_study_minutes_map()
    base["total_clicks"] = base["user_id"].map(total_minutes_map).fillna(0.0)

    # ---- B7. bỏ duplicate nếu có
    base = base.drop_duplicates(subset=["user_id", "term_id", "main_subject_id"]).reset_index(drop=True)

    print(f"✅ Base records built: {base.shape}")
    return base

# =========================================================
# 4) MAP AGE GROUP -> AGE LEVEL
# =========================================================
AGE_MAP = {
    "0-35": 0,
    "35-55": 1,
    "55<=": 2,
}

def map_age_level(df: pd.DataFrame) -> pd.DataFrame:
    out = df.copy()

    out["age_group"] = out["age_group"].astype(str).str.strip()
    out["age_level"] = out["age_group"].map(AGE_MAP)

    # fallback nếu thiếu
    out["age_level"] = out["age_level"].fillna(0).astype(int)

    return out

# =========================================================
# 5) BUILD TIME VECTOR 42
# =========================================================
SLOT_MAP = {
    "ca1": 0,
    "ca2": 1,
    "ca3": 2,
    "ca4": 3,
    "ca5": 4,
    "ca6": 5,
}

def build_time_features(student_free_time_slots: pd.DataFrame) -> pd.DataFrame:
    """
    Output:
    user_id, term_id, time_slot_0 ... time_slot_41
    """
    df = student_free_time_slots.copy()

    # chỉ lấy slot available
    df = df[df["is_available"] == 1].copy()

    # chuẩn hóa slot_code
    df["slot_code"] = df["slot_code"].astype(str).str.lower().str.strip()

    # giữ slot hợp lệ
    df = df[df["slot_code"].isin(SLOT_MAP.keys())].copy()

    # đảm bảo day_of_week hợp lệ
    df = df[df["day_of_week"].between(0, 6)].copy()

    # tính slot_index = day*6 + ca
    df["slot_index"] = df["day_of_week"] * 6 + df["slot_code"].map(SLOT_MAP)

    df["value"] = 1

    time_ohe = (
        df.pivot_table(
            index=["user_id", "term_id"],
            columns="slot_index",
            values="value",
            aggfunc="max",
            fill_value=0
        )
        .reset_index()
    )

    # đảm bảo đủ 42 cột
    for i in range(42):
        if i not in time_ohe.columns:
            time_ohe[i] = 0

    # đổi tên cột
    rename_map = {i: f"time_slot_{i}" for i in range(42)}
    time_ohe = time_ohe.rename(columns=rename_map)

    ordered_cols = ["user_id", "term_id"] + [f"time_slot_{i}" for i in range(42)]
    time_ohe = time_ohe[ordered_cols].copy()

    print(f"✅ Time features built: {time_ohe.shape}")
    return time_ohe

# =========================================================
# 6) BUILD ENROLLMENT CONTEXT
# =========================================================
def build_enrollment_context(student_subject_enrollments: pd.DataFrame) -> pd.DataFrame:
    """
    Output:
    user_id, term_id, enrolled_subject_ids, enrolled_subject_count
    """
    enroll = student_subject_enrollments.copy()

    grouped = (
        enroll.groupby(["user_id", "term_id"])["subject_id"]
        .agg(lambda x: sorted(set(x)))
        .reset_index(name="enrolled_subject_ids")
    )

    grouped["enrolled_subject_count"] = grouped["enrolled_subject_ids"].apply(len)

    print(f"✅ Enrollment context built: {grouped.shape}")
    return grouped


# =========================================================
# 7) BUILD CURRICULUM CONTEXT
# =========================================================
def build_curriculum_context(
    base_records: pd.DataFrame,
    curriculum_term_subjects: pd.DataFrame
) -> pd.DataFrame:
    """
    Output:
    user_id, term_id, main_subject_id, curriculum_subject_ids, curriculum_subject_count
    """
    key_cols = [
        "user_id",
        "term_id",
        "main_subject_id",
        "curriculum_id",
        "study_year_no",
        "semester_no"
    ]

    temp = base_records[key_cols].drop_duplicates().copy()

    cts = curriculum_term_subjects[[
        "curriculum_id",
        "study_year_no",
        "semester_no",
        "subject_id"
    ]].copy()

    merged = temp.merge(
        cts,
        on=["curriculum_id", "study_year_no", "semester_no"],
        how="left"
    )

    curriculum_ctx = (
        merged.groupby(["user_id", "term_id", "main_subject_id"])["subject_id"]
        .agg(lambda x: sorted(set([v for v in x if pd.notna(v)])))
        .reset_index(name="curriculum_subject_ids")
    )

    curriculum_ctx["curriculum_subject_count"] = curriculum_ctx["curriculum_subject_ids"].apply(len)

    print(f"✅ Curriculum context built: {curriculum_ctx.shape}")
    return curriculum_ctx


# =========================================================
# 8) BUILD FINAL SEMESTER CONTEXT
# =========================================================
def build_final_semester_context(
    base_records: pd.DataFrame,
    curriculum_ctx: pd.DataFrame,
    enrollment_ctx: pd.DataFrame
) -> pd.DataFrame:
    """
    Output:
    user_id, term_id, main_subject_id,
    curriculum_subject_ids,
    enrolled_subject_ids,
    semester_context_subject_ids,
    semester_subject_count
    """
    df = base_records[["user_id", "term_id", "main_subject_id"]].drop_duplicates().copy()

    df = df.merge(
        curriculum_ctx,
        on=["user_id", "term_id", "main_subject_id"],
        how="left"
    )

    df = df.merge(
        enrollment_ctx,
        on=["user_id", "term_id"],
        how="left"
    )

    df["curriculum_subject_ids"] = df["curriculum_subject_ids"].apply(
        lambda x: x if isinstance(x, list) else []
    )
    df["enrolled_subject_ids"] = df["enrolled_subject_ids"].apply(
        lambda x: x if isinstance(x, list) else []
    )

    def merge_context(row):
        merged = set(row["curriculum_subject_ids"]) | set(row["enrolled_subject_ids"])
        merged.discard(row["main_subject_id"])  # bỏ môn chính khỏi context phụ
        return sorted(merged)

    df["semester_context_subject_ids"] = df.apply(merge_context, axis=1)
    df["semester_subject_count"] = df["semester_context_subject_ids"].apply(len)

    print(f"✅ Final semester context built: {df.shape}")
    return df

# =========================================================
# 9) BUILD CANONICAL PROFILES
# =========================================================
def build_canonical_profiles(
    student_profiles: pd.DataFrame,
    student_term_profiles: pd.DataFrame,
    student_free_time_slots: pd.DataFrame,
    student_subject_enrollments: pd.DataFrame,
    cohorts: pd.DataFrame,
    curriculum_term_subjects: pd.DataFrame,
    academic_terms: pd.DataFrame,
    subjects: Optional[pd.DataFrame] = None
) -> pd.DataFrame:

    if subjects is None:
        subjects = globals().get("subjects", None)

    # 1. base
    base = build_base_records(
        student_term_profiles=student_term_profiles,
        student_profiles=student_profiles,
        cohorts=cohorts,
        academic_terms=academic_terms,
        subjects=subjects
    )

    # 2. age level
    base = map_age_level(base)

    # 3. time
    time_features = build_time_features(student_free_time_slots)

    # 4. enrollment context
    enrollment_ctx = build_enrollment_context(student_subject_enrollments)

    # 5. curriculum context
    curriculum_ctx = build_curriculum_context(base, curriculum_term_subjects)

    # 6. final semester context
    final_semester_ctx = build_final_semester_context(
        base_records=base,
        curriculum_ctx=curriculum_ctx,
        enrollment_ctx=enrollment_ctx
    )

    # 7. merge all
    profiles = (
        base.merge(
            time_features,
            on=["user_id", "term_id"],
            how="left"
        )
        .merge(
            final_semester_ctx,
            on=["user_id", "term_id", "main_subject_id"],
            how="left"
        )
    )

    # fill time slots
    time_cols = [f"time_slot_{i}" for i in range(42)]
    for c in time_cols:
        if c not in profiles.columns:
            profiles[c] = 0

    profiles[time_cols] = profiles[time_cols].fillna(0).astype(int)

    # fill list cols
    list_cols = [
        "curriculum_subject_ids",
        "enrolled_subject_ids",
        "semester_context_subject_ids"
    ]
    for c in list_cols:
        if c in profiles.columns:
            profiles[c] = profiles[c].apply(lambda x: x if isinstance(x, list) else [])

    # fill count cols
    count_cols = [
        "curriculum_subject_count",
        "enrolled_subject_count",
        "semester_subject_count"
    ]
    for c in count_cols:
        if c in profiles.columns:
            profiles[c] = profiles[c].fillna(0).astype(int)

    # Keep total_clicks values from base, or fillna if missing
    if "total_clicks" in profiles.columns:
        profiles["total_clicks"] = profiles["total_clicks"].fillna(0.0)
    else:
        profiles["total_clicks"] = 0.0

    # làm sạch numeric
    profiles["avg_score"] = pd.to_numeric(profiles["avg_score"], errors="coerce").fillna(0.0)
    profiles["studied_credits"] = pd.to_numeric(profiles["studied_credits"], errors="coerce").fillna(0.0)

    # làm sạch text
    for c in ["gender", "region", "study_goal", "study_mode"]:
        if c in profiles.columns:
            profiles[c] = profiles[c].astype(str).str.strip()

    profiles = profiles.drop_duplicates(subset=["user_id", "term_id", "main_subject_id"]).reset_index(drop=True)

    print(f"Canonical profiles built successfully: {profiles.shape}")
    return profiles



# =========================================================
# 10) BUILD SEMESTER MULTI-HOT
# =========================================================
def build_semester_multihot(profiles: pd.DataFrame) -> pd.DataFrame:
    """
    Input:
        profiles phải có:
        - user_id
        - term_id
        - main_subject_id
        - semester_context_subject_ids (list[int])

    Output:
        user_id, term_id, main_subject_id, semester_has_subject_<id>...
    """
    df = profiles[[
        "user_id",
        "term_id",
        "main_subject_id",
        "semester_context_subject_ids"
    ]].copy()

    all_subject_ids = sorted({
        sid
        for items in df["semester_context_subject_ids"]
        if isinstance(items, list)
        for sid in items
    })

    for sid in all_subject_ids:
        col = f"semester_has_subject_{sid}"
        df[col] = df["semester_context_subject_ids"].apply(
            lambda items: int(sid in items) if isinstance(items, list) else 0
        )

    ordered_cols = ["user_id", "term_id", "main_subject_id"] + [
        f"semester_has_subject_{sid}" for sid in all_subject_ids
    ]

    semester_multihot = df[ordered_cols].copy()
    print(f"Semester multihot built: {semester_multihot.shape}")
    return semester_multihot


# =========================================================
# 11) BUILD FINAL USER FEATURES TABLE
# =========================================================
def build_final_user_features(profiles: pd.DataFrame) -> pd.DataFrame:
    user_features = profiles.copy()

    # Làm sạch numeric
    numeric_cols = ["avg_score", "studied_credits", "total_clicks", "age_level", "semester_subject_count"]
    for c in numeric_cols:
        if c in user_features.columns:
            user_features[c] = pd.to_numeric(user_features[c], errors="coerce").fillna(0)

    # Làm sạch categorical/text
    text_cols = ["gender", "region", "study_goal", "study_mode"]
    for c in text_cols:
        if c in user_features.columns:
            user_features[c] = user_features[c].astype(str).str.strip()

    # time cols
    time_cols = [f"time_slot_{i}" for i in range(42)]
    for c in time_cols:
        if c not in user_features.columns:
            user_features[c] = 0
    user_features[time_cols] = user_features[time_cols].fillna(0).astype(int)

    # bảo đảm list cols
    list_cols = [
        "curriculum_subject_ids",
        "enrolled_subject_ids",
        "semester_context_subject_ids"
    ]
    for c in list_cols:
        if c in user_features.columns:
            user_features[c] = user_features[c].apply(lambda x: x if isinstance(x, list) else [])

    user_features = user_features.drop_duplicates(
        subset=["user_id", "term_id", "main_subject_id"]
    ).reset_index(drop=True)

    print(f"✅ Final user_features built: {user_features.shape}")
    return user_features

from sklearn.preprocessing import OneHotEncoder, MinMaxScaler

# =========================================================
# 12) BUILD FEATURE MATRIX
# =========================================================
def build_feature_matrix_from_user_features(user_features: pd.DataFrame):
    df = user_features.copy().reset_index(drop=True)

    # -------------------------
    # A. One-hot categorical
    # -------------------------
    nominal_cols = ["gender", "region"]
    for c in nominal_cols:
        if c not in df.columns:
            df[c] = "UNKNOWN"

    oh_enc = OneHotEncoder(sparse_output=False, handle_unknown="ignore")
    encoded_cats = oh_enc.fit_transform(df[nominal_cols])
    encoded_cat_df = pd.DataFrame(
        encoded_cats,
        columns=oh_enc.get_feature_names_out(nominal_cols)
    )

    # -------------------------
    # B. Numerical + time
    # -------------------------
    num_cols = [
        "avg_score",
        "studied_credits",
        "total_clicks",
        "age_level",
        "semester_subject_count"
    ]

    for c in num_cols:
        if c not in df.columns:
            df[c] = 0

    time_cols = [f"time_slot_{i}" for i in range(42)]
    for c in time_cols:
        if c not in df.columns:
            df[c] = 0

    semester_cols = [c for c in df.columns if c.startswith("semester_has_subject_")]

    scale_cols = num_cols + time_cols + semester_cols

    scaler = MinMaxScaler()
    scaled_vals = scaler.fit_transform(df[scale_cols])
    scaled_df = pd.DataFrame(scaled_vals, columns=scale_cols)

    # -------------------------
    # C. Group weights
    # -------------------------
    weights = {
        "time": 0.50,
        "academic": 0.20,
        "demographic": 0.10,
        "semester_context": 0.20
    }

    weighted_cat = encoded_cat_df.copy()
    weighted_num = scaled_df.copy()

    # demographic
    weighted_cat = weighted_cat * weights["demographic"]
    if "age_level" in weighted_num.columns:
        weighted_num["age_level"] = weighted_num["age_level"] * weights["demographic"]

    # academic
    academic_cols = ["avg_score", "studied_credits", "total_clicks"]
    for c in academic_cols:
        if c in weighted_num.columns:
            weighted_num[c] = weighted_num[c] * weights["academic"]

    # time
    for c in time_cols:
        if c in weighted_num.columns:
            weighted_num[c] = weighted_num[c] * weights["time"]

    # semester context
    for c in semester_cols:
        if c in weighted_num.columns:
            weighted_num[c] = weighted_num[c] * weights["semester_context"]

    # có thể cho semester_subject_count trọng số nhẹ
    if "semester_subject_count" in weighted_num.columns:
        weighted_num["semester_subject_count"] = weighted_num["semester_subject_count"] * 0.05

    # -------------------------
    # D. Final feature matrix
    # -------------------------
    feature_matrix = pd.concat([weighted_cat.reset_index(drop=True),
                                weighted_num.reset_index(drop=True)], axis=1)

    feature_matrix.index = pd.MultiIndex.from_frame(
        df[["user_id", "main_subject_id", "term_id"]]
    )
    feature_matrix.index.names = ["user_id", "main_subject_id", "term_id"]

    print(f"✅ Feature matrix built: {feature_matrix.shape}")

    artifacts = {
        "feature_matrix": feature_matrix,
        "student_meta": df,
        "oh_enc": oh_enc,
        "scaler": scaler,
        "weights": weights,
        "time_cols": time_cols,
        "semester_cols": semester_cols,
        "nominal_cols": nominal_cols,
        "num_cols": num_cols,
    }

    return artifacts

# =========================================================
# 13) VALIDATE MAPPING
# =========================================================
def validate_recommender_profiles(user_features: pd.DataFrame, feature_matrix: pd.DataFrame):
    print("\n========== VALIDATION ==========")

    # 1. duplicate record
    dup_count = user_features.duplicated(subset=["user_id", "term_id", "main_subject_id"]).sum()
    print(f"Duplicate records: {dup_count}")

    # 2. missing main subject
    missing_main = user_features["main_subject_id"].isna().sum()
    print(f"Missing main_subject_id: {missing_main}")

    # 3. time slot coverage
    time_cols = [f"time_slot_{i}" for i in range(42)]
    user_features["time_slot_sum"] = user_features[time_cols].sum(axis=1)
    zero_time = (user_features["time_slot_sum"] == 0).sum()
    print(f"Users with no available time slot: {zero_time}")

    # 4. semester context coverage
    if "semester_subject_count" in user_features.columns:
        zero_semester_ctx = (user_features["semester_subject_count"] == 0).sum()
        print(f"Users with empty semester context: {zero_semester_ctx}")

    # 5. total_clicks all zero
    all_zero_clicks = (user_features["total_clicks"] == 0).all()
    print(f"total_clicks all zero: {all_zero_clicks}")

    # 6. feature matrix shape
    print(f"Feature matrix shape: {feature_matrix.shape}")

    # 7. index unique
    print(f"Feature matrix index unique: {feature_matrix.index.is_unique}")

    print("================================\n")

profiles = build_canonical_profiles(
    student_profiles=student_profiles,
    student_term_profiles=student_term_profiles,
    student_free_time_slots=student_free_time_slots,
    student_subject_enrollments=student_subject_enrollments,
    cohorts=cohorts,
    curriculum_term_subjects=curriculum_term_subjects,
    academic_terms=academic_terms,
    subjects=subjects
)
import numpy as np
import pandas as pd
from sklearn.metrics.pairwise import cosine_similarity


# =========================================================
# 1) CONFIG MODE - giữ nguyên triết lý cũ
# =========================================================
STRATEGY_CONFIG = {
    "Survivor": {
        "mutual_support": {
            "target_goals": ["Survivor"],
            "avoid_goals": ["Passive Learner", "Standard Learner", "High Achiever"]
        },
        "peer_support": {
            "target_goals": ["Standard Learner"],
            "avoid_goals": ["Passive Learner", "Survivor", "High Achiever"]
        },
        "challenge": {
            "target_goals": ["High Achiever"],
            "avoid_goals": ["Passive Learner", "Survivor", "Standard Learner"]
        }
    },
    "Passive Learner": {
        "mutual_support": {
            "target_goals": ["Passive Learner"],
            "avoid_goals": ["Survivor", "Standard Learner", "High Achiever"]
        },
        "peer_support": {
            "target_goals": ["Standard Learner"],
            "avoid_goals": ["Passive Learner", "Survivor", "High Achiever"]
        },
        "challenge": {
            "target_goals": ["High Achiever"],
            "avoid_goals": ["Passive Learner", "Survivor", "Standard Learner"]
        }
    },
    "Standard Learner": {
        "mutual_support": {
            "target_goals": ["Standard Learner"],
            "avoid_goals": ["Passive Learner", "Survivor", "High Achiever"]
        },
        "peer_support": {
            "target_goals": ["High Achiever"],
            "avoid_goals": ["Passive Learner", "Survivor", "Standard Learner"]
        },
        "support": {
            "target_goals": ["Survivor", "Passive Learner"],
            "avoid_goals": ["Standard Learner", "High Achiever"]
        }
    },
    "High Achiever": {
        "mutual_support": {
            "target_goals": ["High Achiever"],
            "avoid_goals": ["Passive Learner", "Survivor", "Standard Learner"]
        },
        "support": {
            "target_goals": ["Survivor", "Passive Learner", "Standard Learner"],
            "avoid_goals": ["High Achiever"]
        }
    }
}


# =========================================================
# 2) HÀM PHỤ
# =========================================================
def _safe_normalize(value, max_value):
    if max_value <= 0:
        return 0.0
    return float(np.clip(value / max_value, 0.0, 1.0))


def get_similarity_columns_db(feature_matrix: pd.DataFrame):
    """
    Chỉ giữ các cột dùng để tính similarity nền:
    - demographic one-hot: gender_*, region_*
    - time vector: time_slot_*
    - age_level (nếu có)

    Không dùng:
    - avg_score, studied_credits, total_clicks
    - semester_has_subject_*
    """
    all_cols = list(feature_matrix.columns)
    sim_cols = []

    sim_cols += [c for c in all_cols if c.startswith("gender_")]
    sim_cols += [c for c in all_cols if c.startswith("region_")]
    sim_cols += [c for c in all_cols if c.startswith("time_slot_")]

    if "age_level" in all_cols:
        sim_cols.append("age_level")

    sim_cols = list(dict.fromkeys(sim_cols))

    if not sim_cols:
        raise ValueError("Không tìm thấy cột phù hợp để tính similarity.")

    return sim_cols


def compute_shared_subject_score(user_row: pd.Series, cand_row: pd.Series):
    """
    Jaccard trên semester context.
    Dùng các cột semester_has_subject_* từ user_features / student_meta.
    """
    sem_cols = [c for c in user_row.index if c.startswith("semester_has_subject_")]

    if not sem_cols:
        return 0.0, {"n_shared": 0, "n_union": 0, "shared_subject_ids": []}

    user_subjects = set(c for c in sem_cols if float(user_row.get(c, 0)) > 0)
    cand_subjects = set(c for c in sem_cols if float(cand_row.get(c, 0)) > 0)

    intersection = user_subjects & cand_subjects
    union = user_subjects | cand_subjects

    jaccard = len(intersection) / len(union) if len(union) > 0 else 0.0
    shared_ids = [c.replace("semester_has_subject_", "") for c in intersection]

    return jaccard, {
        "n_shared": len(intersection),
        "n_union": len(union),
        "shared_subject_ids": sorted(shared_ids)
    }


def compute_mode_bonus_db(
    user_row: pd.Series,
    cand_row: pd.Series,
    mode: str,
    score_gap_cap: float = 3.0,
    credit_gap_cap: float = 40.0,
    interaction_gap_cap: float = None
):
    """
    Bonus theo mode, dùng raw signal:
    - avg_score
    - studied_credits
    - total_clicks  (sau này bạn map từ số lượng nhắn tin/tương tác)

    Giải thích:
    - avg_score = năng lực học
    - studied_credits = trải nghiệm học
    - total_clicks = mức độ active / willingness to interact
    """
    user_score = float(user_row.get("avg_score", 0))
    cand_score = float(cand_row.get("avg_score", 0))

    user_credits = float(user_row.get("studied_credits", 0))
    cand_credits = float(cand_row.get("studied_credits", 0))

    user_interactions = float(user_row.get("total_clicks", 0))
    cand_interactions = float(cand_row.get("total_clicks", 0))

    score_gap = cand_score - user_score
    credit_gap = cand_credits - user_credits
    interaction_gap = cand_interactions - user_interactions

    if interaction_gap_cap is None:
        interaction_gap_cap = max(abs(user_interactions), abs(cand_interactions), 1.0)

    # ----------------------------------------------------
    # 1) Mutual support: ưu tiên tương đồng
    # ----------------------------------------------------
    if mode == "mutual_support":
        score_fit = max(0.0, 1.0 - abs(score_gap) / score_gap_cap)
        credit_fit = max(0.0, 1.0 - abs(credit_gap) / credit_gap_cap)
        interaction_fit = max(0.0, 1.0 - abs(interaction_gap) / interaction_gap_cap)

        return 0.60 * score_fit + 0.25 * credit_fit + 0.15 * interaction_fit

    # ----------------------------------------------------
    # 2) Peer support: candidate nhỉnh hơn vừa phải
    # ----------------------------------------------------
    elif mode == "peer_support":
        if 0.3 <= score_gap <= 1.0:
            score_bonus = 1.0
        elif 0.0 < score_gap < 0.3 or 1.0 < score_gap <= 1.5:
            score_bonus = 0.7
        else:
            score_bonus = 0.2

        credit_bonus = _safe_normalize(max(0.0, credit_gap), credit_gap_cap)
        interaction_bonus = _safe_normalize(max(0.0, interaction_gap), interaction_gap_cap)

        return 0.65 * score_bonus + 0.20 * credit_bonus + 0.15 * interaction_bonus

    # ----------------------------------------------------
    # 3) Challenge: candidate mạnh hơn rõ hơn
    # ----------------------------------------------------
    elif mode == "challenge":
        if 0.8 <= score_gap <= 2.0:
            score_bonus = 1.0
        elif 0.4 <= score_gap < 0.8 or 2.0 < score_gap <= 2.5:
            score_bonus = 0.6
        else:
            score_bonus = 0.1

        credit_bonus = _safe_normalize(max(0.0, credit_gap), credit_gap_cap)
        interaction_bonus = _safe_normalize(max(0.0, interaction_gap), interaction_gap_cap)

        return 0.70 * score_bonus + 0.15 * credit_bonus + 0.15 * interaction_bonus

    # ----------------------------------------------------
    # 4) Support: candidate thấp hơn để user hỗ trợ
    # ----------------------------------------------------
    elif mode == "support":
        reverse_score_gap = user_score - cand_score
        reverse_credit_gap = user_credits - cand_credits
        reverse_interaction_gap = user_interactions - cand_interactions

        if 0.3 <= reverse_score_gap <= 1.5:
            score_bonus = 1.0
        elif 0.0 < reverse_score_gap < 0.3 or 1.5 < reverse_score_gap <= 2.0:
            score_bonus = 0.7
        else:
            score_bonus = 0.2

        credit_bonus = _safe_normalize(max(0.0, reverse_credit_gap), credit_gap_cap)
        interaction_bonus = _safe_normalize(max(0.0, reverse_interaction_gap), interaction_gap_cap)

        return 0.65 * score_bonus + 0.20 * credit_bonus + 0.15 * interaction_bonus

    return 0.0


def generate_user_explanation_db(
    user_row,
    cand_row,
    similarity_score,
    mode_bonus,
    shared_score,
    final_score,
    mode,
    shared_detail
):
    user_score = float(user_row.get("avg_score", 0))
    cand_score = float(cand_row.get("avg_score", 0))
    score_gap = cand_score - user_score

    if final_score >= 0.85:
        fit_text = "🟢 Rất phù hợp"
    elif final_score >= 0.70:
        fit_text = "🟡 Khá phù hợp"
    else:
        fit_text = "🔵 Có thể thử học cùng"

    goal_text = f"🎯 Mục tiêu: {cand_row.get('study_goal', 'Unknown')}"

    if abs(score_gap) <= 0.5:
        level_text = "📘 Trình độ khá tương đồng"
    elif score_gap > 0.5:
        level_text = "📈 Có thể giúp bạn học nâng cao hơn"
    else:
        level_text = "🤝 Bạn có thể hỗ trợ người này"

    mode_map = {
        "mutual_support": "Học cùng người tương đồng",
        "peer_support": "Tìm người nhỉnh hơn một chút",
        "challenge": "Tìm người để thử thách bản thân",
        "support": "Tìm người để bạn hỗ trợ"
    }
    mode_text = f"⚙️ Chế độ: {mode_map.get(mode, mode)}"

    n_shared = shared_detail.get("n_shared", 0)
    shared_subject_ids = shared_detail.get("shared_subject_ids", [])
    if n_shared > 0:
        shared_text = f"📚 Trùng {n_shared} môn trong context kỳ học: {', '.join(map(str, shared_subject_ids))}"
    else:
        shared_text = "📚 Không trùng môn context"

    score_text = (
        f"📊 Sim: {similarity_score:.2%} | "
        f"Bonus: {mode_bonus:.2%} | "
        f"Shared: {shared_score:.2%} | "
        f"Final: {final_score:.2%}"
    )

    return " | ".join([fit_text, goal_text, level_text, mode_text, shared_text, score_text])



# =========================================================
# 2.2) HÀM HỖ TRỢ PHẢN HỒI THÔNG MINH (FEEDBACK LOOP)
# =========================================================
def analyze_user_feedback_preferences(current_user_id, student_meta):
    """
    Phân tích hành vi lịch sử của User (Implicit + Explicit Feedback) bằng cách gọi HTTP API sang AI Service.
    """
    import os
    import requests
    
    API_GATEWAY_URL = os.getenv("API_GATEWAY_URL", "http://localhost:8080")
    
    pref = {
        "reward_regions": [],
        "penalty_regions": [],
        "reward_slots": [],
        "penalty_slots": [],
        "target_gpa_gap": None
    }
    
    positive_uids = []
    negative_uids = []
    
    # Gọi API Lấy danh sách ID người dùng tích cực và tiêu cực từ feedback/action
    pref_url = f"{API_GATEWAY_URL}/api/matching-items/preferences/{current_user_id}"
    try:
        resp = requests.get(pref_url, timeout=5)
        if resp.status_code == 200:
            body = resp.json()
            if body.get("success") and isinstance(body.get("data"), dict):
                data = body["data"]
                positive_uids = [int(uid) for uid in (data.get("positiveUserIds") or [])]
                negative_uids = [int(uid) for uid in (data.get("negativeUserIds") or [])]
    except Exception as e:
        print(f"⚠️ Cannot fetch feedback preferences from {pref_url}: {e}")

    # Loại bỏ các ID trùng lặp
    positive_uids = list(set(positive_uids))
    negative_uids = list(set(negative_uids))

    # 3. Trích xuất đặc trưng của nhóm tích cực và tiêu cực từ student_meta
    pos_meta = student_meta[student_meta["user_id"].isin(positive_uids)]
    neg_meta = student_meta[student_meta["user_id"].isin(negative_uids)]
    
    if not pos_meta.empty:
        pref["reward_regions"] = pos_meta["region"].dropna().value_counts().head(2).index.tolist()
        
        time_cols = [f"time_slot_{i}" for i in range(42)]
        slot_sums = pos_meta[time_cols].sum()
        pref["reward_slots"] = [int(col.replace("time_slot_", "")) for col in slot_sums.nlargest(5).index if slot_sums[col] > 0]
        
        user_rows = student_meta[student_meta["user_id"] == current_user_id]
        if not user_rows.empty:
            user_gpa = float(user_rows.iloc[0].get("avg_score", 0))
            gpa_gaps = pos_meta["avg_score"].apply(lambda val: abs(float(val) - user_gpa))
            pref["target_gpa_gap"] = float(gpa_gaps.mean()) if not gpa_gaps.empty else None

    if not neg_meta.empty:
        pref["penalty_regions"] = neg_meta["region"].dropna().value_counts().head(2).index.tolist()
        
        time_cols = [f"time_slot_{i}" for i in range(42)]
        slot_sums_neg = neg_meta[time_cols].sum()
        pref["penalty_slots"] = [int(col.replace("time_slot_", "")) for col in slot_sums_neg.nlargest(5).index if slot_sums_neg[col] > 0]

    return pref


def compute_feedback_modifier(cand_row, user_row, pref):
    """
    Tính toán tổng điểm hiệu chỉnh (Reward/Penalty) cho ứng viên cand_row dựa trên preferences.
    """
    modifier = 0.0
    
    # 1. Hiệu chỉnh vùng miền (Region)
    cand_region = cand_row.get("region")
    if cand_region in pref["reward_regions"]:
        modifier += 0.05
    elif cand_region in pref["penalty_regions"]:
        modifier -= 0.05
        
    # 2. Hiệu chỉnh thời gian (Time Slots)
    time_score = 0.0
    for slot in pref["reward_slots"]:
        if float(cand_row.get(f"time_slot_{slot}", 0)) > 0:
            time_score += 0.02
    for slot in pref["penalty_slots"]:
        if float(cand_row.get(f"time_slot_{slot}", 0)) > 0:
            time_score -= 0.02
            
    modifier += max(-0.06, min(0.06, time_score))
    
    # 3. Hiệu chỉnh khoảng cách GPA (avg_score)
    if pref["target_gpa_gap"] is not None:
        user_gpa = float(user_row.get("avg_score", 0))
        cand_gpa = float(cand_row.get("avg_score", 0))
        current_gpa_gap = abs(cand_gpa - user_gpa)
        gpa_deviation = abs(current_gpa_gap - pref["target_gpa_gap"])
        if gpa_deviation > 0.5:
            modifier -= 0.04
            
    return modifier


# =========================================================
# 3) HÀM GỢI Ý CHÍNH CHO SCHEMA MỚI
# =========================================================
def recommend_content_based_db(
    current_user_id,
    current_main_subject_id,
    current_term_id,
    student_meta,
    feature_matrix,
    mode="mutual_support",
    top_k=10,
    min_similarity=0.30,
    use_fallback=True,
    alpha=0.60,
    beta=0.25,
    gamma=0.15,
    sim_cols=None
):
    """
    Schema mới:
    - record recommender = (user_id, term_id, main_subject_id)

    Logic:
    - Hard filter: cùng main_subject_id + cùng term_id + khác user_id
    - similarity: demographic + time + age
    - bonus: avg_score + studied_credits + total_clicks
    - shared score: semester_has_subject_*
    """

    # Chuẩn hóa index để loc đồng bộ
    df_info = student_meta.copy().reset_index(drop=True)
    feats = feature_matrix.copy().reset_index(drop=True)

    # A. Chọn cột similarity
    if sim_cols is None:
        sim_cols = get_similarity_columns_db(feats)

    sim_feats = feats[sim_cols].copy()

    # B. Tìm user hiện tại
    current_rows = df_info[
        (df_info["user_id"] == current_user_id) &
        (df_info["main_subject_id"] == current_main_subject_id) &
        (df_info["term_id"] == current_term_id)
    ]

    if current_rows.empty:
        raise ValueError(
            f"Không tìm thấy record user_id={current_user_id}, "
            f"main_subject_id={current_main_subject_id}, term_id={current_term_id}"
        )

    current_idx = current_rows.index[0]
    user_row = df_info.loc[current_idx]
    current_goal = user_row["study_goal"]

    # C. Validate mode
    available_modes = STRATEGY_CONFIG.get(current_goal, {})
    if mode not in available_modes:
        raise ValueError(
            f"Mode '{mode}' không hợp lệ cho goal '{current_goal}'. "
            f"Available modes: {list(available_modes.keys())}"
        )

    strategy = available_modes[mode]
    target_goals = strategy.get("target_goals", [])
    avoid_goals = strategy.get("avoid_goals", [])

    # D. Hard filter
    same_subject_mask = (
        (df_info["main_subject_id"] == current_main_subject_id) &
        (df_info["term_id"] == current_term_id) &
        (df_info["user_id"] != current_user_id)
    )
    candidate_df = df_info[same_subject_mask].copy()

    if candidate_df.empty:
        return {
            "target_user": user_row.to_dict(),
            "mode": mode,
            "recommendations": pd.DataFrame(),
            "metadata": {
                "message": "Không có ứng viên nào cùng môn chính trong cùng học kỳ.",
                "total_candidates_same_subject_term": 0,
                "sim_cols_used": sim_cols
            }
        }

    # E. Similarity
    target_vector = sim_feats.loc[current_idx].values.reshape(1, -1)
    candidate_indices = candidate_df.index.tolist()
    candidate_vectors = sim_feats.loc[candidate_indices].values

    sim_scores = cosine_similarity(target_vector, candidate_vectors).flatten()

    candidate_df["similarity_score"] = sim_scores

    # F. Filter goal + similarity
    filtered = candidate_df.copy()

    if target_goals:
        filtered = filtered[filtered["study_goal"].isin(target_goals)]
    if avoid_goals:
        filtered = filtered[~filtered["study_goal"].isin(avoid_goals)]

    filtered = filtered[filtered["similarity_score"] >= min_similarity]

    # G. Fallback
    if filtered.empty and use_fallback:
        fallback = candidate_df.copy()

        if target_goals:
            fallback = fallback[fallback["study_goal"].isin(target_goals)]
        if avoid_goals:
            fallback = fallback[~fallback["study_goal"].isin(avoid_goals)]

        filtered = fallback if not fallback.empty else candidate_df.copy()

    if filtered.empty:
        return {
            "target_user": user_row.to_dict(),
            "mode": mode,
            "recommendations": pd.DataFrame(),
            "metadata": {
                "message": "Không tìm thấy ứng viên phù hợp sau khi lọc.",
                "total_candidates_same_subject_term": len(candidate_df),
                "sim_cols_used": sim_cols
            }
        }

    # H. Tính final score
    pref = analyze_user_feedback_preferences(current_user_id, df_info)

    mode_bonus_list = []
    shared_score_list = []
    shared_detail_list = []
    final_score_list = []
    explanations = []

    for idx, cand_row in filtered.iterrows():
        sim = float(cand_row["similarity_score"])

        bonus = compute_mode_bonus_db(user_row, cand_row, mode)

        shared_score, shared_detail = compute_shared_subject_score(user_row, cand_row)

        final_score = alpha * sim + beta * bonus + gamma * shared_score
        
        # Tích hợp cơ chế phản hồi thông minh (Feedback Loop)
        feedback_modifier = compute_feedback_modifier(cand_row, user_row, pref)
        final_score = max(0.0, min(1.0, final_score + feedback_modifier))

        mode_bonus_list.append(bonus)
        shared_score_list.append(shared_score)
        shared_detail_list.append(shared_detail)
        final_score_list.append(final_score)

        explanations.append(
            generate_user_explanation_db(
                user_row, cand_row,
                sim, bonus, shared_score, final_score,
                mode, shared_detail
            )
        )

    filtered["mode_bonus"] = mode_bonus_list
    filtered["shared_subject_score"] = shared_score_list
    filtered["shared_subject_ids"] = [d["shared_subject_ids"] for d in shared_detail_list]
    filtered["n_shared_subjects"] = [d["n_shared"] for d in shared_detail_list]
    filtered["final_score"] = final_score_list
    filtered["match_percentage"] = filtered["final_score"] * 100
    filtered["explanation"] = explanations

    # I. Sort + top_k
    result = filtered.sort_values(
        by=["final_score", "similarity_score", "shared_subject_score"],
        ascending=False
    ).head(top_k).reset_index(drop=True)

    # J. Output
    output_cols = [
        "user_id",
        "full_name",
        "term_id",
        "main_subject_id",
        "main_subject_name",
        "avatar_url",
        "study_goal",
        "study_mode",
        "avg_score",
        "studied_credits",
        "total_clicks",
        "gender",
        "region",
        "similarity_score",
        "mode_bonus",
        "shared_subject_score",
        "n_shared_subjects",
        "shared_subject_ids",
        "final_score",
        "match_percentage",
        # "explanation"
    ]

    output_cols = [c for c in output_cols if c in result.columns]
    result = result[output_cols]

    metadata = {
        "target_user_id": int(current_user_id),
        "target_main_subject_id": int(current_main_subject_id),
        "target_term_id": int(current_term_id),
        "target_goal": current_goal,
        "mode": mode,
        "weights": {"alpha": alpha, "beta": beta, "gamma": gamma},
        "sim_cols_used": sim_cols,
        "total_candidates_same_subject_term": int(len(candidate_df)),
        "total_after_filter": int(len(filtered)),
        "returned_recommendations": int(len(result)),
        "avg_similarity": round(float(result["similarity_score"].mean()), 4) if not result.empty and not pd.isna(result["similarity_score"].mean()) else 0.0,
        "avg_final_score": round(float(result["final_score"].mean()), 4) if not result.empty and not pd.isna(result["final_score"].mean()) else 0.0,
        "avg_shared_subjects": round(float(result["n_shared_subjects"].mean()), 2) if not result.empty and not pd.isna(result["n_shared_subjects"].mean()) else 0.0,
    }

    # Replace NaN in result DataFrame with None so it's JSON compliant
    result = result.where(pd.notnull(result), None)

    # Sanitize target_user dict to replace NaN with None
    target_user_dict = user_row.to_dict()
    target_user_dict = {k: (None if (np.isscalar(v) and pd.isna(v)) else v) for k, v in target_user_dict.items()}

    return {
        "target_user": target_user_dict,
        "mode": mode,
        "recommendations": result,
        "metadata": metadata
    }

important_cols = [
    "user_id",
    "term_id",
    "main_subject_id",
    "main_subject_name",
    "avatar_url",
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



if __name__ == "__main__":
    profiles = build_canonical_profiles(
        student_profiles=student_profiles,
        student_term_profiles=student_term_profiles,
        student_free_time_slots=student_free_time_slots,
        student_subject_enrollments=student_subject_enrollments,
        cohorts=cohorts,
        curriculum_term_subjects=curriculum_term_subjects,
        academic_terms=academic_terms,
        subjects=subjects,
    )

    profiles.to_csv("canonical_profiles_debug.csv", index=False, encoding="utf-8-sig")
    print("✅ Saved canonical_profiles_debug.csv")

    semester_multihot = build_semester_multihot(profiles)

    user_features = build_final_user_features(profiles).merge(
        semester_multihot,
        on=["user_id", "term_id", "main_subject_id"],
        how="left"
    )

    artifacts = build_feature_matrix_from_user_features(user_features)
    feature_matrix = artifacts["feature_matrix"]

    validate_recommender_profiles(user_features, feature_matrix)


