package ru.andryss.trousseau.service;

import java.time.Instant;

import org.springframework.stereotype.Service;

@Service
public class TimeServiceImpl implements TimeService {
    @Override
    public Instant now() {
        return Instant.now();
    }
}
