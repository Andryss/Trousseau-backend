package ru.andryss.trousseau.service;

import java.time.Clock;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Date;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("!functionalTest")
@RequiredArgsConstructor
public class TimeServiceImpl implements TimeService {

    private final Clock clock;

    @Override
    public ZonedDateTime nowWithZone() {
        return clock.instant().atZone(ZoneOffset.UTC);
    }
    @Override
    public long epochMillis() {
        return clock.millis();
    }

    @Override
    public Date now() {
        return Date.from(clock.instant());
    }
}
