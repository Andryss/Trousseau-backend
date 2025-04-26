package ru.andryss.trousseau.controller;

import java.util.List;

import org.springframework.web.bind.annotation.RestController;
import ru.andryss.trousseau.generated.api.SellerApi;
import ru.andryss.trousseau.generated.model.BookingDto;
import ru.andryss.trousseau.generated.model.ChangeStatusRequest;
import ru.andryss.trousseau.generated.model.ItemDto;
import ru.andryss.trousseau.generated.model.ItemInfoRequest;
import ru.andryss.trousseau.generated.model.ItemListResponse;
import ru.andryss.trousseau.model.BookingEntity;
import ru.andryss.trousseau.model.ItemEntity;
import ru.andryss.trousseau.model.ItemStatus;
import ru.andryss.trousseau.service.BookingService;
import ru.andryss.trousseau.service.CategoryService;
import ru.andryss.trousseau.service.FavouriteService;
import ru.andryss.trousseau.service.ItemService;
import ru.andryss.trousseau.service.MediaService;
import ru.andryss.trousseau.service.UserService;

@RestController
public class SellerApiController extends ItemApiController implements SellerApi {

    private final ItemService itemService;
    private final BookingService bookingService;

    public SellerApiController(
            ItemService itemService,
            MediaService mediaService,
            FavouriteService favouriteService,
            CategoryService categoryService,
            UserService userService,
            BookingService bookingService) {
        super(mediaService, favouriteService, categoryService, userService);
        this.itemService = itemService;
        this.bookingService = bookingService;
    }

    @Override
    public void changeSellerItemStatus(String itemId, ChangeStatusRequest request) {
        itemService.changeSellerItemStatus(itemId, getUser(), ItemStatus.fromOpenApi(request.getStatus()));
    }

    @Override
    public ItemDto createSellerItem(ItemInfoRequest request) {
        ItemEntity item = itemService.createItem(getUser(), request);

        return mapItemDto(item);
    }

    @Override
    public BookingDto getItemBookingInfo(String itemId) {
        BookingEntity booking = bookingService.findByItem(itemId, getUser());

        return new BookingDto()
                .author(mapAuthorDto(booking.getUserId()))
                .bookedAt(toOffsetDateTime(booking.getBookedAt()));
    }

    @Override
    public ItemDto getSellerItem(String itemId) {
        ItemEntity item = itemService.getItem(itemId, getUser());

        return mapItemDto(item);
    }

    @Override
    public ItemListResponse getSellerItems() {
        List<ItemEntity> items = itemService.getItems(getUser());

        List<ItemDto> dtoList = mapItemDtoList(items);

        return new ItemListResponse()
                .items(dtoList);
    }

    @Override
    public ItemDto updateSellerItem(String itemId, ItemInfoRequest request) {
        ItemEntity item = itemService.updateItem(itemId, getUser(), request);

        return mapItemDto(item);
    }
}
