package ru.andryss.trousseau.repository;

import java.util.List;

import ru.andryss.trousseau.model.ItemEntity;

public interface ItemRepository {
    ItemEntity save(ItemEntity item);
    ItemEntity update(ItemEntity item);
    ItemEntity findById(String id);
    List<ItemEntity> findAll();
}
