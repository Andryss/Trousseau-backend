package ru.andryss.trousseau.service;

/**
 * Сервис для работы с парами ключ-значение
 */
public interface KeyStorageService {
    /**
     * Положить значение по ключу
     */
    <T> void put(String key, T value);

    /**
     * Получить значение по ключу
     */
    <T> T get(String key, T defaultValue);
}
