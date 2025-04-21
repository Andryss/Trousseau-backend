package ru.andryss.trousseau.tms;

import java.util.List;
import java.util.Map;

import lombok.RequiredArgsConstructor;
import org.apache.commons.text.StringSubstitutor;
import org.quartz.JobExecutionContext;
import org.springframework.stereotype.Component;
import ru.andryss.trousseau.model.EventEntity;
import ru.andryss.trousseau.model.EventEntity.EventType;
import ru.andryss.trousseau.model.ItemEntity;
import ru.andryss.trousseau.model.SubscriptionEntity;
import ru.andryss.trousseau.service.CategoryService;
import ru.andryss.trousseau.service.EventService;
import ru.andryss.trousseau.service.ItemService;
import ru.andryss.trousseau.service.KeyStorageService;
import ru.andryss.trousseau.service.NotificationService;
import ru.andryss.trousseau.service.NotificationService.NotificationInfo;
import ru.andryss.trousseau.service.SubscriptionService;

@Component
@RequiredArgsConstructor
public class ItemPublishedEventHandlerExecutor extends BaseExecutor {

    private static final String NOTIFICATION_TITLE = "Новое объявление";
    private static final String NOTIFICATION_CONTENT = "Новое объявление \"${itemTitle}\"" +
            " по подписке \"${subscriptionName}\".";

    private final KeyStorageService keyStorageService;
    private final EventService eventService;
    private final ItemService itemService;
    private final CategoryService categoryService;
    private final SubscriptionService subscriptionService;
    private final NotificationService notificationService;

    @Override
    public String cronExpression() {
        return "0 */5 * * * ?"; // each 5 minutes
    }

    @Override
    public void doJob(JobExecutionContext context) {
        if (!keyStorageService.get("itemPublishedEventHandlerExecutor.enabled", true)) {
            log.info("Skipping ItemPublishedEventHandlerExecutor");
            return;
        }

        log.info("ItemPublishedEventHandlerExecutor started");

        int batchSize = keyStorageService.get("itemPublishedEventHandlerExecutor.batchSize", 5);

        List<EventEntity> events;
        do {
            events = eventService.readBatch(EventType.ITEM_PUBLISHED, batchSize);
            log.info("Read next {} events", events.size());

            for (EventEntity event : events) {
                handleEvent(event);
            }

            eventService.delete(events);
        } while (events.size() == batchSize);

        log.info("ItemPublishedEventHandlerExecutor finished");
    }

    private void handleEvent(EventEntity event) {
        String itemId = (String) event.getPayload().get("itemId");
        ItemEntity item = itemService.getItem(itemId);

        List<String> categories = categoryService.getAllParents(item.getCategoryId());

        List<SubscriptionEntity> subscribed = subscriptionService.getSubscribedOnCategories(categories);

        for (SubscriptionEntity subscription : subscribed) {
            Map<String, String> params = Map.of(
                    "itemTitle", item.getTitle(),
                    "subscriptionName", subscription.getName()
            );
            StringSubstitutor substitutor = new StringSubstitutor(params);

            NotificationInfo info = new NotificationInfo(
                    subscription.getOwner(),
                    substitutor.replace(NOTIFICATION_TITLE),
                    substitutor.replace(NOTIFICATION_CONTENT),
                    List.of(item, subscription)
            );

            notificationService.sendNotification(info);
        }
    }
}
