package ru.andryss.trousseau.model;

import java.time.Instant;
import java.util.Map;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SessionEntity {
    private String id;
    private String userId;
    private Map<String, Object> meta;
    private Instant createdAt;
}
