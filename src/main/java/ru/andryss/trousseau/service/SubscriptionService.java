package ru.andryss.trousseau.service;

import java.util.List;

import ru.andryss.trousseau.generated.model.SubscriptionInfoRequest;
import ru.andryss.trousseau.model.SubscriptionEntity;

public interface SubscriptionService {
    SubscriptionEntity create(SubscriptionInfoRequest request);
    void delete(String id);
    List<SubscriptionEntity> getAll();
    SubscriptionEntity update(String id, SubscriptionInfoRequest request);
    List<SubscriptionEntity> getSubscribedOnCategories(List<String> categoryIds);
}
