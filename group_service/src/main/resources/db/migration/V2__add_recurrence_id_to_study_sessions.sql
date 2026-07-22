ALTER TABLE study_sessions
    ADD COLUMN IF NOT EXISTS recurrence_id VARCHAR(255) NULL;
