CREATE TABLE IF NOT EXISTS admin_invitations (
    id BIGINT NOT NULL AUTO_INCREMENT,
    email VARCHAR(150) NOT NULL,
    full_name VARCHAR(120) NOT NULL,
    token_hash VARCHAR(255) NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'PENDING',
    expires_at DATETIME NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uq_admin_invitations_token_hash (token_hash),
    UNIQUE KEY uq_admin_invitations_email (email)
);
