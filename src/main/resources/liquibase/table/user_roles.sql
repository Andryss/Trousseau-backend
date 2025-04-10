--liquibase formatted sql

--changeset andryss:create-users-table
create table user_roles (
    user_id text references users(id) not null,
    role_id text references roles(id) not null,
    primary key (user_id, role_id)
);

comment on table user_roles is 'Связка пользователь-роль';

comment on column user_roles.user_id is 'Идентификатор пользователя';
comment on column user_roles.role_id is 'Идентификатор роли';
