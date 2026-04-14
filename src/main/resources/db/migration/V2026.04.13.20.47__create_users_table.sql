create table if not exists users (
    id integer not null auto_increment,
    is_admin bit,
    first_name varchar(255),
    last_name varchar(255),
    profile_pic varchar(255),
    primary key (id)
    );