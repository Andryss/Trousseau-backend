package ru.andryss.trousseau.repository;

import java.util.List;

import ru.andryss.trousseau.model.EventEntity;
import ru.andryss.trousseau.model.EventEntity.EventType;

/**
 * Репозиторий для работы с сущностями событий
 */
public interface EventRepository {
    /**
     * Сохранить новое событие
     */
    void save(EventEntity event);

    /**
     * Найти события по типу
     */
    List<EventEntity> findAllByTypeOrderByCreatedAt(EventType type, int limit);

    /**
     * Удалить события по идентификаторам
     */
    void deleteByIds(List<String> ids);
}
