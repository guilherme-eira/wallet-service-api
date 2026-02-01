CREATE TABLE users (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    tax_id VARCHAR(14) NOT NULL,
    email VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    type VARCHAR(20) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    verified BOOLEAN NOT NULL DEFAULT FALSE,
    login_attempts INTEGER NOT NULL DEFAULT 0,
    login_blocked_until TIMESTAMP,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP,
    deleted_at TIMESTAMP,
    two_factor_active BOOLEAN NOT NULL DEFAULT FALSE,
    two_factor_secret VARCHAR(255)
);

CREATE UNIQUE INDEX idx_users_email_unique_active ON users (email) WHERE deleted_at IS NULL;
CREATE UNIQUE INDEX idx_users_tax_id_unique_active ON users (tax_id) WHERE deleted_at IS NULL;