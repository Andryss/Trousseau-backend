package ru.andryss.trousseau.service;

import java.util.Optional;

import ru.andryss.trousseau.model.SessionEntity;

/**
 * Сервис для работы с сессиями
 */
public interface SessionService {
    /**
     * Получить сессию по идентификатору
     */
    Optional<SessionEntity> getById(String id);

    /**
     * Инициировать создание новой сессии пользователя
     */
    void newSession(SessionEntity session);

    /**
     * Удалить сессию
     */
    void deleteById(String id);
}
