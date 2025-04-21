--liquibase formatted sql

--changeset andryss:create-favourites-table
create table favourites (
    id text primary key,
    user_id text references users(id) not null,
    item_id text references items(id) not null,
    created_at timestamp not null,
    constraint favourites_unique unique (user_id, item_id)
);

comment on table favourites is 'Избранное';

comment on column favourites.id is 'Идентификатор';
comment on column favourites.user_id is 'ИД пользователя';
comment on column favourites.item_id is 'ИД объявления';
comment on column favourites.created_at is 'Время добавления в избранное';
