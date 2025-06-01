package ru.andryss.trousseau.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

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
     * Прочитать событие заданного типа
     */
    Optional<EventEntity> readNextEvent(EventType type);

    /**
     * Удалить группу событий
     */
    void delete(List<EventEntity> events);
}
