CREATE TABLE transactions (
    id UUID PRIMARY KEY,

    sender_wallet_id UUID,
    receiver_wallet_id UUID,

    amount DECIMAL(19, 2) NOT NULL,
    type VARCHAR(20) NOT NULL,
    created_at TIMESTAMP NOT NULL,

    CONSTRAINT fk_transaction_sender
        FOREIGN KEY (sender_wallet_id) REFERENCES wallets(id),

    CONSTRAINT fk_transaction_receiver
        FOREIGN KEY (receiver_wallet_id) REFERENCES wallets(id)
);

CREATE INDEX idx_transactions_sender ON transactions(sender_wallet_id);
CREATE INDEX idx_transactions_receiver ON transactions(receiver_wallet_id);