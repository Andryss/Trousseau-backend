--liquibase formatted sql

--changeset andryss:create-privileges-table
create table privileges (
    id text primary key,
    privilege text not null,
    created_at timestamp not null
);

comment on table privileges is 'Привилегии';

comment on column privileges.id is 'Идентификатор';
comment on column privileges.privilege is 'Название';
comment on column privileges.created_at is 'Время создания';
