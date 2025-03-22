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
    public void changeIsFavourite(String itemId, boolean isFavourite) {
        log.info("Changing isFavourite of item {} to {}", itemId, isFavourite);

        if (isFavourite) {
            ZonedDateTime now = timeService.now();

            FavouriteItemEntity favourite = new FavouriteItemEntity();
            favourite.setId(favouriteIdFormatter.format(now));
            favourite.setItemId(itemId);
            favourite.setCreatedAt(now.toInstant());

            favouriteItemRepository.upsert(favourite);
        } else {
            favouriteItemRepository.deleteByItemId(itemId);
        }
    }

    @Override
    public List<ItemEntity> getAll() {
        log.info("Getting favourite items");

        return itemRepository.findAllFavourites();
    }

    @Override
    public boolean checkFavourite(ItemEntity item) {
        log.info("Checking favourite item {}", item.getId());

        return favouriteItemRepository.existsByItemId(List.of(item.getId())).size() == 1;
    }

    @Override
    public Map<String, Boolean> checkFavourite(List<ItemEntity> items) {
        log.info("Checking favourite items {}", items.size());

        List<String> itemIds = items.stream()
                .map(ItemEntity::getId)
                .toList();

        List<String> existent = favouriteItemRepository.existsByItemId(itemIds);

        return items.stream()
                .collect(toMap(ItemEntity::getId, i -> existent.contains(i.getId())));
    }
}
