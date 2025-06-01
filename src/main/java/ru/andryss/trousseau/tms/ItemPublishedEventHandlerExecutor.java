package ru.andryss.trousseau.tms;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.text.StringSubstitutor;
import org.springframework.stereotype.Component;
import ru.andryss.trousseau.model.EventEntity;
import ru.andryss.trousseau.model.EventEntity.EventType;
import ru.andryss.trousseau.model.ItemEntity;
import ru.andryss.trousseau.model.SubscriptionEntity;
import ru.andryss.trousseau.service.CategoryService;
import ru.andryss.trousseau.service.EventService;
import ru.andryss.trousseau.service.ItemService;
import ru.andryss.trousseau.service.NotificationService;
import ru.andryss.trousseau.service.NotificationService.NotificationInfo;
import ru.andryss.trousseau.service.SubscriptionService;

import static ru.andryss.trousseau.service.NotificationService.NotificationInfo.itemLink;
import static ru.andryss.trousseau.service.NotificationService.NotificationInfo.subscriptionLink;

@Component
public class ItemPublishedEventHandlerExecutor extends BaseEventHandlerExecutor {

    private static final String NOTIFICATION_TITLE = "Новое объявление";
    private static final String NOTIFICATION_CONTENT = "Новое объявление \"${itemTitle}\"" +
            " по подписке \"${subscriptionName}\".";

    private final ItemService itemService;
    private final CategoryService categoryService;
    private final SubscriptionService subscriptionService;
    private final NotificationService notificationService;

    public ItemPublishedEventHandlerExecutor(
            EventService eventService,
            ItemService itemService,
            CategoryService categoryService,
            SubscriptionService subscriptionService,
            NotificationService notificationService) {
        super(eventService);
        this.itemService = itemService;
        this.categoryService = categoryService;
        this.subscriptionService = subscriptionService;
        this.notificationService = notificationService;
    }

    @Override
    public String cronExpression() {
        return "0 */5 * * * ?"; // each 5 minutes
    }

    @Override
    protected EventType getEventType() {
        return EventType.ITEM_PUBLISHED;
    }

    @Override
    protected void handleEvent(EventEntity event) {
        String itemId = (String) event.getPayload().get("itemId");
        ItemEntity item = itemService.getItem(itemId);

        List<String> categories = categoryService.getAllParents(item.getCategoryId());

        List<SubscriptionEntity> subscribed = subscriptionService.getSubscribedOnCategories(categories);

        for (SubscriptionEntity subscription : subscribed) {
            if (Objects.equals(subscription.getOwner(), item.getOwner())) {
                continue;
            }

            Map<String, String> params = Map.of(
                    "itemTitle", item.getTitle(),
                    "subscriptionName", subscription.getName()
            );
            StringSubstitutor substitutor = new StringSubstitutor(params);

            NotificationInfo info = new NotificationInfo(
                    subscription.getOwner(),
                    substitutor.replace(NOTIFICATION_TITLE),
                    substitutor.replace(NOTIFICATION_CONTENT),
                    List.of(itemLink(item), subscriptionLink(subscription))
            );

            notificationService.sendNotification(info);
        }
    }
}
