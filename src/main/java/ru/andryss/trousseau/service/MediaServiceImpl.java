package ru.andryss.trousseau.service;

import java.time.Instant;
import java.time.format.DateTimeFormatter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
public class MediaServiceImpl implements MediaService {

    private final TimeService timeService;
    private final S3Service s3Service;

    private final DateTimeFormatter mediaIdFormatter = DateTimeFormatter.ofPattern("yyyyMM_hhmmssSSS");

    @Override
    public String saveMedia(MultipartFile media) {
        Instant now = timeService.now();
        String id = mediaIdFormatter.format(now);
        s3Service.put(id, media);
        return id;
    }
}
