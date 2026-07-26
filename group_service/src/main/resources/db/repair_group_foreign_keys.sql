-- One-time repair for legacy db_group_service schemas.
-- Run this script against db_group_service before starting GROUP-SERVICE.

START TRANSACTION;

-- Hibernate maps both fields as Java Long. Keep both MySQL columns identical.
ALTER TABLE study_groups
    MODIFY COLUMN id BIGINT NOT NULL AUTO_INCREMENT;

ALTER TABLE group_members
    MODIFY COLUMN group_id BIGINT NOT NULL;

-- Preserve invalid legacy rows before removing them from the live relation.
CREATE TABLE IF NOT EXISTS study_session_participants_orphan_backup
LIKE study_session_participants;

INSERT IGNORE INTO study_session_participants_orphan_backup
SELECT participant.*
FROM study_session_participants participant
LEFT JOIN study_sessions session_row ON session_row.id = participant.session_id
WHERE session_row.id IS NULL;

DELETE participant
FROM study_session_participants participant
LEFT JOIN study_sessions session_row ON session_row.id = participant.session_id
WHERE session_row.id IS NULL;

ALTER TABLE study_sessions
    MODIFY COLUMN id BIGINT NOT NULL AUTO_INCREMENT;

ALTER TABLE study_session_participants
    MODIFY COLUMN session_id BIGINT NOT NULL;

COMMIT;
