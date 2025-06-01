package ru.andryss.trousseau.service;

import java.util.List;
import java.util.Map;

import ru.andryss.trousseau.model.ItemEntity;
import ru.andryss.trousseau.security.UserData;

/**
 * Сервис для работы с избранным пользователя
 */
public interface FavouriteService {
    /**
     * Изменить наличие в избранном пользователя для объявления
     */
    void changeIsFavourite(String itemId, UserData user, boolean isFavourite);

    /**
     * Получить все объявления в избранном для пользователя
     */
    List<ItemEntity> getAll(UserData user);

    /**
     * Проверить наличие объявления в избранном пользователя
     */
    boolean checkFavourite(UserData user, ItemEntity item);

    /**
     * Проверить наличие группы объявлений в избранном пользователя
     */
    Map<String, Boolean> checkFavourite(UserData user, List<ItemEntity> items);
}
