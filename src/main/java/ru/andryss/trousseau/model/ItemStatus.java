package ru.andryss.trousseau.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ItemStatus {
    DRAFT("DRAFT"),
    READY("READY"),
    PUBLISHED("PUBLISHED"),
    BOOKED("BOOKED"),
    ARCHIVED("ARCHIVED");

    private final String value;

    public static ItemStatus fromValue(String value) {
        for (ItemStatus status : values()) {
            if (status.value.equals(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown item status value: " + value);
    }

    public ru.andryss.trousseau.generated.model.ItemStatus toOpenApi() {
        return switch (this) {
            case DRAFT -> ru.andryss.trousseau.generated.model.ItemStatus.DRAFT;
            case READY -> ru.andryss.trousseau.generated.model.ItemStatus.READY;
            case PUBLISHED -> ru.andryss.trousseau.generated.model.ItemStatus.PUBLISHED;
            case BOOKED -> ru.andryss.trousseau.generated.model.ItemStatus.BOOKED;
            case ARCHIVED -> ru.andryss.trousseau.generated.model.ItemStatus.ARCHIVED;
        };
    }
}
