package ru.andryss.trousseau.repository;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.andryss.trousseau.model.NotificationSettingsEntity;

@Repository
@RequiredArgsConstructor
public class NotificationSettingsRepositoryImpl implements NotificationSettingsRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    @Override
    public void upsert(NotificationSettingsEntity entity) {
        jdbcTemplate.update("""
                        insert into notifications_settings(user_id, token, updated_at, created_at)
                            values (:userId, :token, :updatedAt, :createdAt)
                        on conflict (user_id) do update set token = excluded.token, updated_at = excluded.updated_at
                """, new MapSqlParameterSource()
                        .addValue("userId", entity.getUserId())
                        .addValue("token", entity.getToken())
                        .addValue("updatedAt", Timestamp.from(entity.getUpdatedAt()))
                        .addValue("createdAt", Timestamp.from(entity.getCreatedAt())));
    }

    @Override
    public Optional<String> findTokenByUserId(String userId) {
        List<String> settings = jdbcTemplate.queryForList("""
                        select token from notifications_settings where user_id = :userId
                """, new MapSqlParameterSource()
                        .addValue("userId", userId), String.class);

        if (settings.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(settings.get(0));
    }
}
