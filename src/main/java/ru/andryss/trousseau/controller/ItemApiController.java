package ru.andryss.trousseau.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import ru.andryss.trousseau.generated.api.ItemApi;
import ru.andryss.trousseau.generated.model.ItemInfoRequest;
import ru.andryss.trousseau.service.ItemService;

@RestController
@AllArgsConstructor
public class ItemApiController implements ItemApi {

    private final ItemService itemService;

    @Override
    public void createItem(ItemInfoRequest request) {
        itemService.createItem(request.getTitle(), request.getMedia(), request.getDescription());
    }

    @Override
    public void updateItem(String itemId, ItemInfoRequest request) {
        // TODO
    }
}
