package ru.andryss.trousseau.service;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

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

    private static final String FALLBACK_URL = "error_fetch";

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

    @Override
    public List<String> toUrls(List<String> ids) {
        ArrayList<String> urls = new ArrayList<>(ids.size());
        for (String id : ids) {
            try {
                urls.add(s3Service.presignedUrl(id));
            } catch (Exception e) {
                log.error("Error while generating urls to media", e);
                urls.add(FALLBACK_URL);
            }
        }
        return urls;
    }
}
