package ru.andryss.trousseau.repository;

import java.util.List;

import ru.andryss.trousseau.model.CategoryEntity;

public interface CategoryRepository {
    List<CategoryEntity> findAll();
}
