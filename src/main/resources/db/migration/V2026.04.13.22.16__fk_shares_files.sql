alter table if exists shares
    add constraint fk_shares_files
    foreign key (file_id)
    references files (id);