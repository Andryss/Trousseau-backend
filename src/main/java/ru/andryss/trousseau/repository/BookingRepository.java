package ru.andryss.trousseau.repository;

import java.util.List;
import java.util.Optional;

import ru.andryss.trousseau.model.BookingEntity;

/**
 * Репозиторий для работы с сущностями бронирований
 */
public interface BookingRepository {
    /**
     * Получить все бронирования пользователя
     */
    List<BookingEntity> findAllByUserId(String userId);

    /**
     * Найти бронирование по объявлению и его владельцу
     */
    Optional<BookingEntity> findByItemIdAndOwner(String itemId, String owner);

    /**
     * Сохранить новое бронирование
     */
    void save(BookingEntity booking);

    /**
     * Удалить бронирование по идентификатору объявления
     */
    int deleteByItemId(String itemId);

    /**
     * Удалить бронирование по идентификатору объявления и забронировавшему пользователю
     */
    int deleteByItemIdAndUserId(String itemId, String userId);
}
