from fastapi import FastAPI, HTTPException, Query
from pydantic import BaseModel, Field
from typing import Optional, Dict, Any, List
from pathlib import Path
import pandas as pd
from fastapi.middleware.cors import CORSMiddleware
import uvicorn
import requests
from concurrent.futures import ThreadPoolExecutor
import os
import math
import chat_model as cm

def sanitize_nan(val):
    if isinstance(val, float) and math.isnan(val):
        return None
    elif isinstance(val, list):
        return [sanitize_nan(x) for x in val]
    elif isinstance(val, dict):
        return {k: sanitize_nan(v) for k, v in val.items()}
    return val

app = FastAPI()
API_GATEWAY_URL = os.getenv("API_GATEWAY_URL", "http://localhost:8080")

app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)


@app.get("/health")
def health():
    return {"status": "UP"}


profiles = None
semester_multihot = None
user_features = None
artifacts = None
feature_matrix = None

BASE_DIR = Path(__file__).resolve().parent
DEBUG_CSV_PATH = BASE_DIR / "canonical_profiles_debug.csv"


def load_recommender_artifacts():
    global profiles, semester_multihot, user_features, artifacts, feature_matrix

    profiles = cm.build_canonical_profiles(
        student_profiles=cm.student_profiles,
        student_term_profiles=cm.student_term_profiles,
        student_free_time_slots=cm.student_free_time_slots,
        student_subject_enrollments=cm.student_subject_enrollments,
        cohorts=cm.cohorts,
        curriculum_term_subjects=cm.curriculum_term_subjects,
        academic_terms=cm.academic_terms,
        subjects=getattr(cm, "subjects", None),
    )

    profiles.to_csv(DEBUG_CSV_PATH, index=False, encoding="utf-8-sig")
    print(f"✅ Saved canonical_profiles_debug.csv at: {DEBUG_CSV_PATH}")

    # FIX: Nếu DB chưa có dữ liệu đủ để build recommender thì không cho app crash
    if profiles is None or profiles.empty:
        print("⚠️ Không có dữ liệu canonical profiles.")
        print("⚠️ Kiểm tra các bảng sau trong database:")
        print("- student_term_profiles")
        print("- student_free_time_slots")
        print("- student_subject_enrollments")
        print("- curriculum_term_subjects")

        semester_multihot = pd.DataFrame()
        user_features = pd.DataFrame()
        artifacts = {}
        feature_matrix = None
        return

    semester_multihot = cm.build_semester_multihot(profiles)

    user_features = cm.build_final_user_features(profiles).merge(
        semester_multihot,
        on=["user_id", "term_id", "main_subject_id"],
        how="left",
    )

    if user_features is None or user_features.empty:
        print("⚠️ Không có dữ liệu user_features sau khi build.")
        artifacts = {}
        feature_matrix = None
        return

    artifacts = cm.build_feature_matrix_from_user_features(user_features)
    feature_matrix = artifacts.get("feature_matrix")

    if feature_matrix is None:
        print("⚠️ Không build được feature_matrix.")
        return

    cm.validate_recommender_profiles(user_features, feature_matrix)


class RecommendUsersRequest(BaseModel):
    user_id: int
    top_k: int = 10
    min_similarity: float = 0.0
    use_fallback: bool = True
    alpha: float = 0.70
    beta: float = 0.2
    gamma: float = 0.1


class ReloadRecommenderRequest(BaseModel):
    user_id: int = Field(alias="userId")

    model_config = {
        "populate_by_name": True
    }


def paginate_items(items: List[Dict[str, Any]], page: int, limit: int) -> Dict[str, Any]:
    total_items = len(items)
    total_pages = (total_items + limit - 1) // limit if total_items > 0 else 0
    start_index = (page - 1) * limit
    end_index = start_index + limit

    return {
        "items": items[start_index:end_index],
        "pagination": {
            "page": page,
            "limit": limit,
            "total_items": total_items,
            "total_pages": total_pages,
            "has_next": page < total_pages,
            "has_previous": page > 1 and total_pages > 0,
        },
    }


def resolve_user_profile_context(user_id: int) -> pd.Series:
    if cm.student_term_profiles is None or cm.academic_terms is None:
        raise ValueError("Dữ liệu profile/term chưa được tải.")

    user_profiles = cm.student_term_profiles[
        cm.student_term_profiles["user_id"] == user_id
    ].copy()

    if user_profiles.empty:
        raise ValueError(f"Không tìm thấy profile nào cho user_id={user_id}.")

    user_profiles = user_profiles[
        user_profiles["term_id"].notna()
        & user_profiles["main_subject_id"].notna()
    ].copy()

    if user_profiles.empty:
        raise ValueError(
            f"Profile của user_id={user_id} không có term_id/main_subject_id hợp lệ."
        )

    active_terms = cm.academic_terms[
        cm.academic_terms["status"].astype(str).str.strip().str.lower() == "active"
    ][["term_id"]].drop_duplicates()

    active_profiles = user_profiles.merge(active_terms, on="term_id", how="inner")

    if active_profiles.empty:
        raise ValueError(
            f"Không tìm thấy profile thuộc học kỳ active cho user_id={user_id}."
        )

    return active_profiles.iloc[0]


def fetch_friend_requests_map(current_user_id: int) -> Dict[int, Dict[str, Any]]:
    """
    Calls the social service endpoint /social/friends/{userId}/relations once and builds a mapping.
    The response has a data field containing a map of:
    - key: other_user_id (str)
    - value: FriendRelationDto (id, senderId, receiverId, status)
    Returns empty dict on any error.
    """
    url = f"{API_GATEWAY_URL}/social/friends/{current_user_id}/relations"

    try:
        resp = requests.get(url, timeout=5)
        resp.raise_for_status()
        body = resp.json()

        if not isinstance(body, dict):
            return {}

        data = body.get("data") or {}
        mapping: Dict[int, Dict[str, Any]] = {}

        for other_uid_str, rel in data.items():
            try:
                if not isinstance(rel, dict):
                    continue
                other_user_id = int(other_uid_str)
                mapping[other_user_id] = {
                    "id": rel.get("id"),
                    "senderId": rel.get("senderId"),
                    "receiverId": rel.get("receiverId"),
                    "status": rel.get("status"),
                }
            except Exception:
                continue

        return mapping

    except Exception as exc:
        print(f"⚠️ Cannot call social service {url}: {exc}")
        return {}


def fetch_common_groups(user_id: int, other_user_id: int) -> List[Dict[str, Any]]:
    """
    Calls the backend API to get common groups between user_id and other_user_id.
    GET http://localhost:8080/api/groups/user/{userId}/common/{otherUserId}
    """
    url = f"{API_GATEWAY_URL}/api/groups/user/{user_id}/common/{other_user_id}"
    try:
        resp = requests.get(url, timeout=2)
        if resp.status_code == 200:
            body = resp.json()
            if isinstance(body, dict) and body.get("success"):
                return body.get("data") or []
        return []
    except Exception as exc:
        print(f"⚠️ Cannot call common groups service {url}: {exc}")
        return []


def enrich_common_groups(user_id: int, recs: List[Dict[str, Any]]) -> None:
    def _fetch_single(rec):
        other_uid = rec.get("user_id")
        if other_uid is not None:
            rec["common_groups"] = fetch_common_groups(user_id, int(other_uid))
        else:
            rec["common_groups"] = []

    with ThreadPoolExecutor(max_workers=min(len(recs) if recs else 1, 10)) as executor:
        list(executor.map(_fetch_single, recs))


@app.get("/api/recommend-users")
def recommend_users(
    user_id: int,
    page: int = Query(1, ge=1),
    limit: int = Query(10, ge=1, le=100),
):
    try:
        payload = RecommendUsersRequest(user_id=user_id)
        if feature_matrix is None or user_features is None or user_features.empty:
            load_recommender_artifacts()

        if feature_matrix is None or user_features is None or user_features.empty:
            return sanitize_nan({
                "success": False,
                "message": (
                    "Không có dữ liệu để gợi ý. "
                    "Hãy kiểm tra các bảng: "
                    "student_term_profiles, student_free_time_slots, "
                    "student_subject_enrollments, curriculum_term_subjects."
                ),
                "recommendations": [],
                "pagination": {
                    "page": page,
                    "limit": limit,
                    "total_items": 0,
                    "total_pages": 0,
                    "has_next": False,
                    "has_previous": False,
                },
            })

        selected_profile = resolve_user_profile_context(payload.user_id)

        current_main_subject_id = int(selected_profile["main_subject_id"])
        current_term_id = int(selected_profile["term_id"])
        current_mode = selected_profile["study_mode"]

        total_candidate_rows = (
            len(user_features.index)
            if isinstance(user_features, pd.DataFrame) and not user_features.empty
            else payload.top_k
        )

        result = cm.recommend_content_based_db(
            current_user_id=payload.user_id,
            current_main_subject_id=current_main_subject_id,
            current_term_id=current_term_id,
            student_meta=user_features,
            feature_matrix=feature_matrix,
            mode=current_mode,
            top_k=max(payload.top_k, total_candidate_rows),
            min_similarity=payload.min_similarity,
            use_fallback=payload.use_fallback,
            alpha=payload.alpha,
            beta=payload.beta,
            gamma=payload.gamma,
        )

        recommendations = result["recommendations"]

        if isinstance(recommendations, pd.DataFrame):
            recommendations = recommendations.where(pd.notnull(recommendations), None)
            recommendations = recommendations.to_dict(orient="records")

        try:
            fr_map = fetch_friend_requests_map(payload.user_id)
        except Exception:
            fr_map = {}

        blocked_statuses = {"APPROVED", "REJECTED", "SKIP", "UNFRIEND"}
        enriched_recs: List[Dict[str, Any]] = []

        if isinstance(recommendations, list):
            for rec in recommendations:
                try:
                    other_uid = (
                        int(rec.get("user_id"))
                        if rec.get("user_id") is not None
                        else None
                    )
                except Exception:
                    other_uid = None

                friend_req = fr_map.get(other_uid) if other_uid is not None else None

                status = (
                    str(friend_req.get("status", "")).strip().upper()
                    if isinstance(friend_req, dict)
                    else ""
                )

                # Không gợi ý lại nếu hai user đã có request ở trạng thái kết thúc
                if status in blocked_statuses:
                    continue

                rec["friend_request"] = friend_req if isinstance(friend_req, dict) else None

                enriched_recs.append(rec)
        else:
            enriched_recs = []

        paginated_result = paginate_items(enriched_recs, page=page, limit=limit)

        # Enrich common groups in parallel for only the paginated items
        enrich_common_groups(payload.user_id, paginated_result["items"])

        return sanitize_nan({
            "success": True,
            "message": "Đã tạo danh sách gợi ý thành công.",
            "recommendations": paginated_result["items"],
            "pagination": paginated_result["pagination"],
        })

    except Exception as exc:
        return sanitize_nan({
            "success": False,
            "message": str(exc),
            "recommendations": [],
            "pagination": {
                "page": page,
                "limit": limit,
                "total_items": 0,
                "total_pages": 0,
                "has_next": False,
                "has_previous": False,
            },
        })


@app.post("/api/reload-recommender")
def reload_recommender(
    userId: Optional[int] = Query(None, alias="userId"),
    user_id: Optional[int] = Query(None, alias="user_id"),
    payload: Optional[ReloadRecommenderRequest] = None,
):
    try:
        target_user_id = None

        if payload is not None and payload.user_id is not None:
            target_user_id = payload.user_id

        if target_user_id is None:
            target_user_id = userId if userId is not None else user_id

        if target_user_id is None:
            raise HTTPException(
                status_code=422,
                detail="user_id or userId is required as a query parameter or in the request body",
            )

        refreshed = cm.reload_user_from_db(target_user_id)

        print("========== AFTER RELOAD CHECK ==========")
        print(cm.student_profiles[cm.student_profiles["user_id"] == int(target_user_id)])
        print(cm.student_term_profiles[cm.student_term_profiles["user_id"] == int(target_user_id)])
        print("========================================")

        load_recommender_artifacts()

        current_csv_row = pd.DataFrame()

        if profiles is not None and not profiles.empty:
            current_csv_row = profiles[profiles["user_id"] == int(target_user_id)][
                [
                    "user_id",
                    "term_id",
                    "main_subject_id",
                    "avg_score",
                    "studied_credits",
                    "study_goal",
                    "study_mode",
                    "region",
                ]
            ]

        print("========== CSV SOURCE ROW CHECK ==========")
        print(current_csv_row)
        print("==========================================")

        return sanitize_nan({
            "success": True,
            "message": f"Reload recommender artifacts thành công cho user_id={target_user_id}.",
            "refreshed_rows": refreshed,
            "debug_csv_path": str(DEBUG_CSV_PATH),
        })

    except HTTPException as he:
        raise he
    except Exception as exc:
        raise HTTPException(status_code=400, detail=str(exc))


@app.on_event("startup")
def startup_load_data():
    load_recommender_artifacts()


if __name__ == "__main__":
    import os
    load_recommender_artifacts()
    port = int(os.getenv("PORT", 8000))
    uvicorn.run(app, host="0.0.0.0", port=port)
