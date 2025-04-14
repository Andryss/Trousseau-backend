--liquibase formatted sql

--changeset andryss:create-role_privileges-table
create table role_privileges (
    role_id text references roles(id) not null,
    privilege_id text references privileges(id) not null,
    primary key (role_id, privilege_id)
);

comment on table role_privileges is 'Связка роль-привилегия';

comment on column role_privileges.role_id is 'Идентификатор роли';
comment on column role_privileges.privilege_id is 'Идентификатор привилегии';

--changeset andryss:link-base-roles-and-privileges
insert into role_privileges(role_id, privilege_id)
    values ('seller', 'media.upload'),
           ('seller', 'items.create'),
           ('seller', 'items.created.view'),
           ('user', 'items.published.view'),
           ('user', 'items.published.status.change'),
           ('user', 'items.bookings.view'),
           ('user', 'category.tree.view'),
           ('seller', 'category.tree.view'),
           ('user', 'items.favourites'),
           ('user', 'subscriptions.view'),
           ('user', 'subscriptions.edit'),
           ('user', 'notifications.view'),
           ('seller', 'notifications.view');
