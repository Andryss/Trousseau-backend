package ru.andryss.trousseau.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import ru.andryss.trousseau.generated.api.MediaApi;
import ru.andryss.trousseau.generated.model.UploadMediaResponse;
import ru.andryss.trousseau.service.MediaService;

@RestController
@AllArgsConstructor
public class MediaApiController implements MediaApi {

    private final MediaService mediaService;

    @Override
    public UploadMediaResponse uploadMedia(MultipartFile data) {
        String id = mediaService.saveMedia(data);
        return new UploadMediaResponse().id(id);
    }
}
