package ru.andryss.trousseau.controller;

import java.util.List;

import org.springframework.web.bind.annotation.RestController;
import ru.andryss.trousseau.generated.api.SellerApi;
import ru.andryss.trousseau.generated.model.ChangeStatusRequest;
import ru.andryss.trousseau.generated.model.GetItemsResponse;
import ru.andryss.trousseau.generated.model.ItemDto;
import ru.andryss.trousseau.generated.model.ItemInfoRequest;
import ru.andryss.trousseau.model.ItemEntity;
import ru.andryss.trousseau.model.ItemStatus;
import ru.andryss.trousseau.service.ItemService;
import ru.andryss.trousseau.service.MediaService;

@RestController
public class SellerApiController extends CommonApiController implements SellerApi {

    private final ItemService itemService;

    public SellerApiController(ItemService itemService, MediaService mediaService) {
        super(mediaService);
        this.itemService = itemService;
    }

    @Override
    public void changeSellerItemStatus(String itemId, ChangeStatusRequest request) {
        itemService.changeItemStatus(itemId, ItemStatus.fromOpenApi(request.getStatus()));
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
    public GetItemsResponse getSellerItems() {
        List<ItemEntity> items = itemService.getItems();

        List<ItemDto> dtos = items.stream()
                .map(this::mapToDto)
                .toList();

        return new GetItemsResponse()
                .items(dtos);
    }

    @Override
    public ItemDto updateSellerItem(String itemId, ItemInfoRequest request) {
        ItemEntity item = itemService.updateItem(itemId, request);

        return mapToDto(item);
    }

    private ItemDto mapToDto(ItemEntity entity) {
        return new ItemDto()
                .id(entity.getId())
                .title(entity.getTitle())
                .media(mapToDtos(entity.getMediaIds()))
                .description(entity.getDescription())
                .status(entity.getStatus().toOpenApi());
    }
}
