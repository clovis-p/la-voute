alter table if exists files
    add constraint fk_files_users
    foreign key (user_id)
    references users (id);