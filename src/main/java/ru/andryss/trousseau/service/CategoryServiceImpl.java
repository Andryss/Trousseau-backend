package ru.andryss.trousseau.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.andryss.trousseau.generated.model.CategoryDto;
import ru.andryss.trousseau.model.CategoryEntity;
import ru.andryss.trousseau.repository.CategoryRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Override
    public CategoryDto getCategoryTree() {
        List<CategoryEntity> categories = categoryRepository.findAll();

        HashMap<String, List<CategoryEntity>> categoriesByParentId = new HashMap<>();

        for (CategoryEntity category : categories) {
            categoriesByParentId.computeIfAbsent(category.getParent(), s -> new ArrayList<>()).add(category);
        }

        List<CategoryEntity> rootArray = categoriesByParentId.get(null);

        if (rootArray.size() > 1) {
            log.warn("Found more than one root category: {}", rootArray);
        }

        return formCategory(rootArray.get(0), categoriesByParentId);
    }

    private CategoryDto formCategory(CategoryEntity category, Map<String, List<CategoryEntity>> categoriesByParentId) {
        CategoryDto dto = new CategoryDto()
                .id(category.getId())
                .name(category.getName());

        List<CategoryEntity> children = categoriesByParentId.get(category.getId());

        if (children != null) {
            for (CategoryEntity child : children) {
                dto.addChildrenItem(formCategory(child, categoriesByParentId));
            }
        }

        return dto;
    }
}
