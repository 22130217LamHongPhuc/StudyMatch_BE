# Tài liệu API - AdminMatchingController

`AdminMatchingController` cung cấp các API dành cho Quản trị viên (Admin) để theo dõi số liệu thống kê, danh sách hoạt động ghép đôi (matching actions) và các phản hồi học tập (study feedbacks).

- **Base URL:** `/api/admin/matching`

---

## Danh sách API

### 1. Lấy số liệu thống kê ghép đôi (Matching Statistics)

*   **Endpoint:** `GET /api/admin/matching/statistics`
*   **Mô tả:** Lấy tổng hợp thông tin thống kê về các lượt đề xuất ghép đôi, phản hồi và điểm đánh giá trung bình.
*   **Request Parameters (Query Parameters):**
    *   `fromDate` (tùy chọn, định dạng `yyyy-MM-dd`): Ngày bắt đầu lọc thống kê.
    *   `toDate` (tùy chọn, định dạng `yyyy-MM-dd`): Ngày kết thúc lọc thống kê.
*   **Response:** `MatchingStatisticsResponse` (JSON Object)
    ```json
    {
      "totalRecommendationItems": 150,
      "totalViewed": 120,
      "totalFriendRequestSent": 45,
      "totalRejected": 15,
      "totalAccepted": 20,
      "viewRate": 0.8,
      "friendRequestRate": 0.3,
      "acceptRate": 0.13,
      "rejectRate": 0.1,
      "averageFinalScore": 0.76,
      "totalFeedbacks": 30,
      "averageRating": 4.5
    }
    ```
    *   `totalRecommendationItems`: Tổng số mục đề xuất ghép đôi đã tạo.
    *   `totalViewed`: Tổng số mục đã xem (`VIEWED`).
    *   `totalFriendRequestSent`: Tổng số lời mời kết bạn đã gửi (`FRIEND_REQUEST_SENT`).
    *   `totalRejected`: Tổng số lượt từ chối đề xuất (`REJECTED`).
    *   `totalAccepted`: Tổng số lượt chấp nhận kết nối (`ACCEPTED`).
    *   `viewRate`: Tỷ lệ đã xem (`totalViewed` / `totalRecommendationItems`).
    *   `friendRequestRate`: Tỷ lệ gửi yêu cầu kết bạn (`totalFriendRequestSent` / `totalRecommendationItems`).
    *   `acceptRate`: Tỷ lệ chấp nhận kết nối (`totalAccepted` / `totalRecommendationItems`).
    *   `rejectRate`: Tỷ lệ từ chối ghép đôi (`totalRejected` / `totalRecommendationItems`).
    *   `averageFinalScore`: Điểm số đề xuất trung bình của mô hình AI đối với các cặp đề xuất.
    *   `totalFeedbacks`: Tổng số lượt phản hồi đã nhận.
    *   `averageRating`: Điểm số đánh giá trung bình từ người dùng.

---

### 2. Danh sách các hoạt động ghép đôi (Matching Actions)

*   **Endpoint:** `GET /api/admin/matching/actions`
*   **Mô tả:** Lấy danh sách phân trang các hoạt động ghép đôi dựa trên các điều kiện tìm kiếm và khoảng thời gian.
*   **Request Parameters (Query Parameters):**
    *   `page` (tùy chọn, mặc định `0`): Chỉ số trang.
    *   `size` (tùy chọn, mặc định `10`): Số lượng phần tử mỗi trang.
    *   `userId` (tùy chọn): ID của người dùng.
    *   `recommendedUserId` (tùy chọn): ID của người dùng được đề xuất ghép đôi.
    *   `actionStatus` (tùy chọn, enum `MatchingActionStatus`): Trạng thái hoạt động ghép đôi.
    *   `fromDate` (tùy chọn, định dạng `yyyy-MM-dd`): Ngày bắt đầu lọc.
    *   `toDate` (tùy chọn, định dạng `yyyy-MM-dd`): Ngày kết thúc lọc.
*   **Response:** `PageResponse<MatchingActionResponse>` (JSON Object)
    ```json
    {
      "content": [
        {
          "id": 1,
          "userId": 101,
          "recommendedUserId": 102,
          "finalScore": 0.85,
          "reasonText": "Matching based on shared Java & Spring Boot interest",
          "actionStatus": "FRIEND_REQUEST_SENT",
          "createdAt": "2026-06-14T10:00:00",
          "updatedAt": "2026-06-14T10:05:00"
        }
      ],
      "page": 0,
      "size": 10,
      "totalElements": 1,
      "totalPages": 1
    }
    ```

---

### 3. Danh sách phản hồi học tập (Study Feedbacks)

*   **Endpoint:** `GET /api/admin/matching/feedbacks`
*   **Mô tả:** Lấy danh sách phân trang các phản hồi (feedback) của phiên học tập kèm theo các bộ lọc chi tiết.
*   **Request Parameters (Query Parameters):**
    *   `page` (tùy chọn, mặc định `0`): Chỉ số trang.
    *   `size` (tùy chọn, mặc định `10`): Số lượng phần tử mỗi trang.
    *   `sessionType` (tùy chọn, enum `StudySessionType`): Loại phiên học (`USER_PAIR` hoặc `GROUP`).
    *   `reviewerUserId` (tùy chọn): ID người viết phản hồi.
    *   `targetUserId` (tùy chọn): ID người được nhận xét (được đánh giá).
    *   `groupId` (tùy chọn): ID của nhóm học tập (nếu là phiên học nhóm).
    *   `minRating` (tùy chọn): Điểm đánh giá tối thiểu (ví dụ: từ 1 đến 5).
    *   `fromDate` (tùy chọn, định dạng `yyyy-MM-dd`): Ngày bắt đầu lọc.
    *   `toDate` (tùy chọn, định dạng `yyyy-MM-dd`): Ngày kết thúc lọc.
*   **Response:** `PageResponse<StudyFeedbackResponse>` (JSON Object)
    ```json
    {
      "content": [
        {
          "id": 10,
          "sessionId": 5,
          "reviewerUserId": 101,
          "targetUserId": 102,
          "groupId": null,
          "sessionType": "USER_PAIR",
          "feedbackType": "SESSION_FEEDBACK",
          "rating": 5,
          "matchedQualityScore": 5,
          "communicationScore": 4,
          "studyEffectivenessScore": 5,
          "eligibleForModel": true,
          "comment": "Rất hợp nhau, trao đổi bài học hiệu quả.",
          "createdAt": "2026-06-14T11:00:00"
        }
      ],
      "page": 0,
      "size": 10,
      "totalElements": 1,
      "totalPages": 1
    }
    ```

---

### 4. Chi tiết một phản hồi học tập (Study Feedback Detail)

*   **Endpoint:** `GET /api/admin/matching/feedbacks/{feedbackId}`
*   **Mô tả:** Lấy thông tin chi tiết của một phản hồi theo ID của nó.
*   **Request Parameters:**
    *   `feedbackId` (Path Variable, bắt buộc): ID của phản hồi cần lấy chi tiết.
*   **Response:** `StudyFeedbackResponse` (JSON Object)
    ```json
    {
      "id": 10,
      "sessionId": 5,
      "reviewerUserId": 101,
      "targetUserId": 102,
      "groupId": null,
      "sessionType": "USER_PAIR",
      "feedbackType": "SESSION_FEEDBACK",
      "rating": 5,
      "matchedQualityScore": 5,
      "communicationScore": 4,
      "studyEffectivenessScore": 5,
      "eligibleForModel": true,
      "comment": "Rất hợp nhau, trao đổi bài học hiệu quả.",
      "createdAt": "2026-06-14T11:00:00"
    }
    ```

---

### 5. Thống kê phản hồi học tập (Study Feedback Statistics)

*   **Endpoint:** `GET /api/admin/matching/feedbacks/statistics`
*   **Mô tả:** Lấy thông tin thống kê chung về các phản hồi học tập (tổng số phản hồi, điểm trung bình, phân bổ điểm rating, tỷ lệ học nhóm / học đôi, ...).
*   **Request Parameters (Query Parameters):**
    *   `fromDate` (tùy chọn, định dạng `yyyy-MM-dd`): Ngày bắt đầu lọc.
    *   `toDate` (tùy chọn, định dạng `yyyy-MM-dd`): Ngày kết thúc lọc.
*   **Response:** `StudyFeedbackStatisticsResponse` (JSON Object)
    ```json
    {
      "totalFeedbacks": 100,
      "averageRating": 4.2,
      "averageCompatibilityRating": 4.5,
      "oneToOneFeedbacks": 70,
      "groupFeedbacks": 30,
      "ratingDistribution": {
        "5": 50,
        "4": 30,
        "3": 15,
        "2": 3,
        "1": 2
      }
    }
    ```

---

### 6. Xem phân bố trạng thái matching (Action Distribution)

*   **Endpoint:** `GET /api/admin/matching/action-distribution`
*   **Mô tả:** Lấy thông tin phân bố trạng thái ghép đôi (matching action status) phục vụ vẽ biểu đồ (chart).
*   **Request Parameters (Query Parameters):**
    *   `fromDate` (tùy chọn, định dạng `yyyy-MM-dd`): Ngày bắt đầu lọc.
    *   `toDate` (tùy chọn, định dạng `yyyy-MM-dd`): Ngày kết thúc lọc.
*   **Response:** (JSON Object)
    ```json
    {
      "VIEWED": 120,
      "FRIEND_REQUEST_SENT": 45,
      "ACCEPTED": 20,
      "REJECTED": 15
    }
    ```

---

### 7. Thống kê xu hướng theo thời gian (Trend Statistics)

*   **Endpoint:** `GET /api/admin/matching/trend`
*   **Mô tả:** Lấy thông tin thống kê số lượng đề xuất và trạng thái ghép đôi theo từng ngày trong khoảng thời gian lọc.
*   **Request Parameters (Query Parameters):**
    *   `fromDate` (tùy chọn, định dạng `yyyy-MM-dd` - mặc định là 7 ngày trước): Ngày bắt đầu lọc xu hướng.
    *   `toDate` (tùy chọn, định dạng `yyyy-MM-dd` - mặc định là hôm nay): Ngày kết thúc lọc xu hướng.
*   **Response:** `List<MatchingTrendResponse>` (JSON Array)
    ```json
    [
      {
        "date": "2026-06-01",
        "totalRecommendations": 20,
        "totalViewed": 15,
        "totalFriendRequestSent": 6,
        "totalAccepted": 3,
        "totalRejected": 2
      }
    ]
    ```

---

## Chi tiết các Enum sử dụng

### 1. `MatchingActionStatus`
Trạng thái hành động ghép đôi giữa 2 người dùng:
*   `VIEWED`: Đã xem đề xuất.
*   `FRIEND_REQUEST_SENT`: Đã gửi lời mời kết bạn/liên hệ.
*   `REJECTED`: Đã từ chối đề xuất.
*   `ACCEPTED`: Đã đồng ý kết nối.

### 2. `StudySessionType`
Loại phiên học tập:
*   `USER_PAIR`: Phiên học tập đôi (1-1).
*   `GROUP`: Phiên học tập theo nhóm.

### 3. `StudyFeedbackType`
Loại phản hồi:
*   `SESSION_FEEDBACK`: Phản hồi về phiên học.
*   `PARTIAL_FEEDBACK`: Phản hồi một phần.
*   `EARLY_LEAVE_REASON`: Lý do rời đi sớm.
*   `REPORT_PROBLEM`: Báo cáo sự cố/vấn đề.
