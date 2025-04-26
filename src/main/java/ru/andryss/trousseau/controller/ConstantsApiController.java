package ru.andryss.trousseau.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import ru.andryss.trousseau.generated.api.ConstantsApi;
import ru.andryss.trousseau.generated.model.CategoryNode;
import ru.andryss.trousseau.generated.model.CategoryTree;
import ru.andryss.trousseau.service.CategoryService;

@RestController
@RequiredArgsConstructor
public class ConstantsApiController implements ConstantsApi {

    private final CategoryService categoryService;

    @Override
    public CategoryTree getCategoryTree() {
        CategoryNode root = categoryService.getCategoryTree();

        return new CategoryTree().root(root);
    }

}
