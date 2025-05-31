package ru.andryss.trousseau.service;

import java.util.List;

import ru.andryss.trousseau.model.ItemEntity;
import ru.andryss.trousseau.model.NotificationEntity;
import ru.andryss.trousseau.model.SubscriptionEntity;
import ru.andryss.trousseau.security.UserData;

public interface NotificationService {
    List<NotificationEntity> getAll(UserData user);
    int getUnreadCount(UserData user);
    void markRead(String id, UserData user);
    void updateToken(String token, UserData user);
    void sendNotification(NotificationInfo info);

    record NotificationInfo(String receiver, String title, String content, List<String> links) {
        public static String itemLink(ItemEntity item) {
            return "item:" + item.getId();
        }
        public static String subscriptionLink(SubscriptionEntity subscription) {
            return "subscription:" + subscription.getId();
        }
        public static String sellerItemLink(ItemEntity item) {
            return "sellerItem:" + item.getId();
        }
    }
}
