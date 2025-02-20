package ru.andryss.trousseau.model;

import java.util.List;

import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@NoArgsConstructor
public class ItemEntity {
    private String title;
    private List<String> mediaIds;
    private String description;
}
