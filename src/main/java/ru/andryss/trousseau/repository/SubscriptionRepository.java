package ru.andryss.trousseau.repository;

import java.util.List;
import java.util.Optional;

import ru.andryss.trousseau.model.SubscriptionEntity;

public interface SubscriptionRepository {
    SubscriptionEntity save(SubscriptionEntity subscription);
    Optional<SubscriptionEntity> findById(String id);
    SubscriptionEntity update(SubscriptionEntity subscription);
    List<SubscriptionEntity> findAllOrderByCreatedAt();
    void deleteById(String id);
    List<SubscriptionEntity> findAllByCategoryIdsHas(List<String> categoryIds);
}
