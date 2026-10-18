CREATE TABLE user_profile_provisioning
(
    user_id UUID PRIMARY KEY,

    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',

    attempt_count INTEGER NOT NULL DEFAULT 0,

    next_attempt_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    lease_expires_at TIMESTAMP NULL,

    last_status_code VARCHAR(64) NULL,

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_user_profile_provisioning_credentials
        FOREIGN KEY (user_id)
        REFERENCES user_credentials(id)
        ON DELETE CASCADE,

    CONSTRAINT chk_user_profile_provisioning_status
        CHECK (status IN ('PENDING', 'PROCESSING', 'PROVISIONED', 'FAILED')),

    CONSTRAINT chk_user_profile_provisioning_attempt_count
        CHECK (attempt_count >= 0)
);

CREATE INDEX idx_user_profile_provisioning_due
    ON user_profile_provisioning(status, next_attempt_at);
