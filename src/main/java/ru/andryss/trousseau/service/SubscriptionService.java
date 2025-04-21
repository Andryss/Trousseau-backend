package ru.andryss.trousseau.service;

import java.util.List;

import ru.andryss.trousseau.generated.model.SubscriptionInfoRequest;
import ru.andryss.trousseau.model.SubscriptionEntity;
import ru.andryss.trousseau.security.UserData;

public interface SubscriptionService {
    SubscriptionEntity create(UserData user, SubscriptionInfoRequest request);
    void delete(String id, UserData user);
    List<SubscriptionEntity> getAll(UserData user);
    SubscriptionEntity update(String id, UserData user, SubscriptionInfoRequest request);
    List<SubscriptionEntity> getSubscribedOnCategories(List<String> categoryIds);
}
