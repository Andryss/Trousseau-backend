package ru.andryss.trousseau.service;

import java.time.ZonedDateTime;

/**
 * Сервис для получения текущего времени
 */
public interface TimeService extends io.jsonwebtoken.Clock {
    /**
     * Получить текущее время по Гринвичу
     */
    ZonedDateTime nowWithZone();

    /**
     * Получить текущее количество миллисекунд от начала отсчета времени
     * @see System#currentTimeMillis()
     */
    long epochMillis();
}
