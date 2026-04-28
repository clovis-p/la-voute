ALTER TABLE files
    ADD COLUMN IF not exists size BIGINT NOT NULL;