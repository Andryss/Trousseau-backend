package ru.andryss.trousseau.repository;

import java.util.List;
import java.util.Optional;

import ru.andryss.trousseau.model.ItemEntity;

/**
 * Репозиторий для работы с сущностями объявлений
 */
public interface ItemRepository {
    /**
     * Сохранение нового объявления
     */
    ItemEntity save(ItemEntity item);

    /**
     * Обновление информации объявления
     */
    ItemEntity update(ItemEntity item);

    /**
     * Поиск объявления по идентификатору
     */
    Optional<ItemEntity> findById(String id);

    /**
     * Поиск объявления по идентификатору и автору
     */
    Optional<ItemEntity> findByIdAndOwner(String id, String owner);

    /**
     * Получить все объявления по автору
     */
    List<ItemEntity> findAllByOwnerOrderByCreatedAtDesc(String owner);

    /**
     * Получить все объявления, забронированные пользователем
     */
    List<ItemEntity> findAllBookedBy(String userId);

    /**
     * Получить все уведомления, добавленные пользователем в избранное
     */
    List<ItemEntity> findFavouritesOf(String userId);

    /**
     * Выполнить запрос на получение объявлений
     */
    List<ItemEntity> executeQuery(String query);
}
