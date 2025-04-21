--liquibase formatted sql

--changeset andryss:create-notifications-table
create table notifications (
    id text primary key,
    receiver text references users(id) not null,
    title text not null,
    content text not null,
    links jsonb not null,
    is_read bool not null default false,
    created_at timestamp not null
);

comment on table notifications is 'Уведомления';

comment on column notifications.id is 'Идентификатор';
comment on column notifications.receiver is 'Идентификатор пользователя получателя';
comment on column notifications.title is 'Заголовок';
comment on column notifications.content is 'Тело';
comment on column notifications.links is 'Ссылки на связанные объекты';
comment on column notifications.is_read is 'Флаг прочитано ли уведомление';
comment on column notifications.created_at is 'Время создания';
