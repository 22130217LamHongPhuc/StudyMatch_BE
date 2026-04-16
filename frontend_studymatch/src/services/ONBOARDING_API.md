# Onboarding API Integration

## Overview

The client-side onboarding form now automatically submits the collected user data to your backend API after the user completes Step 7 and clicks "Hoàn tất & Tìm bạn học".

## API Endpoint

**POST** `http://localhost:8081/api/onboarding/submit`

## Request Body Structure

```json
{
  "studentCode": "21130524",
  "fullName": "Nguyen Van A",
  "gender": "male",
  "ageGroup": "18_22",
  "region": "TP.HCM",
  "cohortId": 2,
  "termId": 5,
  "studyYearNo": 3,
  "semesterNo": 2,
  "avgScore": 7.8,
  "studiedCredits": 78,
  "studyGoal": "IMPROVE",
  "studyMode": "GROUP",
  "mainSubjectId": 11,
  "currentSubjectIds": [21, 22, 23],
  "freeTimeSlots": [
    { "dayOfWeek": 0, "slotCode": "ca4" },
    { "dayOfWeek": 2, "slotCode": "ca5" }
  ],
  "subjectScheduleSlots": [
    {
      "subjectId": 11,
      "dayOfWeek": 0,
      "slotCode": "ca1",
      "scheduleType": "MAIN_SUBJECT"
    },
    {
      "subjectId": 11,
      "dayOfWeek": 3,
      "slotCode": "ca3",
      "scheduleType": "MAIN_SUBJECT"
    },
    {
      "subjectId": 21,
      "dayOfWeek": 1,
      "slotCode": "ca2",
      "scheduleType": "CURRENT_TERM"
    },
    {
      "subjectId": 22,
      "dayOfWeek": 2,
      "slotCode": "ca1",
      "scheduleType": "CURRENT_TERM"
    },
    {
      "subjectId": 22,
      "dayOfWeek": 4,
      "slotCode": "ca1",
      "scheduleType": "CURRENT_TERM"
    },
    {
      "subjectId": 23,
      "dayOfWeek": 5,
      "slotCode": "ca4",
      "scheduleType": "CURRENT_TERM"
    }
  ]
}
```

## Field Descriptions

### User Information

- **studentCode** (string): Student ID from Step 1
- **fullName** (string): Full name from Step 1
- **gender** (string): "male" | "female" | "" from Step 2
- **ageGroup** (string): Age group from Step 2 (e.g., "18_22", "22_30", etc.)
- **region** (string): Region from Step 2

### Academic Information

- **cohortId** (number|string): ID of the selected cohort (mapped from cohortCode)
- **termId** (number|string, optional): Academic term ID (derived from studyPlan.termFullName)
- **studyYearNo** (number, optional): Study year number (from studyPlan.studyYearNo)
- **semesterNo** (number, optional): Semester number (from studyPlan.semesterNo)

### Academic Performance

- **avgScore** (number): Average score from Step 6 (0-10 scale)
- **studiedCredits** (number): Number of credits studied from Step 6
- **prevAttempts** (number): Previous attempts (Note: always 0 in current form)

### Study Preferences

- **studyGoal** (string): Study goal mapped from user selection
  - "Survivor" → "SURVIVE"
  - "Passive Learner" → "PASSIVE"
  - "Standard Learner" → "STANDARD"
  - "High Achiever" → "IMPROVE"

- **studyMode** (string): Study mode mapped from user selection
  - "mutual_support" → "GROUP"
  - "peer_support" → "PEER"
  - "challenge" → "CHALLENGE"
  - "support" → "SUPPORT"

### Subjects

- **mainSubjectId** (number|string): ID of the main subject from Step 4
- **currentSubjectIds** (array): Array of IDs for enrolled subjects from Step 4

### Schedules

- **freeTimeSlots** (array): User's available time slots from Step 5
  - **dayOfWeek**: Day index (0=Monday, 1=Tuesday, ..., 6=Sunday)
  - **slotCode**: Time slot code ("ca1" to "ca6")

- **subjectScheduleSlots** (array): When user plans to study each subject
  - **subjectId**: Subject ID
  - **dayOfWeek**: Day index (0-6)
  - **slotCode**: Time slot code ("ca1" to "ca6")
  - **scheduleType**: "MAIN_SUBJECT" for main subject, "CURRENT_TERM" for enrolled subjects

## Time Slots Reference

```
ca1: 07:00 - 08:30
ca2: 08:30 - 10:00
ca3: 10:15 - 11:45
ca4: 13:00 - 14:30
ca5: 14:30 - 16:00
ca6: 16:15 - 17:45
```

## Days of Week

```
0 = Monday (Thứ 2)
1 = Tuesday (Thứ 3)
2 = Wednesday (Thứ 4)
3 = Thursday (Thứ 5)
4 = Friday (Thứ 6)
5 = Saturday (Thứ 7)
6 = Sunday (Chủ nhật)
```

## Expected Response

### Success Response (200 OK)

```json
{
  "success": true,
  "data": {
    "studentId": 123,
    "message": "Onboarding completed successfully"
  }
}
```

### Error Response

```json
{
  "error": "Error message indicating what went wrong",
  "message": "User-friendly error message",
  "code": "ERROR_CODE"
}
```

## Frontend Implementation Details

### File: `src/services/OnboardingService.ts`

Contains three main exports:

1. **`createSubjectCodeToIdMap(studyPlan)`**
   - Converts subject codes to subject IDs from the StudyPlan
   - Used for mapping the moduleSlots data

2. **`transformFormDataToPayload(formData, cohorts, studyPlan, subjectCodeToIdMap)`**
   - Transforms the frontend FormData into the API payload format
   - Handles all mappings and conversions
   - Returns `OnboardingSubmissionPayload`

3. **`submitOnboardingForm(payload)`**
   - Async function that makes the POST request
   - Returns `{ success: boolean; data?: any; error?: string }`

### UI Updates

When submitting:

1. Next button shows loading spinner and "Đang gửi..." text
2. User sees a loading screen with spinner
3. On success: celebratory screen with "Hồ sơ hoàn tất!"
4. On error: error message with retry and reset options

## Data Transformation Examples

### Gender Mapping

- Frontend: `"M"` or `"F"` or `""`
- Backend: `"male"`, `"female"`, or `""`

### Study Goal Mapping

- Frontend: User selects from predefined goals
- Backend: Mapped to enum values ("SURVIVE", "PASSIVE", "STANDARD", "IMPROVE")

### Study Mode Mapping

- Frontend: User selects a mode based on their goal
- Backend: Mapped to system values ("GROUP", "PEER", "CHALLENGE", "SUPPORT")

### Module Slots Transformation

- Frontend stores: `Record<moduleCode, Record<dayId, Record<slotId, boolean>>>`
- Backend expects: Array of `{ subjectId, dayOfWeek, slotCode, scheduleType }`
- Transformation filters out all false values and creates array entries only for selected slots

## Backend Implementation Checklist

- [ ] Create `/api/onboarding/submit` POST endpoint
- [ ] Handle the payload structure
- [ ] Validate all required fields
- [ ] Create or update student records
- [ ] Store schedule preferences
- [ ] Store subject selections
- [ ] Return success response
- [ ] Handle errors gracefully
- [ ] Implement appropriate HTTP status codes

## Testing

To test the submission:

1. Fill out all 7 steps of the onboarding form
2. Click "Hoàn tất & Tìm bạn học" on Step 7
3. Watch the loading screen
4. Check network tab in browser DevTools to see the request payload
5. Implement corresponding backend endpoint and test end-to-end
