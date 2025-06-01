package ru.andryss.trousseau.service;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.andryss.trousseau.model.EventEntity;
import ru.andryss.trousseau.model.EventEntity.EventType;
import ru.andryss.trousseau.repository.EventRepository;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final TimeService timeService;

    private final DateTimeFormatter idFormatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmssSSS");

    @Override
    public void push(EventType type, Map<String, Object> payload) {
        EventEntity event = new EventEntity();
        event.setType(type);
        event.setPayload(payload);

        ZonedDateTime now = timeService.now();
        event.setId(idFormatter.format(now));
        event.setCreatedAt(now.toInstant());

        eventRepository.save(event);
    }

    @Override
    public Optional<EventEntity> readNextEvent(EventType type) {
        return eventRepository.findByType(type);
    }

    @Override
    public void delete(List<EventEntity> events) {
        List<String> ids = events.stream()
                .map(EventEntity::getId)
                .toList();
        eventRepository.deleteByIds(ids);
    }
}
