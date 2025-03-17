--liquibase formatted sql

--changeset andryss:create-bookings-table
create table bookings (
    id text primary key,
    item_id text unique references items(id),
    booked_at timestamp not null
);

comment on table bookings is 'Бронирования';

comment on column bookings.id is 'Идентификатор';
comment on column bookings.item_id is 'ИД забронированного объявления';
comment on column bookings.booked_at is 'Время бронирования';
