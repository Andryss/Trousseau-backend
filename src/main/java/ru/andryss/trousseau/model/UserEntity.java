package ru.andryss.trousseau.model;

import java.time.Instant;
import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class UserEntity {
    private String id;
    private String username;
    private String passwordHash;
    private List<String> contacts;
    private String room;
    private Instant createdAt;
}
