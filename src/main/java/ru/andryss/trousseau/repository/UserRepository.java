package ru.andryss.trousseau.repository;

import java.util.List;
import java.util.Optional;

import ru.andryss.trousseau.model.UserEntity;

public interface UserRepository {
    void save(UserEntity user);
    void saveUserRoles(String userId, List<String> roleIds);
    Optional<UserEntity> findByUsername(String username);
    List<String> findUserRoles(String userId);
    List<String> findRolesPrivileges(List<String> roles);
}
