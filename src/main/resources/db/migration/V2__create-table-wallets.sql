CREATE TABLE wallets (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL UNIQUE,
    balance DECIMAL(19, 2) NOT NULL DEFAULT 0.00,
    transaction_pin VARCHAR(255) NOT NULL,
    pin_attempts INTEGER NOT NULL DEFAULT 0,
    pin_blocked_until TIMESTAMP,
    transaction_limit DECIMAL(19, 2) NOT NULL,
    night_limit DECIMAL(19, 2) NOT NULL,
    daily_limit DECIMAL(19, 2) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP,
    CONSTRAINT fk_wallet_user FOREIGN KEY (user_id) REFERENCES users(id)
);