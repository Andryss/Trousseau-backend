package ru.andryss.trousseau.repository;

import java.util.List;
import java.util.Optional;

import ru.andryss.trousseau.model.CategoryEntity;

/**
 * Репозиторий для работы с сущностями категорий
 */
public interface CategoryRepository {
    /**
     * Получить все категории
     */
    List<CategoryEntity> findAll();

    /**
     * Найти категорию по идентификатору
     */
    Optional<CategoryEntity> findById(String id);

    /**
     * Получить список идентификаторов категорий от заданной до корня дерева категорий
     */
    List<String> findPathToRoot(String id);
}
