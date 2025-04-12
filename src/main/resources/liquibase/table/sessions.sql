--liquibase formatted sql

--changeset andryss:create-sessions-table
create table sessions (
    id text primary key,
    user_id text references users(id) not null,
    meta jsonb not null,
    created_at timestamp not null
);

comment on table sessions is 'Активные сессии';

comment on column sessions.id is 'Идентификатор';
comment on column sessions.user_id is 'Идентификатор пользователя';
comment on column sessions.meta is 'Метаинформация';
comment on column sessions.created_at is 'Время создания';
