CREATE TABLE IF NOT EXISTS posts (
  post_id BIGINT NOT NULL AUTO_INCREMENT,
  author_id BIGINT NOT NULL,
  content TEXT NULL,
  visibility VARCHAR(20) NOT NULL DEFAULT 'PUBLIC',
  is_deleted TINYINT(1) NOT NULL DEFAULT 0,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (post_id),
  INDEX idx_posts_author_created (author_id, created_at),
  INDEX idx_posts_deleted (is_deleted)
);

CREATE TABLE IF NOT EXISTS post_media (
  media_id BIGINT NOT NULL AUTO_INCREMENT,
  post_id BIGINT NOT NULL,
  media_url VARCHAR(1000) NOT NULL,
  media_type VARCHAR(30) NOT NULL DEFAULT 'IMAGE',
  display_order INT NOT NULL DEFAULT 0,
  PRIMARY KEY (media_id),
  INDEX idx_post_media_post (post_id),
  CONSTRAINT fk_post_media_post
    FOREIGN KEY (post_id) REFERENCES posts(post_id)
    ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS post_reactions (
  reaction_id BIGINT NOT NULL AUTO_INCREMENT,
  post_id BIGINT NOT NULL,
  user_id BIGINT NOT NULL,
  reaction_type VARCHAR(20) NOT NULL DEFAULT 'LIKE',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (reaction_id),
  UNIQUE KEY uk_post_reaction_user (post_id, user_id),
  INDEX idx_post_reactions_user (user_id),
  CONSTRAINT fk_post_reactions_post
    FOREIGN KEY (post_id) REFERENCES posts(post_id)
    ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS post_comments (
  comment_id BIGINT NOT NULL AUTO_INCREMENT,
  post_id BIGINT NOT NULL,
  author_id BIGINT NOT NULL,
  content TEXT NOT NULL,
  is_deleted TINYINT(1) NOT NULL DEFAULT 0,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (comment_id),
  INDEX idx_post_comments_post_created (post_id, created_at),
  INDEX idx_post_comments_author (author_id),
  CONSTRAINT fk_post_comments_post
    FOREIGN KEY (post_id) REFERENCES posts(post_id)
    ON DELETE CASCADE
);
