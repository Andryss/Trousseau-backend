package ru.andryss.trousseau.service;

import java.util.List;

import ru.andryss.trousseau.model.ItemEntity;

public interface ItemService {
    ItemEntity createItem(String title, List<String> mediaIds, String description);
}
