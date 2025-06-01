package ru.andryss.trousseau.repository;

import java.util.Optional;

import ru.andryss.trousseau.model.NotificationSettingsEntity;

/**
 * Репозиторий для работы с сущностями настроек уведомлений
 */
public interface NotificationSettingsRepository {
    /**
     * Сохранить новую настройку уведомлений
     */
    void upsert(NotificationSettingsEntity entity);

    /**
     * Получить токен настройки по идентификатору пользователя
     */
    Optional<String> findTokenByUserId(String userId);
}
