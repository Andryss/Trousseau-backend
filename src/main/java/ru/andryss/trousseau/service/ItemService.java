package ru.andryss.trousseau.service;

import java.util.List;

import ru.andryss.trousseau.generated.model.ItemInfoRequest;
import ru.andryss.trousseau.generated.model.SearchInfo;
import ru.andryss.trousseau.model.ItemEntity;
import ru.andryss.trousseau.model.ItemStatus;

public interface ItemService {
    ItemEntity createItem(ItemInfoRequest info);
    ItemEntity updateItem(String id, ItemInfoRequest info);
    List<ItemEntity> getItems();
    ItemEntity getItem(String id);
    void changeItemStatus(String id, ItemStatus status);
    List<ItemEntity> searchItems(SearchInfo search);
    ItemEntity getPublicItem(String itemId);
}
