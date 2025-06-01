package ru.andryss.trousseau.tms;

import java.util.List;
import java.util.Map;

import org.apache.commons.text.StringSubstitutor;
import org.springframework.stereotype.Component;
import ru.andryss.trousseau.model.EventEntity;
import ru.andryss.trousseau.model.EventEntity.EventType;
import ru.andryss.trousseau.model.ItemEntity;
import ru.andryss.trousseau.service.EventService;
import ru.andryss.trousseau.service.ItemService;
import ru.andryss.trousseau.service.NotificationService;
import ru.andryss.trousseau.service.NotificationService.NotificationInfo;

import static ru.andryss.trousseau.service.NotificationService.NotificationInfo.sellerItemLink;

@Component
public class ItemBookedEventHandlerExecutor extends BaseEventHandlerExecutor {

    private static final String NOTIFICATION_TITLE = "Новое бронирование";
    private static final String NOTIFICATION_CONTENT = "Объявление \"${itemTitle}\" было забронировано";

    private final ItemService itemService;
    private final NotificationService notificationService;

    public ItemBookedEventHandlerExecutor(
            EventService eventService,
            ItemService itemService,
            NotificationService notificationService) {
        super(eventService);
        this.itemService = itemService;
        this.notificationService = notificationService;
    }

    @Override
    public String cronExpression() {
        return "0 2/5 * * * ?"; // each 5 minutes
    }

    @Override
    protected EventType getEventType() {
        return EventType.ITEM_BOOKED;
    }

    @Override
    protected void handleEvent(EventEntity event) {
        String itemId = (String) event.getPayload().get("itemId");

        ItemEntity item = itemService.getItem(itemId);

        Map<String, String> params = Map.of(
                "itemTitle", item.getTitle()
        );
        StringSubstitutor substitutor = new StringSubstitutor(params);

        NotificationInfo info = new NotificationInfo(
                item.getOwner(),
                substitutor.replace(NOTIFICATION_TITLE),
                substitutor.replace(NOTIFICATION_CONTENT),
                List.of(sellerItemLink(item))
        );

        notificationService.sendNotification(info);
    }
}
