package ru.andryss.trousseau.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.andryss.trousseau.exception.Errors;
import ru.andryss.trousseau.generated.model.CategoryNode;
import ru.andryss.trousseau.model.CategoryEntity;
import ru.andryss.trousseau.repository.CategoryRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Override
    public CategoryNode getCategoryTree() {
        log.info("Getting category tree");

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

    @Override
    public String getCategoryName(String id) {
        log.info("Getting category {} name", id);

        Optional<CategoryEntity> category = categoryRepository.findById(id);
        if (category.isEmpty()) {
            throw Errors.categoryNotFound(id);
        }
        return category.get().getName();
    }

    @Override
    public List<String> getAllParents(String id) {
        return categoryRepository.findPathToRoot(id);
    }

    private CategoryNode formCategory(CategoryEntity category, Map<String, List<CategoryEntity>> categoriesByParentId) {
        CategoryNode dto = new CategoryNode()
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
