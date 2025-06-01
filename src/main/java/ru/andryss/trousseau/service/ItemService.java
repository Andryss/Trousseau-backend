package ru.andryss.trousseau.service;

import java.util.List;

import ru.andryss.trousseau.generated.model.ItemInfoRequest;
import ru.andryss.trousseau.generated.model.SearchInfo;
import ru.andryss.trousseau.model.ItemEntity;
import ru.andryss.trousseau.model.ItemStatus;
import ru.andryss.trousseau.security.UserData;

/**
 * Сервис для работы с объявдениями
 */
public interface ItemService {
    /**
     * Создать новое объявления пользователя
     */
    ItemEntity createItem(UserData user, ItemInfoRequest info);

    /**
     * Обновить заданное объявления пользователя
     */
    ItemEntity updateItem(String id, UserData user, ItemInfoRequest info);

    /**
     * Получить все созданные объявления пользователя
     */
    List<ItemEntity> getItems(UserData user);

    /**
     * Получить объявление по идентификатору
     */
    ItemEntity getItem(String id);

    /**
     * Получить созданное объявление пользователя
     */
    ItemEntity getItem(String id, UserData user);

    /**
     * Изменить статус объявления от лица автора объявления
     */
    void changeSellerItemStatus(String id, UserData user, ItemStatus status);

    /**
     * Изменить статус объявления от лица покупателя
     */
    void changePublicItemStatus(String id, UserData user, ItemStatus status);

    /**
     * Поиск объявлений по заданным параметрам
     */
    List<ItemEntity> searchItems(SearchInfo search);

    /**
     * Получить информацию об опубликованном объявлении
     */
    ItemEntity getPublicItem(String itemId);

    /**
     * Получить забронированные пользователем объявления
     */
    List<ItemEntity> getBooked(UserData user);
}
