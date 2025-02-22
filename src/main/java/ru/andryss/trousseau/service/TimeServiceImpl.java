package ru.andryss.trousseau.service;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import org.springframework.stereotype.Service;

@Service
public class TimeServiceImpl implements TimeService {
    @Override
    public ZonedDateTime now() {
        return Instant.now().atZone(ZoneOffset.UTC);
    }
}
