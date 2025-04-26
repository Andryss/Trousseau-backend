package ru.andryss.trousseau.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import lombok.RequiredArgsConstructor;
import ru.andryss.trousseau.generated.model.AuthorDto;
import ru.andryss.trousseau.generated.model.CategoryDto;
import ru.andryss.trousseau.generated.model.ItemDto;
import ru.andryss.trousseau.generated.model.ItemMediaDto;
import ru.andryss.trousseau.model.ItemEntity;
import ru.andryss.trousseau.service.CategoryService;
import ru.andryss.trousseau.service.FavouriteService;
import ru.andryss.trousseau.service.MediaService;
import ru.andryss.trousseau.service.UserService;

@RequiredArgsConstructor
public abstract class ItemApiController extends BaseApiController {

    private final MediaService mediaService;
    private final FavouriteService favouriteService;
    private final CategoryService categoryService;
    private final UserService userService;


    protected List<ItemDto> mapItemDtoList(List<ItemEntity> items) {
        List<ItemDto> dtoList = items.stream()
                .map(entity -> mapItemDtoCommon(entity, false))
                .toList();

        Map<String, Boolean> favouritesMap = favouriteService.checkFavourite(items);
        for (ItemDto itemDto : dtoList) {
            itemDto.isFavourite(favouritesMap.getOrDefault(itemDto.getId(), false));
        }

        return dtoList;
    }

    protected ItemDto mapItemDto(ItemEntity entity) {
        return mapItemDtoCommon(entity, true);
    }

    protected ItemDto mapItemDtoCommon(ItemEntity entity, boolean enrichFavourite) {
        ItemDto dto = new ItemDto()
                .id(entity.getId())
                .author(mapAuthorDto(entity.getOwner()))
                .title(entity.getTitle())
                .media(mapMediaIdToDto(entity.getMediaIds()))
                .description(entity.getDescription())
                .category(mapCategoryDto(entity.getCategoryId()))
                .status(entity.getStatus().toOpenApi())
                .publishedAt(toOffsetDateTime(entity.getPublishedAt()));
        if (enrichFavourite) {
            dto.isFavourite(favouriteService.checkFavourite(entity));
        }
        return dto;
    }

    protected AuthorDto mapAuthorDto(String userId) {
        return userService.findById(userId)
                .map(user -> new AuthorDto()
                        .username(user.getUsername())
                        .contacts(user.getContacts())
                        .room(user.getRoom()))
                .orElse(null);
    }

    private List<ItemMediaDto> mapMediaIdToDto(List<String> ids) {
        List<String> urls = mediaService.toUrls(ids);
        List<ItemMediaDto> dtos = new ArrayList<>(ids.size());
        for (int i = 0; i < ids.size(); i++) {
            dtos.add(new ItemMediaDto()
                    .id(ids.get(i))
                    .href(urls.get(i)));
        }
        return dtos;
    }

    protected CategoryDto mapCategoryDto(String categoryId) {
        if (categoryId == null) {
            return null;
        }
        return new CategoryDto()
                .id(categoryId)
                .name(categoryService.getCategoryName(categoryId));
    }
}
