alter table if exists files
    add constraint fk_files_file_types
    foreign key (type_id)
    references file_types (id);