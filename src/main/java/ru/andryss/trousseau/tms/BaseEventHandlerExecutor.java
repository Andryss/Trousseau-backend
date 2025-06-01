package ru.andryss.trousseau.tms;

import java.util.List;
import java.util.Optional;

import lombok.RequiredArgsConstructor;
import org.quartz.JobExecutionContext;
import org.springframework.stereotype.Component;
import ru.andryss.trousseau.model.EventEntity;
import ru.andryss.trousseau.model.EventEntity.EventType;
import ru.andryss.trousseau.service.EventService;

@Component
@RequiredArgsConstructor
public abstract class BaseEventHandlerExecutor extends BaseExecutor {

    private final EventService eventService;

    @Override
    public void doJob(JobExecutionContext context) {
        Optional<EventEntity> eventOptional;
        do {
            eventOptional = eventService.readNextEvent(getEventType());
            if (eventOptional.isEmpty()) {
                break;
            }

            EventEntity event = eventOptional.get();
            log.info("Read next event {}", event.getId());

            handleEvent(event);

            eventService.delete(List.of(event));
            log.info("Event {} handled", event.getId());
        } while (true);
    }

    protected abstract EventType getEventType();
    protected abstract void handleEvent(EventEntity event);
}
