package ru.andryss.trousseau.repository;

import java.util.List;
import java.util.Optional;

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
     * Найти событие по типу
     */
    Optional<EventEntity> findByType(EventType type);

    /**
     * Удалить события по идентификаторам
     */
    void deleteByIds(List<String> ids);
}
