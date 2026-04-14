alter table if exists shares
    add constraint fk_shares_files unique (file_id);