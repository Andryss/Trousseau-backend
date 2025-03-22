package ru.andryss.trousseau.service;

import java.util.List;

import ru.andryss.trousseau.model.ItemEntity;

public interface FavouriteService {
    void changeIsFavourite(String itemId, boolean isFavourite);
    List<ItemEntity> getAll();
}
