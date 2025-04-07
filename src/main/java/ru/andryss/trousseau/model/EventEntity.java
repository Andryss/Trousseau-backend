package ru.andryss.trousseau.model;

import java.time.Instant;
import java.util.Map;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class EventEntity {
    private String id;
    private EventType type;
    private Map<String, Object> payload;
    private Instant createdAt;

    @Getter
    @RequiredArgsConstructor
    public enum EventType {
        ITEM_PUBLISHED("ITEM_PUBLISHED");

        private final String value;
    }
}
