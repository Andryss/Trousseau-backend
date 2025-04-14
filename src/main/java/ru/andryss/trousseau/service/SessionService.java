package ru.andryss.trousseau.service;

import java.util.Optional;

import ru.andryss.trousseau.model.SessionEntity;

public interface SessionService {
    Optional<SessionEntity> getById(String id);
    void newSession(SessionEntity session);
    void deleteById(String id);
}
