package ru.andryss.trousseau.repository;

import java.sql.Timestamp;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.andryss.trousseau.model.FavouriteItemEntity;

@Repository
@RequiredArgsConstructor
public class FavouriteItemRepositoryImpl implements FavouriteItemRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    @Override
    public void upsert(FavouriteItemEntity favourite) {
        jdbcTemplate.update("""
                insert into favourites(id, item_id, created_at)
                    values(:id, :itemId, :createdAt)
                on conflict on constraint favourites_unique do nothing
        """, new MapSqlParameterSource()
                .addValue("id", favourite.getId())
                .addValue("itemId", favourite.getItemId())
                .addValue("createdAt", Timestamp.from(favourite.getCreatedAt())));
    }
}
