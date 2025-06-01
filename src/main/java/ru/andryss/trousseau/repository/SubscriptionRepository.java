package ru.andryss.trousseau.repository;

import java.util.List;
import java.util.Optional;

import ru.andryss.trousseau.model.SubscriptionEntity;

/**
 * Репозиторий для работы с сущностями подписок
 */
public interface SubscriptionRepository {
    /**
     * Сохранить новую подписку
     */
    SubscriptionEntity save(SubscriptionEntity subscription);

    /**
     * Найти подписку по идентификатору
     */
    Optional<SubscriptionEntity> findById(String id);

    /**
     * Найти подписку по идентификатору и автору
     */
    Optional<SubscriptionEntity> findByIdAndOwner(String id, String owner);

    /**
     * Обновить данные подписки
     */
    SubscriptionEntity update(SubscriptionEntity subscription);

    /**
     * Найти все подписки по автору
     */
    List<SubscriptionEntity> findAllByOwnerOrderByCreatedAt(String owner);

    /**
     * Удалить подписку по идентификатору и пользователю
     */
    void deleteByIdAndOwner(String id, String owner);

    /**
     * Найти подписки по идентификаторам
     */
    List<SubscriptionEntity> findAllByCategoryIdsHas(List<String> categoryIds);
}
