package ru.andryss.trousseau.service;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.andryss.trousseau.model.FavouriteItemEntity;
import ru.andryss.trousseau.model.ItemEntity;
import ru.andryss.trousseau.repository.FavouriteItemRepository;
import ru.andryss.trousseau.repository.ItemRepository;
import ru.andryss.trousseau.security.UserData;

import static java.util.stream.Collectors.toMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class FavouriteServiceImpl implements FavouriteService {

    private final FavouriteItemRepository favouriteItemRepository;
    private final ItemRepository itemRepository;
    private final TimeService timeService;

    private final DateTimeFormatter favouriteIdFormatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmssSSS");

    @Override
    public void changeIsFavourite(String itemId, UserData user, boolean isFavourite) {
        log.info("Changing isFavourite of item {} of user {} to {}", itemId, user.getId(), isFavourite);

        if (isFavourite) {
            ZonedDateTime now = timeService.now();

            FavouriteItemEntity favourite = new FavouriteItemEntity();
            favourite.setId(favouriteIdFormatter.format(now));
            favourite.setUserId(user.getId());
            favourite.setItemId(itemId);
            favourite.setCreatedAt(now.toInstant());

            favouriteItemRepository.upsert(favourite);
        } else {
            favouriteItemRepository.deleteByItemIdAndUserId(itemId, user.getId());
        }
    }

    @Override
    public List<ItemEntity> getAll(UserData user) {
        log.info("Getting favourite items as user {}", user.getId());

        return itemRepository.findFavouritesOf(user.getId());
    }

    @Override
    public boolean checkFavourite(UserData user, ItemEntity item) {
        log.info("Checking favourite item {} as user {}", item.getId(), user.getId());

        return favouriteItemRepository.existsByUserIdAndItemIds(user.getId(), List.of(item.getId())).size() == 1;
    }

    @Override
    public Map<String, Boolean> checkFavourite(UserData user, List<ItemEntity> items) {
        log.info("Checking favourite items {} as user {}", items.size(), user.getId());

        List<String> itemIds = items.stream()
                .map(ItemEntity::getId)
                .toList();

        List<String> existent = favouriteItemRepository.existsByUserIdAndItemIds(user.getId(), itemIds);

        return items.stream()
                .collect(toMap(ItemEntity::getId, i -> existent.contains(i.getId())));
    }
}
