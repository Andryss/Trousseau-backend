--liquibase formatted sql

--changeset andryss:create-row_change_type-type
create type row_change_type as enum (
    'INSERT',
    'UPDATE',
    'DELETE'
);

comment on type row_change_type is 'Тип изменения строки таблицы';
