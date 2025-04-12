package ru.andryss.trousseau.service;

import java.util.Optional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.andryss.trousseau.model.SessionEntity;
import ru.andryss.trousseau.repository.SessionRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class SessionServiceImpl implements SessionService {

    private final SessionRepository sessionRepository;

    @Override
    public Optional<SessionEntity> getById(String id) {
        log.info("Getting session by id");

        return sessionRepository.findById(id);
    }

    @Override
    public void newSession(SessionEntity session) {
        log.info("Saving new session for user {}", session.getUserId());

        sessionRepository.save(session);
    }
}
