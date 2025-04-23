--liquibase formatted sql

--changeset andryss:create-users-table
create table users (
    id text primary key,
    username text unique not null,
    password_hash varchar not null,
    contacts jsonb not null,
    room text,
    created_at timestamp not null
);

comment on table users is 'Пользователи';

comment on column users.id is 'Идентификатор';
comment on column users.username is 'Имя пользователя';
comment on column users.password_hash is 'Хеш пароля';
comment on column users.contacts is 'Список контактов для связи';
comment on column users.room is 'Комната проживания';
comment on column users.created_at is 'Время создания';
