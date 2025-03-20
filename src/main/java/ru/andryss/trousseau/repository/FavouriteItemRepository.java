package ru.andryss.trousseau.repository;

import ru.andryss.trousseau.model.FavouriteItemEntity;

public interface FavouriteItemRepository {
    void upsert(FavouriteItemEntity favourite);
}
