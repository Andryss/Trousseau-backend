package ru.andryss.trousseau.service;

import java.util.List;

import ru.andryss.trousseau.model.NotificationEntity;

public interface NotificationService {
    List<NotificationEntity> getAll();
    int getUnreadCount();
    void markRead(String id);
    void sendNotification(NotificationInfo info);

    record NotificationInfo(String title, String content, List<Object> links) { }
}
