--liquibase formatted sql

--changeset andryss:create-roles-table
create table roles (
    id text primary key,
    role text not null,
    created_at timestamp not null
);

comment on table roles is 'Роли';

comment on column roles.id is 'Идентификатор';
comment on column roles.role is 'Название';
comment on column roles.created_at is 'Время создания';

--changeset andryss:add-base-roles
insert into roles(id, role, created_at)
    values ('user', 'USER', now()),
           ('seller', 'SELLER', now());
