alter table if exists shares
    add constraint fk_shares_permissions
    foreign key (perms_id)
    references permissions (id);