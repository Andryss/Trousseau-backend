package ru.andryss.trousseau.service;

import java.util.List;
import java.util.Map;

import ru.andryss.trousseau.model.EventEntity;
import ru.andryss.trousseau.model.EventEntity.EventType;

public interface EventService {
    void push(EventType type, Map<String, Object> payload);
    List<EventEntity> readBatch(int limit);
    void delete(List<String> ids);
}
