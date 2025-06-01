package ru.andryss.trousseau.repository;

import java.util.List;

import ru.andryss.trousseau.model.NotificationEntity;

/**
 * Репозиторий для работы с сущностями уведомлений
 */
public interface NotificationRepository {
    /**
     * Сохранить новое уведомление
     */
    void save(NotificationEntity entity);

    /**
     * Получить все уведомления пользователя
     */
    List<NotificationEntity> findAllByReceiverOrderByCreatedAtDesc(String receiver);

    /**
     * Посчитать количество уведомлений пользователя по признаку прочитанности
     */
    int countByReceiverAndIsRead(String receiver, boolean isRead);

    /**
     * Установить признак прочитанности уведомления
     */
    void updateByIdAndReceiverSetIsRead(String id, String receiver, boolean isRead);
}
