package ru.andryss.trousseau.service;

import java.util.List;

import ru.andryss.trousseau.generated.model.SubscriptionInfoRequest;
import ru.andryss.trousseau.model.SubscriptionEntity;
import ru.andryss.trousseau.security.UserData;

/**
 * Сервис для работы с подписками
 */
public interface SubscriptionService {
    /**
     * Создать новую подписку пользователя
     */
    SubscriptionEntity create(UserData user, SubscriptionInfoRequest request);

    /**
     * Удалить подписку пользователя
     */
    void delete(String id, UserData user);

    /**
     * Получить все подписки пользователя
     */
    List<SubscriptionEntity> getAll(UserData user);

    /**
     * Обновить данные о подписке пользователя
     */
    SubscriptionEntity update(String id, UserData user, SubscriptionInfoRequest request);

    /**
     * Получить подписки на публикацию объявлений хотя бы одной из заданных категорий
     */
    List<SubscriptionEntity> getSubscribedOnCategories(List<String> categoryIds);
}
