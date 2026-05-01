ALTER TABLE files
    ADD COLUMN IF not exists created_on DATE;
