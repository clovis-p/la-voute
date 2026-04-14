create table if not exists permissions (
    id integer not null auto_increment,
    user_id integer,
    description varchar(255),
    primary key (id)
    );