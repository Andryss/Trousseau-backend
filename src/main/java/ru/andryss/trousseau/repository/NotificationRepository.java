package ru.andryss.trousseau.repository;

import java.util.List;

import ru.andryss.trousseau.model.NotificationEntity;

public interface NotificationRepository {
    void save(NotificationEntity entity);
    List<NotificationEntity> findAllByReceiverOrderByCreatedAtDesc(String receiver);
    int countByReceiverAndIsRead(String receiver, boolean isRead);
    void updateByIdAndReceiverSetIsRead(String id, String receiver, boolean isRead);
}
