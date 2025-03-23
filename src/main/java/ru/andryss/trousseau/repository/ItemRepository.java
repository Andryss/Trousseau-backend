package ru.andryss.trousseau.repository;

import java.util.List;
import java.util.Optional;

import ru.andryss.trousseau.model.ItemEntity;
import ru.andryss.trousseau.model.ItemStatus;

public interface ItemRepository {
    ItemEntity save(ItemEntity item);
    ItemEntity update(ItemEntity item);
    Optional<ItemEntity> findById(String id);
    List<ItemEntity> findAllOrderByCreatedAtDesc();
    List<ItemEntity> findAllByStatusOrderByCreatedAtDesc(ItemStatus status);
    List<ItemEntity> findAllByStatusOrderByCreatedAtDesc(ItemStatus status, int limit);
    List<ItemEntity> findAllFavourites();
}
