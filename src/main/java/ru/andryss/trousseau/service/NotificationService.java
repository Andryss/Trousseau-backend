package ru.andryss.trousseau.service;

import java.util.List;

import ru.andryss.trousseau.model.ItemEntity;
import ru.andryss.trousseau.model.NotificationEntity;
import ru.andryss.trousseau.model.SubscriptionEntity;
import ru.andryss.trousseau.security.UserData;

/**
 * Сервис для работы с уведомлениями
 */
public interface NotificationService {
    /**
     * Получить все уведомления пользователя
     */
    List<NotificationEntity> getAll(UserData user);

    /**
     * Получить количество непрочитанных уведомлений пользователя
     */
    int getUnreadCount(UserData user);

    /**
     * Отметить уведомление пользователя прочитанным
     */
    void markRead(String id, UserData user);

    /**
     * Обновить токен пользователя для отправки сообщений
     * @see RuntimeMessagingService
     */
    void updateToken(String token, UserData user);

    /**
     * Отправить уведомление пользователю с заданным содержимым
     */
    void sendNotification(NotificationInfo info);

    record NotificationInfo(String receiver, String title, String content, List<String> links) {
        /**
         * Сформировать ссылку на заданное объявление
         */
        public static String itemLink(ItemEntity item) {
            return "item:" + item.getId();
        }

        /**
         * Сформировать ссылку на заданную подписку
         */
        public static String subscriptionLink(SubscriptionEntity subscription) {
            return "subscription:" + subscription.getId();
        }

        /**
         * Сформировать ссылку на созданное объявление
         */
        public static String sellerItemLink(ItemEntity item) {
            return "sellerItem:" + item.getId();
        }
    }
}
