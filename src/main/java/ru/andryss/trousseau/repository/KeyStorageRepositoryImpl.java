package ru.andryss.trousseau.repository;

import java.util.List;
import java.util.Optional;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class KeyStorageRepositoryImpl implements KeyStorageRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;


    @Override
    public void upsert(String key, String value) {
        jdbcTemplate.update("""
                        insert into key_storage(key, value)
                            values (:key, :value)
                        on conflict (key) do update set value = excluded.value
                """, new MapSqlParameterSource()
                        .addValue("key", key)
                        .addValue("value", value));
    }

    @Override
    public Optional<String> get(String key) {
        List<String> result = jdbcTemplate.queryForList("""
                        select value from key_storage
                        where key = :key
                """, new MapSqlParameterSource()
                        .addValue("key", key), String.class);

        if (result.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(result.get(0));
    }
}
