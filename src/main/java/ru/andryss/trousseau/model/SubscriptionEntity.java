package ru.andryss.trousseau.model;

import java.time.Instant;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SubscriptionEntity {
    private String id;
    private String owner;
    private String name;
    private SubscriptionInfo data;
    private Instant createdAt;
}
