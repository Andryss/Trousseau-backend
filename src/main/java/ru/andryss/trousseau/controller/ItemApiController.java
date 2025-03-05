package ru.andryss.trousseau.controller;

import java.util.List;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import ru.andryss.trousseau.generated.api.ItemApi;
import ru.andryss.trousseau.generated.model.GetItemsResponse;
import ru.andryss.trousseau.generated.model.ItemDto;
import ru.andryss.trousseau.generated.model.ItemInfoRequest;
import ru.andryss.trousseau.model.ItemEntity;
import ru.andryss.trousseau.service.ItemService;
import ru.andryss.trousseau.service.MediaService;

@RestController
@AllArgsConstructor
public class ItemApiController implements ItemApi {

    private final ItemService itemService;
    private final MediaService mediaService;

    @Override
    public ItemDto createItem(ItemInfoRequest request) {
        ItemEntity item = itemService.createItem(request);

        return mapToDto(item);
    }

    @Override
    public GetItemsResponse getItems() {
        List<ItemEntity> items = itemService.getItems();

        List<ItemDto> dtos = items.stream()
                .map(this::mapToDto)
                .toList();

        return new GetItemsResponse()
                .items(dtos);
    }

    @Override
    public ItemDto updateItem(String itemId, ItemInfoRequest request) {
        ItemEntity item = itemService.updateItem(itemId, request);

        return mapToDto(item);
    }

    private ItemDto mapToDto(ItemEntity entity) {
        return new ItemDto()
                .id(entity.getId())
                .title(entity.getTitle())
                .media(mediaService.toUrls(entity.getMediaIds()))
                .description(entity.getDescription())
                .status(entity.getStatus().toOpenApi());
    }
}
