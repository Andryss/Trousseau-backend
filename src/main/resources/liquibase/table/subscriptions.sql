--liquibase formatted sql

--changeset andryss:create-subscriptions-table
create table subscriptions (
    id text primary key,
    owner text references users(id) not null,
    name text not null,
    data jsonb,
    created_at timestamp not null
);

comment on table subscriptions is 'Подписки';

comment on column subscriptions.id is 'Идентификатор';
comment on column subscriptions.owner is 'Идентификатор пользователя автора';
comment on column subscriptions.name is 'Название';
comment on column subscriptions.data is 'Информация о подписке';
comment on column subscriptions.created_at is 'Время создания';
