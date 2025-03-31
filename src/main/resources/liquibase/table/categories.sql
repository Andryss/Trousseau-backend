--liquibase formatted sql

--changeset andryss:create-categories-table
create table categories (
    id text primary key,
    parent text references categories(id),
    name text not null
);

comment on table categories is 'Категории';

comment on column categories.id is 'Идентификатор';
comment on column categories.parent is 'Идентификатор родительской категории';
comment on column categories.name is 'Название категории';

--changeset andryss:add-categories
insert into categories(id, parent, name) values
    -- Мастер-категория
    ('all', null, 'Все категории'),

    -- Одежда и обувь
    ('clothes',           'all',     'Одежда и обувь'),
    ('clothes.outerwear', 'clothes', 'Верхняя одежда'),
    ('clothes.casual',    'clothes', 'Повседневная одежда'),
    ('clothes.homewear',  'clothes', 'Домашняя одежда'),
    ('clothes.footwear',  'clothes', 'Обувь'),

    -- Электроника и гаджеты
    ('electronics',             'all',         'Электроника и гаджеты'),
    ('electronics.smartphones', 'electronics', 'Смартфоны и планшеты'),
    ('electronics.laptops',     'electronics', 'Ноутбуки и аксессуары'),
    ('electronics.audio',       'electronics', 'Наушники и колонки'),
    ('electronics.chargers',    'electronics', 'Зарядные устройства и кабели'),

    -- Товары для комнаты
    ('room',            'all',  'Товары для комнаты'),
    ('room.bedding',    'room', 'Постельное белье и пледы'),
    ('room.lighting',   'room', 'Освещение'),
    ('room.organizers', 'room', 'Органайзеры для хранения'),
    ('room.hangers',    'room', 'Вешалки и сушилки для одежды'),

    -- Кухня и питание
    ('kitchen',            'all',     'Кухня и питание'),
    ('kitchen.appliances', 'kitchen', 'Электроприборы'),
    ('kitchen.dishes',     'kitchen', 'Посуда и столовые приборы'),
    ('kitchen.containers', 'kitchen', 'Контейнеры для еды'),
    ('kitchen.food',       'kitchen', 'Продукты питания'),

    -- Бытовая химия и личная гигиена
    ('hygiene',               'all',     'Бытовая химия и личная гигиена'),
    ('hygiene.cleaning',      'hygiene', 'Средства для уборки'),
    ('hygiene.personal',      'hygiene', 'Личная гигиена'),
    ('hygiene.laundry',       'hygiene', 'Стиральные порошки и кондиционеры'),
    ('hygiene.airfresheners', 'hygiene', 'Освежители воздуха'),

    -- Учеба и канцелярия
    ('study',            'all',   'Учеба и канцелярия'),
    ('study.books',      'study', 'Книги и учебники'),
    ('study.stationery', 'study', 'Канцелярия'),
    ('study.organizers', 'study', 'Органайзеры для бумаг'),

    -- Спорт и активный отдых
    ('sports',            'all',    'Спорт и активный отдых'),
    ('sports.weights',    'sports', 'Гантели и эспандеры'),
    ('sports.yoga',       'sports', 'Йога-коврики'),
    ('sports.sportswear', 'sports', 'Спортивная одежда'),
    ('sports.bikes',      'sports', 'Велосипеды и самокаты'),

    -- Развлечения и досуг
    ('entertainment',            'all',           'Развлечения и досуг'),
    ('entertainment.boardgames', 'entertainment', 'Настольные игры'),
    ('entertainment.gaming',     'entertainment', 'Игровые приставки и аксессуары'),
    ('entertainment.music',      'entertainment', 'Музыкальные инструменты'),
    ('entertainment.hobbies',    'entertainment', 'Хобби'),

    -- Животные
    ('pets',        'all',  'Животные'),
    ('pets.food',   'pets', 'Корма и миски'),
    ('pets.litter', 'pets', 'Лотки и наполнители'),
    ('pets.toys',   'pets', 'Игрушки для животных');
