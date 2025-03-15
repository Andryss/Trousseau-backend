package ru.andryss.trousseau.controller;

import java.util.ArrayList;
import java.util.List;

import lombok.RequiredArgsConstructor;
import ru.andryss.trousseau.generated.model.ItemDto;
import ru.andryss.trousseau.generated.model.ItemMediaDto;
import ru.andryss.trousseau.model.ItemEntity;
import ru.andryss.trousseau.service.MediaService;

@RequiredArgsConstructor
public abstract class CommonApiController {

    private final MediaService mediaService;


    protected List<ItemDto> mapToDto(List<ItemEntity> items) {
        return items.stream()
                .map(this::mapToDto)
                .toList();
    }

    protected ItemDto mapToDto(ItemEntity entity) {
        return new ItemDto()
                .id(entity.getId())
                .title(entity.getTitle())
                .media(mapMediaIdToDto(entity.getMediaIds()))
                .description(entity.getDescription())
                .status(entity.getStatus().toOpenApi());
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
}
