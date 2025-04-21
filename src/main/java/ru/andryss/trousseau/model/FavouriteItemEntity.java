package ru.andryss.trousseau.model;

import java.time.Instant;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class FavouriteItemEntity {
    private String id;
    private String userId;
    private String itemId;
    private Instant createdAt;
}
