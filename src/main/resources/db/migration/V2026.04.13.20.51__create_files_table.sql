create table files (
    id integer not null auto_increment,
    is_directory TINYINT(1),
    is_locked TINYINT(1) NOT NULL DEFAULT 0,
    parent_dir_id integer,
    type_id integer,
    user_id integer not null,
    name varchar(255),
    path varchar(255),
    primary key (id)
    );