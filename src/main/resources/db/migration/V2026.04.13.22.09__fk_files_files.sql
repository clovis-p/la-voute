alter table if exists files
    add constraint fk_files_files
    foreign key (parent_dir_id)
    references files (id);