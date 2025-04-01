package ru.andryss.trousseau.service;

import ru.andryss.trousseau.generated.model.CategoryNode;

public interface CategoryService {
    CategoryNode getCategoryTree();
    String getCategoryName(String id);
}
