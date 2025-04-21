package ru.andryss.trousseau.repository;

import java.util.List;
import java.util.Optional;

import ru.andryss.trousseau.model.ItemEntity;

public interface ItemRepository {
    ItemEntity save(ItemEntity item);
    ItemEntity update(ItemEntity item);
    Optional<ItemEntity> findById(String id);
    Optional<ItemEntity> findByIdAndOwner(String id, String owner);
    List<ItemEntity> findAllByOwnerOrderByCreatedAtDesc(String owner);
    List<ItemEntity> findAllBookedBy(String userId);
    List<ItemEntity> findFavouritesOf(String userId);
    List<ItemEntity> executeQuery(String query);
}
