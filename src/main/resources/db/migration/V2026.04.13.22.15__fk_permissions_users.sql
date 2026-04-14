alter table if exists permissions
    add constraint fk_permissions_users
    foreign key (user_id)
    references users (id);