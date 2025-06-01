package ru.andryss.trousseau.repository;

import java.util.List;
import java.util.Optional;

import ru.andryss.trousseau.model.UserEntity;

/**
 * Репозиторий для работы с сущностями пользователей
 */
public interface UserRepository {
    /**
     * Сохранение нового пользователя
     */
    void save(UserEntity user);

    /**
     * Добавление ролей пользователю
     */
    void saveUserRoles(String userId, List<String> roleIds);

    /**
     * Поиск пользователя по идентификатору
     */
    Optional<UserEntity> findById(String id);

    /**
     * Получение пользователя по имени пользователя
     */
    Optional<UserEntity> findByUsername(String username);

    /**
     * Получение ролей пользователя
     */
    List<String> findUserRoles(String userId);

    /**
     * Получение всех привилегий по ролям
     */
    List<String> findRolesPrivileges(List<String> roles);
}
