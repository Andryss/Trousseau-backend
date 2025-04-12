package ru.andryss.trousseau.repository;

import java.util.Optional;

import ru.andryss.trousseau.model.SessionEntity;

public interface SessionRepository {
    void save(SessionEntity session);
    Optional<SessionEntity> findById(String id);
}
