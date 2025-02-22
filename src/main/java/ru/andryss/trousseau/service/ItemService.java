package ru.andryss.trousseau.service;

import ru.andryss.trousseau.generated.model.ItemInfoRequest;
import ru.andryss.trousseau.model.ItemEntity;

public interface ItemService {
    ItemEntity createItem(ItemInfoRequest info);
    ItemEntity updateItem(String id, ItemInfoRequest info);
}
