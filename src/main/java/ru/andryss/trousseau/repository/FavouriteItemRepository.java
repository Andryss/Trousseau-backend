package ru.andryss.trousseau.repository;

import java.util.List;

import ru.andryss.trousseau.model.FavouriteItemEntity;

/**
 * Репозиторий для работы с сущностями избранного
 */
public interface FavouriteItemRepository {
    /**
     * Сохранить или изменить сущность избранного
     */
    void upsert(FavouriteItemEntity favourite);

    /**
     * Удалить информацию об избранном по объявлению и пользователю
     */
    void deleteByItemIdAndUserId(String itemId, String userId);

    /**
     * Проверить наличие избранного по пользователю и объявлениям
     */
    List<String> existsByUserIdAndItemIds(String userId, List<String> itemIds);
}
