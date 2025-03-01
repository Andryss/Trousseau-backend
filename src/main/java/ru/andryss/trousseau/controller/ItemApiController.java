package ru.andryss.trousseau.controller;

import java.util.List;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import ru.andryss.trousseau.generated.api.ItemApi;
import ru.andryss.trousseau.generated.model.GetItemsResponse;
import ru.andryss.trousseau.generated.model.ItemDto;
import ru.andryss.trousseau.generated.model.ItemInfoRequest;
import ru.andryss.trousseau.generated.model.UpdateItemResponse;
import ru.andryss.trousseau.model.ItemEntity;
import ru.andryss.trousseau.service.ItemService;
import ru.andryss.trousseau.service.MediaService;

@RestController
@AllArgsConstructor
public class ItemApiController implements ItemApi {

    private final ItemService itemService;
    private final MediaService mediaService;

    @Override
    public UpdateItemResponse createItem(ItemInfoRequest request) {
        ItemEntity item = itemService.createItem(request);

        return new UpdateItemResponse()
                .id(item.getId())
                .status(item.getStatus().toOpenApi());
    }

    @Override
    public GetItemsResponse getItems() {
        List<ItemEntity> items = itemService.getItems();

        List<ItemDto> dtos = items.stream()
                .map(entity -> {
                    ItemDto dto = new ItemDto();
                    dto.setId(entity.getId());
                    dto.setTitle(entity.getTitle());
                    dto.setMedia(mediaService.toUrls(entity.getMediaIds()));
                    dto.setDescription(entity.getDescription());
                    dto.setStatus(entity.getStatus().toOpenApi());
                    return dto;
                })
                .toList();

        return new GetItemsResponse()
                .items(dtos);
    }

    @Override
    public UpdateItemResponse updateItem(String itemId, ItemInfoRequest request) {
        ItemEntity item = itemService.updateItem(itemId, request);

        return new UpdateItemResponse()
                .id(item.getId())
                .status(item.getStatus().toOpenApi());
    }
}
