package ru.andryss.trousseau.service;

import ru.andryss.trousseau.model.BookingEntity;
import ru.andryss.trousseau.security.UserData;

/**
 * Сервис для работы с бронированиями
 */
public interface BookingService {
    /**
     * Найти бронирование объявления от лица заданного пользователя
     */
    BookingEntity findByItem(String itemId, UserData user);
}
