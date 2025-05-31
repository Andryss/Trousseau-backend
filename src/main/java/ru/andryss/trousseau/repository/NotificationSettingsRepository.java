package ru.andryss.trousseau.repository;

import java.util.Optional;

import ru.andryss.trousseau.model.NotificationSettingsEntity;

public interface NotificationSettingsRepository {
    void upsert(NotificationSettingsEntity entity);
    Optional<String> findTokenByUserId(String userId);
}
