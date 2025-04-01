package ru.andryss.trousseau.repository;

import java.util.List;
import java.util.Optional;

import ru.andryss.trousseau.model.CategoryEntity;

public interface CategoryRepository {
    List<CategoryEntity> findAll();
    Optional<CategoryEntity> findById(String id);
}
