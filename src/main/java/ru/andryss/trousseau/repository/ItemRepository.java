package ru.andryss.trousseau.repository;

import ru.andryss.trousseau.model.ItemEntity;

public interface ItemRepository {
    void save(ItemEntity item);
}
