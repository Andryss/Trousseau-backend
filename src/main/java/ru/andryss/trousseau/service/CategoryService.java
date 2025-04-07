package ru.andryss.trousseau.service;

import java.util.List;

import ru.andryss.trousseau.generated.model.CategoryNode;

public interface CategoryService {
    CategoryNode getCategoryTree();
    String getCategoryName(String id);
    List<String> getAllParents(String id);
}
