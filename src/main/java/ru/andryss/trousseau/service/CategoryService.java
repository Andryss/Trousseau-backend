package ru.andryss.trousseau.service;

import java.util.List;

import ru.andryss.trousseau.generated.model.CategoryNode;

/**
 * Сервис для работы с категориями
 */
public interface CategoryService {
    /**
     * Получить полное дерево категорий
     */
    CategoryNode getCategoryTree();

    /**
     * Получить название категории
     */
    String getCategoryName(String id);

    /**
     * Получение всех родителей для категории
     */
    List<String> getAllParents(String id);
}
