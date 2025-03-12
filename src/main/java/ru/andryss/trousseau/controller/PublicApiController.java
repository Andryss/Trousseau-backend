package ru.andryss.trousseau.controller;

import java.util.List;

import org.springframework.web.bind.annotation.RestController;
import ru.andryss.trousseau.generated.api.PublicApi;
import ru.andryss.trousseau.generated.model.PublicItemDto;
import ru.andryss.trousseau.generated.model.SearchInfo;
import ru.andryss.trousseau.generated.model.SearchItemsResponse;
import ru.andryss.trousseau.model.ItemEntity;
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
    public PublicItemDto getItem(String itemId) {
        ItemEntity item = itemService.getPublicItem(itemId);

        return mapToDto(item);
    }

    @Override
    public SearchItemsResponse searchItems(SearchInfo searchInfo) {
        List<ItemEntity> items = itemService.searchItems(searchInfo);

        List<PublicItemDto> dtoList = items.stream()
                .map(this::mapToDto)
                .toList();

        return new SearchItemsResponse()
                .items(dtoList);
    }

    private PublicItemDto mapToDto(ItemEntity entity) {
        return new PublicItemDto()
                .id(entity.getId())
                .title(entity.getTitle())
                .media(mapToDtos(entity.getMediaIds()))
                .description(entity.getDescription());
    }
}
