package ru.andryss.trousseau.service;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.andryss.trousseau.exception.Errors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MediaServiceImpl implements MediaService {

    private final TimeService timeService;
    private final S3Service s3Service;

    private final DateTimeFormatter mediaIdFormatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmssSSS");

    @Override
    public String saveMedia(MultipartFile media) {
        ZonedDateTime now = timeService.now();
        String id = mediaIdFormatter.format(now);
        try {
            s3Service.put(id, media);
        } catch (Exception e) {
            log.error("Error while saving media to S3", e);
            throw Errors.mediaSaveError();
        }
        return id;
    }
}
