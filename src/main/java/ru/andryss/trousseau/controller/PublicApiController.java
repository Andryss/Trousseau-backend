package ru.andryss.trousseau.controller;

import java.util.List;

import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RestController;
import ru.andryss.trousseau.controller.validator.SearchInfoValidator;
import ru.andryss.trousseau.exception.Errors;
import ru.andryss.trousseau.generated.api.PublicApi;
import ru.andryss.trousseau.generated.model.CategoryNode;
import ru.andryss.trousseau.generated.model.CategoryTree;
import ru.andryss.trousseau.generated.model.ChangeFavouriteRequest;
import ru.andryss.trousseau.generated.model.ChangeStatusRequest;
import ru.andryss.trousseau.generated.model.ItemDto;
import ru.andryss.trousseau.generated.model.ItemListResponse;
import ru.andryss.trousseau.generated.model.SearchInfo;
import ru.andryss.trousseau.model.ItemEntity;
import ru.andryss.trousseau.model.ItemStatus;
import ru.andryss.trousseau.service.CategoryService;
import ru.andryss.trousseau.service.FavouriteService;
import ru.andryss.trousseau.service.ItemService;
import ru.andryss.trousseau.service.MediaService;
import ru.andryss.trousseau.service.UserService;

@RestController
public class PublicApiController extends ItemApiController implements PublicApi {

    private final ItemService itemService;
    private final FavouriteService favouriteService;
    private final CategoryService categoryService;
    private final SearchInfoValidator searchInfoValidator;

    public PublicApiController(
            ItemService itemService,
            MediaService mediaService,
            FavouriteService favouriteService,
            CategoryService categoryService,
            SearchInfoValidator searchInfoValidator,
            UserService userService
    ) {
        super(mediaService, favouriteService, categoryService, userService);
        this.itemService = itemService;
        this.favouriteService = favouriteService;
        this.categoryService = categoryService;
        this.searchInfoValidator = searchInfoValidator;
    }

    @Override
    public void changeFavourite(String itemId, ChangeFavouriteRequest request) {
        favouriteService.changeIsFavourite(itemId, getUser(), request.getIsFavourite());
    }

    @Override
    public void changeItemStatus(String itemId, ChangeStatusRequest request) {
        itemService.changePublicItemStatus(itemId, getUser(), ItemStatus.fromOpenApi(request.getStatus()));
    }

    @Override
    public ItemListResponse getBookedItems() {
        List<ItemEntity> items = itemService.getBooked(getUser());

        List<ItemDto> dtoList = mapToDto(items);

        return new ItemListResponse()
                .items(dtoList);
    }

    @Override
    public CategoryTree getCategoryTree() {
        CategoryNode root = categoryService.getCategoryTree();

        return new CategoryTree().root(root);
    }

    @Override
    public ItemListResponse getFavourites() {
        List<ItemEntity> items = favouriteService.getAll(getUser());

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
        BindingResult bindingResult = new BeanPropertyBindingResult(searchInfo, SearchInfo.class.getSimpleName());
        searchInfoValidator.validate(searchInfo, bindingResult);
        if (bindingResult.hasErrors()) {
            throw Errors.validationErrors(bindingResult);
        }

        List<ItemEntity> items = itemService.searchItems(searchInfo);

        List<ItemDto> dtoList = mapToDto(items);

        return new ItemListResponse()
                .items(dtoList);
    }
}
