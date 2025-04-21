--liquibase formatted sql

--changeset andryss:create-items-table
create table items (
    id text primary key,
    owner text references users(id) not null,
    title text,
    media_ids jsonb,
    description text,
    category_id text references categories(id),
    status text not null,
    created_at timestamp not null
);

comment on table items is 'Объявления';

comment on column items.id is 'Идентификатор';
comment on column items.owner is 'Идентификатор пользователя автора';
comment on column items.title is 'Заголовок';
comment on column items.media_ids is 'Идентификаторы медиа-данных';
comment on column items.description is 'Описание';
comment on column items.category_id is 'Идентификатор категории';
comment on column items.status is 'Текущий статус';
comment on column items.created_at is 'Время создания';
