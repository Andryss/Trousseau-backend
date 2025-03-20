--liquibase formatted sql

--changeset andryss:create-favourites-table
create table favourites (
    id text primary key,
    item_id text references items(id),
    created_at timestamp not null,
    constraint favourites_unique unique (item_id)
);

comment on table favourites is 'Избранное';

comment on column favourites.id is 'Идентификатор';
comment on column favourites.item_id is 'ИД объявления, добавленного';
comment on column favourites.created_at is 'Время добавления в избранное';
