CREATE EXTENSION IF NOT EXISTS "pgcrypto";

----------------------------------------------------------
-- USER CREDENTIALS
----------------------------------------------------------

CREATE TABLE user_credentials
(
    id UUID PRIMARY KEY,

    username VARCHAR(100) NOT NULL UNIQUE,

    email VARCHAR(255) NOT NULL UNIQUE,

    password_hash VARCHAR(255) NOT NULL,

    enabled BOOLEAN NOT NULL DEFAULT TRUE,

    email_verified BOOLEAN NOT NULL DEFAULT FALSE,

    account_non_locked BOOLEAN NOT NULL DEFAULT TRUE,

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

----------------------------------------------------------
-- ROLES
----------------------------------------------------------

CREATE TABLE roles
(
    id BIGSERIAL PRIMARY KEY,

    name VARCHAR(50) NOT NULL UNIQUE
);

----------------------------------------------------------
-- CREDENTIAL ROLES
----------------------------------------------------------

CREATE TABLE credential_roles
(
    credential_id UUID NOT NULL,

    role_id BIGINT NOT NULL,

    PRIMARY KEY (credential_id, role_id),

    CONSTRAINT fk_credential_roles_credentials
        FOREIGN KEY (credential_id)
        REFERENCES user_credentials(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_credential_roles_roles
        FOREIGN KEY (role_id)
        REFERENCES roles(id)
        ON DELETE CASCADE
);

----------------------------------------------------------
-- INDEXES
----------------------------------------------------------

CREATE INDEX idx_user_credentials_username
ON user_credentials(username);

CREATE INDEX idx_user_credentials_email
ON user_credentials(email);

----------------------------------------------------------
-- DEFAULT ROLES
----------------------------------------------------------

INSERT INTO roles(name)
VALUES
('ROLE_USER'),
('ROLE_ADMIN');