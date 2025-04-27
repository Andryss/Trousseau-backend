package ru.andryss.trousseau.service;

import java.util.Optional;

import ru.andryss.trousseau.model.UserEntity;

public interface UserService {
    Optional<UserEntity> findById(String id);
    UserEntity findByIdOrThrow(String id);
}
