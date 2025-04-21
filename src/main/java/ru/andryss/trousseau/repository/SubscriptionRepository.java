package ru.andryss.trousseau.repository;

import java.util.List;
import java.util.Optional;

import ru.andryss.trousseau.model.SubscriptionEntity;

public interface SubscriptionRepository {
    SubscriptionEntity save(SubscriptionEntity subscription);
    Optional<SubscriptionEntity> findById(String id);
    Optional<SubscriptionEntity> findByIdAndOwner(String id, String owner);
    SubscriptionEntity update(SubscriptionEntity subscription);
    List<SubscriptionEntity> findAllByOwnerOrderByCreatedAt(String owner);
    void deleteByIdAndOwner(String id, String owner);
    List<SubscriptionEntity> findAllByCategoryIdsHas(List<String> categoryIds);
}
