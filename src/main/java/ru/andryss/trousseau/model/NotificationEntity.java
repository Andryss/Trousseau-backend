package ru.andryss.trousseau.model;

import java.time.Instant;
import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class NotificationEntity {
    private String id;
    private String title;
    private String content;
    private List<String> links;
    private boolean isRead;
    private Instant createdAt;
}
