package ru.andryss.trousseau.repository;

import java.util.Optional;

public interface KeyStorageRepository {
    void upsert(String key, String value);
    Optional<String> get(String key);
}
