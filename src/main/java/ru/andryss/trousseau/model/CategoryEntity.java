package ru.andryss.trousseau.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class CategoryEntity {
    private String id;
    private String parent;
    private String name;
}
