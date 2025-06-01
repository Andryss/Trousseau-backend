package ru.andryss.trousseau.repository;

import java.util.Optional;

/**
 * Репозиторий для работы с хранилищем ключ-значение
 */
public interface KeyStorageRepository {
    /**
     * Установить значение по ключу
     */
    void upsert(String key, String value);

    /**
     * Получить значение по ключу
     */
    Optional<String> get(String key);
}
