package ru.andryss.trousseau.tms;

import java.util.List;

import lombok.RequiredArgsConstructor;
import org.quartz.JobExecutionContext;
import org.springframework.stereotype.Component;
import ru.andryss.trousseau.model.EventEntity;
import ru.andryss.trousseau.model.EventEntity.EventType;
import ru.andryss.trousseau.service.EventService;
import ru.andryss.trousseau.service.KeyStorageService;

@Component
@RequiredArgsConstructor
public abstract class BaseEventHandlerExecutor extends BaseExecutor {

    private final KeyStorageService keyStorageService;
    private final EventService eventService;

    @Override
    public void doJob(JobExecutionContext context) {
        int batchSize = keyStorageService.get(
                String.format("eventHandlerExecutor.%s.batchSize", getEventType().getValue()), 5
        );

        List<EventEntity> events;
        do {
            events = eventService.readBatch(getEventType(), batchSize);
            log.info("Read next {} events", events.size());

            for (EventEntity event : events) {
                handleEvent(event);
            }

            eventService.delete(events);
        } while (events.size() == batchSize);
    }

    protected abstract EventType getEventType();
    protected abstract void handleEvent(EventEntity event);
}
