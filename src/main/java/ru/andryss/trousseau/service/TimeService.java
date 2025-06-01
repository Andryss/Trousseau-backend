package ru.andryss.trousseau.service;

import java.time.ZonedDateTime;

/**
 * Сервис для получения текущего времени
 */
public interface TimeService {
    /**
     * Получить текущее время по Гринвичу
     */
    ZonedDateTime now();

    /**
     * Получить текущее количество миллисекунд от начала отсчета времени
     * @see System#currentTimeMillis()
     */
    long epochMillis();
}
