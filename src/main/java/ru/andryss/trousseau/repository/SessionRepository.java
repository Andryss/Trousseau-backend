package ru.andryss.trousseau.repository;

import java.util.Optional;

import ru.andryss.trousseau.model.SessionEntity;

/**
 * Репозиторий для работы с сущностями сессий
 */
public interface SessionRepository {
    /**
     * Сохранение новой сессий пользователя
     */
    void save(SessionEntity session);

    /**
     * Поиск сессии по идентификатору
     */
    Optional<SessionEntity> findById(String id);

    /**
     * Удаление сессии по идентификатору
     */
    void removeById(String id);
}
