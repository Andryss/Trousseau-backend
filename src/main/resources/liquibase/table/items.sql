--liquibase formatted sql

--changeset andryss:create-items-table
create table items (
    id text primary key,
    title text,
    media_ids jsonb,
    description text,
    status text not null
);

comment on table items is 'Объявления';

comment on column items.id is 'Идентификатор';
comment on column items.title is 'Заголовок';
comment on column items.media_ids is 'Идентификаторы медиа-данных';
comment on column items.description is 'Описание';
comment on column items.status is 'Текущий статус';
