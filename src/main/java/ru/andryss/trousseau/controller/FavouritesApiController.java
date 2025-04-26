package ru.andryss.trousseau.controller;

import java.util.List;

import org.springframework.web.bind.annotation.RestController;
import ru.andryss.trousseau.generated.api.FavouritesApi;
import ru.andryss.trousseau.generated.model.ChangeFavouriteRequest;
import ru.andryss.trousseau.generated.model.ItemDto;
import ru.andryss.trousseau.generated.model.ItemListResponse;
import ru.andryss.trousseau.model.ItemEntity;
import ru.andryss.trousseau.service.CategoryService;
import ru.andryss.trousseau.service.FavouriteService;
import ru.andryss.trousseau.service.MediaService;
import ru.andryss.trousseau.service.UserService;

@RestController
public class FavouritesApiController extends ItemApiController implements FavouritesApi {

    private final FavouriteService favouriteService;

    public FavouritesApiController(
            MediaService mediaService,
            FavouriteService favouriteService,
            CategoryService categoryService,
            UserService userService) {
        super(mediaService, favouriteService, categoryService, userService);
        this.favouriteService = favouriteService;
    }

    @Override
    public void changeFavourite(String itemId, ChangeFavouriteRequest request) {
        favouriteService.changeIsFavourite(itemId, getUser(), request.getIsFavourite());
    }

    @Override
    public ItemListResponse getFavourites() {
        List<ItemEntity> items = favouriteService.getAll(getUser());

        List<ItemDto> dtoList = mapToDto(items);

        return new ItemListResponse()
                .items(dtoList);
    }
}
