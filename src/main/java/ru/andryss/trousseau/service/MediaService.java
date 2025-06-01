package ru.andryss.trousseau.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

/**
 * Сервис для работы с медиа-данными
 */
public interface MediaService {
    /**
     * Сохранить медиа-файл
     */
    String saveMedia(MultipartFile media);

    /**
     * Сформировать URL для заданных медиа-файлов
     */
    List<String> toUrls(List<String> ids);
}
