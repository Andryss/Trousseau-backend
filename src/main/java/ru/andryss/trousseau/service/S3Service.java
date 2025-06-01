package ru.andryss.trousseau.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * Сервис для работы с S3-хранилищем
 */
public interface S3Service {
    /**
     * Записать содержимое файла по заданному пути
     */
    void put(String path, MultipartFile file);

    /**
     * Получить URL на загрузку файла по заданному пути
     */
    String presignedUrl(String path);
}
