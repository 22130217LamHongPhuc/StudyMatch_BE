USE chat_service_v4;

SET @primary_columns := (
  SELECT GROUP_CONCAT(column_name ORDER BY ordinal_position SEPARATOR ',')
  FROM information_schema.key_column_usage
  WHERE table_schema = DATABASE()
    AND table_name = 'conversation_participants'
    AND constraint_name = 'PRIMARY'
);

SET @sql := IF(
  @primary_columns IS NOT NULL AND @primary_columns <> 'participant_id',
  'ALTER TABLE conversation_participants DROP PRIMARY KEY',
  'SELECT 1'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @has_participant_id := (
  SELECT COUNT(*)
  FROM information_schema.columns
  WHERE table_schema = DATABASE()
    AND table_name = 'conversation_participants'
    AND column_name = 'participant_id'
);

SET @sql := IF(
  @has_participant_id = 0,
  'ALTER TABLE conversation_participants ADD COLUMN participant_id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY FIRST',
  'SELECT 1'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @participant_id_is_auto_increment := (
  SELECT COUNT(*)
  FROM information_schema.columns
  WHERE table_schema = DATABASE()
    AND table_name = 'conversation_participants'
    AND column_name = 'participant_id'
    AND extra LIKE '%auto_increment%'
);

SET @primary_columns := (
  SELECT GROUP_CONCAT(column_name ORDER BY ordinal_position SEPARATOR ',')
  FROM information_schema.key_column_usage
  WHERE table_schema = DATABASE()
    AND table_name = 'conversation_participants'
    AND constraint_name = 'PRIMARY'
);

SET @sql := IF(
  @primary_columns IS NULL,
  'ALTER TABLE conversation_participants ADD PRIMARY KEY (participant_id)',
  'SELECT 1'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @sql := IF(
  @participant_id_is_auto_increment = 0,
  'ALTER TABLE conversation_participants MODIFY COLUMN participant_id BIGINT NOT NULL AUTO_INCREMENT',
  'SELECT 1'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @has_unique_conversation_user := (
  SELECT COUNT(*)
  FROM information_schema.statistics
  WHERE table_schema = DATABASE()
    AND table_name = 'conversation_participants'
    AND index_name = 'uk_conversation_participants_conversation_user'
);

SET @sql := IF(
  @has_unique_conversation_user = 0,
  'ALTER TABLE conversation_participants ADD UNIQUE KEY uk_conversation_participants_conversation_user (conversation_id, user_id)',
  'SELECT 1'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
