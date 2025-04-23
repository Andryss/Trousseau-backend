package ru.andryss.trousseau.service;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

@Service
@Primary
public class MockTimeService implements TimeService {

    private static final long epochMillisDefault = 1_716_208_200_000L;
    private long epochMillis = epochMillisDefault;

    public void reset() {
        epochMillis = epochMillisDefault;
    }

    @Override
    public ZonedDateTime now() {
        epochMillis += 1000;
        return Instant.ofEpochMilli(epochMillis).atZone(ZoneOffset.UTC);
    }

    @Override
    public long epochMillis() {
        epochMillis += 1000;
        return epochMillis;
    }
}
