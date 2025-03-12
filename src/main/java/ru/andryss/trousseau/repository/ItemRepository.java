package ru.andryss.trousseau.repository;

import java.util.List;

import ru.andryss.trousseau.model.ItemEntity;
import ru.andryss.trousseau.model.ItemStatus;

public interface ItemRepository {
    ItemEntity save(ItemEntity item);
    ItemEntity update(ItemEntity item);
    ItemEntity findById(String id);
    List<ItemEntity> findAll();
    List<ItemEntity> findByStatus(ItemStatus status);
    ItemEntity findByIdAndStatus(String id, ItemStatus status);
}
