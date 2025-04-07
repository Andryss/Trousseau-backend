package ru.andryss.trousseau.repository;

import java.util.List;

import ru.andryss.trousseau.model.EventEntity;
import ru.andryss.trousseau.model.EventEntity.EventType;

public interface EventRepository {
    void save(EventEntity event);
    List<EventEntity> findAllByTypeOrderByCreatedAt(EventType type, int limit);
    void deleteByIds(List<String> ids);
}
