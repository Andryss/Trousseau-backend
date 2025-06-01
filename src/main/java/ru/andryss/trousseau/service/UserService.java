package ru.andryss.trousseau.service;

import java.util.Optional;

import ru.andryss.trousseau.model.UserEntity;

/**
 * Сервис для работы с пользователями
 */
public interface UserService {
    /**
     * Получить пользователя по идентификатору
     */
    Optional<UserEntity> findById(String id);

    /**
     * Получить пользователя по идентификатору, выбросить ошибку в случае отсутствия
     */
    UserEntity findByIdOrThrow(String id);
}
