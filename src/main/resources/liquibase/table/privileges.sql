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

--changeset andryss:add-base-privileges
insert into privileges(id, privilege, created_at)
    values ('media.upload', 'MEDIA_UPLOAD', now()),
           ('items.create', 'ITEMS_CREATE', now()),
           ('items.created.view', 'ITEMS_CREATED_VIEW', now()),
           ('items.published.view', 'ITEMS_PUBLISHED_VIEW', now()),
           ('items.published.status.change', 'ITEMS_PUBLISHED_STATUS_CHANGED', now()),
           ('items.bookings.view', 'ITEMS_BOOKINGS_VIEW', now()),
           ('category.tree.view', 'CATEGORY_TREE_VIEW', now()),
           ('items.favourites', 'ITEMS_FAVOURITES', now()),
           ('subscriptions.view', 'SUBSCRIPTIONS_VIEW', now()),
           ('subscriptions.edit', 'SUBSCRIPTIONS_EDIT', now()),
           ('notifications.view', 'NOTIFICATIONS_VIEW', now());
