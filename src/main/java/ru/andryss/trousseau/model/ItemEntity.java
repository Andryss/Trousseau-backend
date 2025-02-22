package ru.andryss.trousseau.model;

import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class ItemEntity {
    private String id;
    private String title;
    private List<String> mediaIds;
    private String description;
    private ItemStatus status;
}
