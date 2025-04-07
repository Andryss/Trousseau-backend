--liquibase formatted sql

--changeset andryss:create-events-table
create table events (
    id text primary key,
    type text not null,
    payload jsonb,
    created_at timestamp not null
);

comment on table events is 'События';

comment on column events.id is 'Идентификатор';
comment on column events.type is 'Тип';
comment on column events.payload is 'Полезные данные';
comment on column events.created_at is 'Время создания';
