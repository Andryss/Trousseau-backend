package ru.andryss.trousseau.model;

import java.time.Instant;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class NotificationSettingsEntity {
    private String userId;
    private String token;
    private Instant updatedAt;
    private Instant createdAt;
}
