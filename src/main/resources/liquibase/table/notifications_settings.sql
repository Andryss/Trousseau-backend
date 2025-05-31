--liquibase formatted sql

--changeset andryss:create-notifications_settings-table
create table notifications_settings (
    user_id text references users(id) primary key,
    token text not null,
    updated_at timestamp not null,
    created_at timestamp not null
);

comment on table notifications_settings is 'Настройки уведомлений пользователя';

comment on column notifications_settings.user_id is 'Идентификатор пользователя';
comment on column notifications_settings.token is 'Токен FMS для отправки уведомлений';
comment on column notifications_settings.updated_at is 'Время последнего обновления';
comment on column notifications_settings.created_at is 'Время создания';
