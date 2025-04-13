package ru.andryss.trousseau.repository;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.andryss.trousseau.model.UserEntity;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    private final RowMapper<UserEntity> rowMapper = (rs, rowNum) -> {
        UserEntity user = new UserEntity();
        user.setId(rs.getString("id"));
        user.setUsername(rs.getString("username"));
        user.setPasswordHash(rs.getString("password_hash"));
        user.setCreatedAt(rs.getTimestamp("created_at").toInstant());
        return user;
    };

    @Override
    public void save(UserEntity user) {
        jdbcTemplate.update("""
                insert into users(id, username, password_hash, created_at)
                    values (:id, :username, :passwordHash, :createdAt)
        """, new MapSqlParameterSource()
                .addValue("id", user.getId())
                .addValue("username", user.getUsername())
                .addValue("passwordHash", user.getPasswordHash())
                .addValue("createdAt", Timestamp.from(user.getCreatedAt())));
    }

    @Override
    public void saveUserRoles(String userId, List<String> roleIds) {
        jdbcTemplate.update("""
                insert into user_roles(user_id, role_id) select :userId, unnest(array[:roleIds])
        """, new MapSqlParameterSource()
                .addValue("userId", userId)
                .addValue("roleIds", roleIds));
    }

    @Override
    public Optional<UserEntity> findByUsername(String username) {
        List<UserEntity> result = jdbcTemplate.query("""
                select * from users
                where username = :username
        """, new MapSqlParameterSource()
                .addValue("username", username), rowMapper);

        if (result.isEmpty()) {
            return Optional.empty();
        }

        UserEntity user = result.get(0);
        return Optional.of(user);
    }

    @Override
    public List<String> findUserRoles(String userId) {
        return jdbcTemplate.queryForList("""
                select r.role
                from users u
                    join user_roles ur on ur.user_id = u.id
                    join roles r on ur.role_id = r.id
                where u.id = :userId
        """, new MapSqlParameterSource()
                .addValue("userId", userId), String.class);
    }

    @Override
    public List<String> findRolesPrivileges(List<String> roles) {
        return jdbcTemplate.queryForList("""
                select p.privilege
                from roles r
                    join role_privileges rp on rp.role_id = r.id
                    join privileges p on rp.privilege_id = p.id
                where r.role in (:roles)
        """, new MapSqlParameterSource()
                .addValue("roles", roles), String.class);
    }
}
