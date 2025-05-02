package ru.andryss.trousseau.repository;

import java.util.List;

import ru.andryss.trousseau.model.FavouriteItemEntity;

public interface FavouriteItemRepository {
    void upsert(FavouriteItemEntity favourite);
    void deleteByItemIdAndUserId(String itemId, String userId);
    List<String> existsByUserIdAndItemIds(String userId, List<String> itemIds);
}
