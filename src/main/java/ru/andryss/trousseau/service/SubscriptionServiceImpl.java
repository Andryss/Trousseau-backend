package ru.andryss.trousseau.service;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;
import ru.andryss.trousseau.exception.Errors;
import ru.andryss.trousseau.generated.model.SubscriptionInfoRequest;
import ru.andryss.trousseau.model.SubscriptionEntity;
import ru.andryss.trousseau.model.SubscriptionInfo;
import ru.andryss.trousseau.repository.SubscriptionRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class SubscriptionServiceImpl implements SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final TimeService timeService;
    private final TransactionTemplate transactionTemplate;

    private final DateTimeFormatter idFormatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmssSSS");

    @Override
    public SubscriptionEntity create(SubscriptionInfoRequest request) {
        log.info("Creating subscription name={} data={}", request.getName(), request.getData());

        SubscriptionEntity subscription = new SubscriptionEntity();
        patch(subscription, request);

        ZonedDateTime now = timeService.now();
        subscription.setId(idFormatter.format(now));
        subscription.setCreatedAt(now.toInstant());

        return subscriptionRepository.save(subscription);
    }

    @Override
    public void delete(String id) {
        log.info("Deleting subscription with id={}", id);

        subscriptionRepository.deleteById(id);
    }

    @Override
    public List<SubscriptionEntity> getAll() {
        log.info("Getting all subscriptions");

        return subscriptionRepository.findAllOrderByCreatedAt();
    }

    @Override
    public SubscriptionEntity update(String id, SubscriptionInfoRequest request) {
        log.info("Updating subscription id={} name={} data={}", id, request.getName(), request.getData());

        return transactionTemplate.execute(status -> {
            Optional<SubscriptionEntity> optional = subscriptionRepository.findById(id);

            if (optional.isEmpty()) {
                throw Errors.subscriptionNotFound(id);
            }

            SubscriptionEntity subscription = optional.get();
            patch(subscription, request);

            subscriptionRepository.update(subscription);

            return subscription;
        });
    }

    @Override
    public List<SubscriptionEntity> getSubscribedOnCategories(List<String> categoryIds) {
        return subscriptionRepository.findAllByCategoryIdsHas(categoryIds);
    }

    private void patch(SubscriptionEntity subscription, SubscriptionInfoRequest request) {
        SubscriptionInfo info = new SubscriptionInfo();
        info.setCategoryIds(request.getData().getCategoryIds());

        subscription.setName(request.getName());
        subscription.setData(info);
    }
}
