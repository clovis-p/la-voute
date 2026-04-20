UPDATE users SET username = 'user_' || id WHERE username IS NULL;

ALTER TABLE users
    MODIFY COLUMN username VARCHAR(255) NOT NULL,
    ADD CONSTRAINT uk_users_username UNIQUE (username);