package ru.andryss.trousseau.model;

import java.time.Instant;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class BookingEntity {
    private String id;
    private String itemId;
    private String userId;
    private Instant bookedAt;
}
