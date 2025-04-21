--liquibase formatted sql

--changeset andryss:create-items_history-table
create table items_history (
    id text,
    owner text not null,
    title text,
    media_ids jsonb,
    description text,
    category_id text,
    status text not null,
    created_at timestamp not null,
    change_type row_change_type not null,
    changed_at timestamp not null,
    primary key (id, changed_at)
);

comment on table items_history is 'История изменений объявлений';

comment on column items_history.changed_at is 'Время изменения';

--changeset andryss:create-items_history-triggers splitStatements:false
create or replace function insert_items_history(
    item_id text,
    change_type row_change_type
) returns void as $$
begin
    insert into items_history(id, owner, title, media_ids, description, category_id, status, created_at, change_type, changed_at)
    select id, owner, title, media_ids, description, category_id, status, created_at, change_type, now()
    from items
    where id = item_id;
end;
$$ language plpgsql;

create or replace function trigger_insert_items_history()
returns trigger as $$
declare
    change_type row_change_type;
    row record;
begin
    select tg_op::row_change_type into change_type;
    if (change_type = 'DELETE') then
        row = OLD;
    else
        row = NEW;
    end if;
    perform insert_items_history(row.id, change_type);
    return row;
end;
$$ language plpgsql;

create trigger items_insert_update
    after insert or update
    on items
    for each row
    execute procedure trigger_insert_items_history();

create trigger items_delete
    before delete
    on items
    for each row
    execute procedure trigger_insert_items_history();