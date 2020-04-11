CREATE TABLE users
(
    id            INTEGER primary key NOT NULL,
    is_registered BOOLEAN             NOT NULL,
    is_banned     BOOLEAN             NOT NULL DEFAULT FALSE,
    is_admin      BOOLEAN             NOT NULL,
    username      VARCHAR             NOT NULL DEFAULT ''
);

CREATE TABLE triggers
(
    id SERIAL primary key NOT NULL,
    chat_id BIGINT NOT NULL,
    created_by INTEGER NOT NULL,
    created_at TIMESTAMP NOT NULL,
    last_updated TIMESTAMP NOT NULL,
    is_enabled BOOLEAN NOT NULL,
    text VARCHAR NOT NULL,
    cron VARCHAR(30) NOT NULL
);