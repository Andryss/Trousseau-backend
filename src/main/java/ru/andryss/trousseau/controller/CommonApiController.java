package ru.andryss.trousseau.controller;

import java.util.ArrayList;
import java.util.List;

import lombok.RequiredArgsConstructor;
import ru.andryss.trousseau.generated.model.ItemMediaDto;
import ru.andryss.trousseau.service.MediaService;

@RequiredArgsConstructor
public abstract class CommonApiController {

    private final MediaService mediaService;


    protected List<ItemMediaDto> mapToDtos(List<String> ids) {
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
