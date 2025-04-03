create table if not exists users (
    id serial primary key,
    user_email varchar(255) not null unique,
    password varchar(255) not null
);