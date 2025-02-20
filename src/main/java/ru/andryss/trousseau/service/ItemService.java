package ru.andryss.trousseau.service;

import java.util.List;

public interface ItemService {
    void createItem(String title, List<String> mediaIds, String description);
}
