package ru.andryss.trousseau.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import ru.andryss.trousseau.generated.api.ItemApi;
import ru.andryss.trousseau.generated.model.ItemInfoRequest;
import ru.andryss.trousseau.generated.model.UpdateItemResponse;
import ru.andryss.trousseau.model.ItemEntity;
import ru.andryss.trousseau.service.ItemService;

@RestController
@AllArgsConstructor
public class ItemApiController implements ItemApi {

    private final ItemService itemService;

    @Override
    public UpdateItemResponse createItem(ItemInfoRequest request) {
        ItemEntity item = itemService.createItem(request);

        return new UpdateItemResponse()
                .id(item.getId())
                .status(item.getStatus().toOpenApi());
    }

    @Override
    public UpdateItemResponse updateItem(String itemId, ItemInfoRequest request) {
        ItemEntity item = itemService.updateItem(itemId, request);

        return new UpdateItemResponse()
                .id(item.getId())
                .status(item.getStatus().toOpenApi());
    }
}
