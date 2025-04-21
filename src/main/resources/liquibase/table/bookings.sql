--liquibase formatted sql

--changeset andryss:create-bookings-table
create table bookings (
    id text primary key,
    user_id text references users(id) not null,
    item_id text unique references items(id) not null,
    booked_at timestamp not null
);

comment on table bookings is 'Бронирования';

comment on column bookings.id is 'Идентификатор';
comment on column bookings.user_id is 'ИД автора бронирования';
comment on column bookings.item_id is 'ИД забронированного объявления';
comment on column bookings.booked_at is 'Время бронирования';
