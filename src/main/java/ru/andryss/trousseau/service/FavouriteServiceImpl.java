package ru.andryss.trousseau.service;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.andryss.trousseau.model.FavouriteItemEntity;
import ru.andryss.trousseau.model.ItemEntity;
import ru.andryss.trousseau.repository.FavouriteItemRepository;
import ru.andryss.trousseau.repository.ItemRepository;

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
}
