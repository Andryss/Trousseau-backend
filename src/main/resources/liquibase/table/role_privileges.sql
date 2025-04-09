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
