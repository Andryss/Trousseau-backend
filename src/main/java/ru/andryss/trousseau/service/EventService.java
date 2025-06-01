package ru.andryss.trousseau.service;

import java.util.List;
import java.util.Map;

import ru.andryss.trousseau.model.EventEntity;
import ru.andryss.trousseau.model.EventEntity.EventType;

/**
 * Сервис для работы с событиями
 */
public interface EventService {
    /**
     * Создать новое событие
     */
    void push(EventType type, Map<String, Object> payload);

    /**
     * Прочитать группу событий заданного типа
     */
    List<EventEntity> readBatch(EventType type, int limit);

    /**
     * Удалить группу событий
     */
    void delete(List<EventEntity> events);
}
