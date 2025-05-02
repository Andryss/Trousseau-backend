package ru.andryss.trousseau.service;

import java.util.List;
import java.util.Map;

import ru.andryss.trousseau.model.ItemEntity;
import ru.andryss.trousseau.security.UserData;

public interface FavouriteService {
    void changeIsFavourite(String itemId, UserData user, boolean isFavourite);
    List<ItemEntity> getAll(UserData user);
    boolean checkFavourite(UserData user, ItemEntity item);
    Map<String, Boolean> checkFavourite(UserData user, List<ItemEntity> items);
}
