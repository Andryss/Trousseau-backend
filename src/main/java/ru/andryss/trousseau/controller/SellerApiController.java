package ru.andryss.trousseau.controller;

import java.util.List;

import org.springframework.web.bind.annotation.RestController;
import ru.andryss.trousseau.generated.api.SellerApi;
import ru.andryss.trousseau.generated.model.ChangeStatusRequest;
import ru.andryss.trousseau.generated.model.ItemDto;
import ru.andryss.trousseau.generated.model.ItemInfoRequest;
import ru.andryss.trousseau.generated.model.ItemListResponse;
import ru.andryss.trousseau.model.ItemEntity;
import ru.andryss.trousseau.model.ItemStatus;
import ru.andryss.trousseau.service.CategoryService;
import ru.andryss.trousseau.service.FavouriteService;
import ru.andryss.trousseau.service.ItemService;
import ru.andryss.trousseau.service.MediaService;

@RestController
public class SellerApiController extends CommonApiController implements SellerApi {

    private final ItemService itemService;

    public SellerApiController(ItemService itemService, MediaService mediaService, FavouriteService favouriteService,
                               CategoryService categoryService) {
        super(mediaService, favouriteService, categoryService);
        this.itemService = itemService;
    }

    @Override
    public void changeSellerItemStatus(String itemId, ChangeStatusRequest request) {
        itemService.changeSellerItemStatus(itemId, ItemStatus.fromOpenApi(request.getStatus()));
    }

    @Override
    public ItemDto createSellerItem(ItemInfoRequest request) {
        ItemEntity item = itemService.createItem(request);

        return mapToDto(item);
    }

    @Override
    public ItemDto getSellerItem(String itemId) {
        ItemEntity item = itemService.getItem(itemId);

        return mapToDto(item);
    }

    @Override
    public ItemListResponse getSellerItems() {
        List<ItemEntity> items = itemService.getItems();

        List<ItemDto> dtoList = mapToDto(items);

        return new ItemListResponse()
                .items(dtoList);
    }

    @Override
    public ItemDto updateSellerItem(String itemId, ItemInfoRequest request) {
        ItemEntity item = itemService.updateItem(itemId, request);

        return mapToDto(item);
    }
}
