package ru.andryss.trousseau.controller;

import java.util.List;

import org.springframework.web.bind.annotation.RestController;
import ru.andryss.trousseau.generated.api.PublicApi;
import ru.andryss.trousseau.generated.model.ChangeStatusRequest;
import ru.andryss.trousseau.generated.model.ItemListResponse;
import ru.andryss.trousseau.generated.model.PublicItemDto;
import ru.andryss.trousseau.generated.model.SearchInfo;
import ru.andryss.trousseau.model.ItemEntity;
import ru.andryss.trousseau.model.ItemStatus;
import ru.andryss.trousseau.service.ItemService;
import ru.andryss.trousseau.service.MediaService;

@RestController
public class PublicApiController extends CommonApiController implements PublicApi {

    private final ItemService itemService;

    public PublicApiController(ItemService itemService, MediaService mediaService) {
        super(mediaService);
        this.itemService = itemService;
    }

    @Override
    public void changeItemStatus(String itemId, ChangeStatusRequest request) {
        itemService.changeItemStatus(itemId, ItemStatus.fromOpenApi(request.getStatus()));
    }

    @Override
    public ItemListResponse getBookedItems() {
        List<ItemEntity> items = itemService.getBooked();

        List<PublicItemDto> dtoList = mapToDto(items);

        return new ItemListResponse()
                .items(dtoList);
    }

    @Override
    public PublicItemDto getItem(String itemId) {
        ItemEntity item = itemService.getPublicItem(itemId);

        return mapToDto(item);
    }

    @Override
    public ItemListResponse searchItems(SearchInfo searchInfo) {
        List<ItemEntity> items = itemService.searchItems(searchInfo);

        List<PublicItemDto> dtoList = mapToDto(items);

        return new ItemListResponse()
                .items(dtoList);
    }

    private List<PublicItemDto> mapToDto(List<ItemEntity> items) {
        return items.stream()
                .map(this::mapToDto)
                .toList();
    }

    private PublicItemDto mapToDto(ItemEntity entity) {
        return new PublicItemDto()
                .id(entity.getId())
                .title(entity.getTitle())
                .media(mapToDtos(entity.getMediaIds()))
                .description(entity.getDescription());
    }
}
