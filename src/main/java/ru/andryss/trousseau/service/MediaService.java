package ru.andryss.trousseau.service;

import org.springframework.web.multipart.MultipartFile;

public interface MediaService {
    String saveMedia(MultipartFile media);
}
