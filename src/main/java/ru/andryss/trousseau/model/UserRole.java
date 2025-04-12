package ru.andryss.trousseau.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserRole {
    USER("user", "USER"),
    SELLER("seller", "SELLER");

    private final String id;
    private final String role;
}
