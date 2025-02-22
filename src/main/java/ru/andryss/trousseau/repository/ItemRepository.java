package ru.andryss.trousseau.repository;

import ru.andryss.trousseau.model.ItemEntity;

public interface ItemRepository {
    ItemEntity save(ItemEntity item);
    ItemEntity update(ItemEntity item);
    ItemEntity findById(String id);
}
