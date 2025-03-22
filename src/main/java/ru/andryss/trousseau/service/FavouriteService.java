package ru.andryss.trousseau.service;

import java.util.List;
import java.util.Map;

import ru.andryss.trousseau.model.ItemEntity;

public interface FavouriteService {
    void changeIsFavourite(String itemId, boolean isFavourite);
    List<ItemEntity> getAll();
    boolean checkFavourite(ItemEntity item);
    Map<String, Boolean> checkFavourite(List<ItemEntity> items);
}
