package ru.andryss.trousseau.service;

import java.util.List;

import ru.andryss.trousseau.generated.model.ItemInfoRequest;
import ru.andryss.trousseau.generated.model.SearchInfo;
import ru.andryss.trousseau.model.ItemEntity;
import ru.andryss.trousseau.model.ItemStatus;
import ru.andryss.trousseau.security.UserData;

public interface ItemService {
    ItemEntity createItem(UserData user, ItemInfoRequest info);
    ItemEntity updateItem(String id, UserData user, ItemInfoRequest info);
    List<ItemEntity> getItems(UserData user);
    ItemEntity getItem(String id);
    ItemEntity getItem(String id, UserData user);
    void changeSellerItemStatus(String id, UserData user, ItemStatus status);
    void changePublicItemStatus(String id, UserData user, ItemStatus status);
    List<ItemEntity> searchItems(SearchInfo search);
    ItemEntity getPublicItem(String itemId);
    List<ItemEntity> getBooked(UserData user);
}
