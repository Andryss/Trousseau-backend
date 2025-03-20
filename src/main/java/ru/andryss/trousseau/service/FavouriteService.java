package ru.andryss.trousseau.service;

import java.util.List;

import ru.andryss.trousseau.model.ItemEntity;

public interface FavouriteService {
    void addFavourite(String itemId);
    List<ItemEntity> getAll();
}
