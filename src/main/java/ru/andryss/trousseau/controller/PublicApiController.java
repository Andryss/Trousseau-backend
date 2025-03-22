package ru.andryss.trousseau.controller;

import java.util.List;

import org.springframework.web.bind.annotation.RestController;
import ru.andryss.trousseau.generated.api.PublicApi;
import ru.andryss.trousseau.generated.model.ChangeFavouriteRequest;
import ru.andryss.trousseau.generated.model.ChangeStatusRequest;
import ru.andryss.trousseau.generated.model.ItemDto;
import ru.andryss.trousseau.generated.model.ItemListResponse;
import ru.andryss.trousseau.generated.model.SearchInfo;
import ru.andryss.trousseau.model.ItemEntity;
import ru.andryss.trousseau.model.ItemStatus;
import ru.andryss.trousseau.service.FavouriteService;
import ru.andryss.trousseau.service.ItemService;
import ru.andryss.trousseau.service.MediaService;

@RestController
public class PublicApiController extends CommonApiController implements PublicApi {

    private final ItemService itemService;
    private final FavouriteService favouriteService;

    public PublicApiController(ItemService itemService, MediaService mediaService, FavouriteService favouriteService) {
        super(mediaService);
        this.itemService = itemService;
        this.favouriteService = favouriteService;
    }

    @Override
    public void changeFavourite(String itemId, ChangeFavouriteRequest request) {
        favouriteService.changeIsFavourite(itemId, request.getIsFavourite());
    }

    @Override
    public void changeItemStatus(String itemId, ChangeStatusRequest request) {
        itemService.changePublicItemStatus(itemId, ItemStatus.fromOpenApi(request.getStatus()));
    }

    @Override
    public ItemListResponse getBookedItems() {
        List<ItemEntity> items = itemService.getBooked();

        List<ItemDto> dtoList = mapToDto(items);

        return new ItemListResponse()
                .items(dtoList);
    }

    @Override
    public ItemListResponse getFavourites() {
        List<ItemEntity> items = favouriteService.getAll();

        List<ItemDto> dtoList = mapToDto(items);

        return new ItemListResponse()
                .items(dtoList);
    }

    @Override
    public ItemDto getItem(String itemId) {
        ItemEntity item = itemService.getPublicItem(itemId);

        return mapToDto(item);
    }

    @Override
    public ItemListResponse searchItems(SearchInfo searchInfo) {
        List<ItemEntity> items = itemService.searchItems(searchInfo);

        List<ItemDto> dtoList = mapToDto(items);

        return new ItemListResponse()
                .items(dtoList);
    }
}
