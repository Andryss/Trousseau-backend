package ru.andryss.trousseau.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

public interface MediaService {
    String saveMedia(MultipartFile media);
    List<String> toUrls(List<String> ids);
}
