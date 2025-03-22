package ru.andryss.trousseau.repository;

import java.util.List;

import ru.andryss.trousseau.model.FavouriteItemEntity;

public interface FavouriteItemRepository {
    void upsert(FavouriteItemEntity favourite);
    void deleteByItemId(String itemId);
    List<String> existsByItemId(List<String> itemIds);
}
