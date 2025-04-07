package ru.andryss.trousseau.repository;

import java.util.List;

import ru.andryss.trousseau.model.EventEntity;

public interface EventRepository {
    void save(EventEntity event);
    List<EventEntity> findAllOrderByCreatedAt(int limit);
    void deleteByIds(List<String> ids);
}
