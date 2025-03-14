create table if not exists tasks (
    id serial primary key,
    title varchar(255) not null,
    description text not null,
    created_at timestamp not null default current_timestamp,
    user_id bigint not null,
    constraint fk_user foreign key (user_id) references users(id) on delete cascade
);