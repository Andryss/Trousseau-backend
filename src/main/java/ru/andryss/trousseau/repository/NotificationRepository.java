package ru.andryss.trousseau.repository;

import java.util.List;

import ru.andryss.trousseau.model.NotificationEntity;

public interface NotificationRepository {
    void save(NotificationEntity entity);
    List<NotificationEntity> findAllOrderByCreatedAtDesc();
    int countWithIsRead(boolean isRead);
    void updateByIdSetIsRead(String id, boolean isRead);
}
