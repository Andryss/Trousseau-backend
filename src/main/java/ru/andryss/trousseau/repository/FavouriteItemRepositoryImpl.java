package ru.andryss.trousseau.repository;

import java.sql.Timestamp;
import java.util.List;

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
                insert into favourites(id, item_id, user_id, created_at)
                    values(:id, :itemId, :userId, :createdAt)
                on conflict on constraint favourites_unique do nothing
        """, new MapSqlParameterSource()
                .addValue("id", favourite.getId())
                .addValue("itemId", favourite.getItemId())
                .addValue("userId", favourite.getUserId())
                .addValue("createdAt", Timestamp.from(favourite.getCreatedAt())));
    }

    @Override
    public void deleteByItemIdAndUserId(String itemId, String userId) {
        jdbcTemplate.update("""
                delete from favourites where item_id = :itemId and user_id = :userId
        """, new MapSqlParameterSource()
                .addValue("itemId", itemId)
                .addValue("userId", userId));
    }

    @Override
    public List<String> existsByUserIdAndItemIds(String userId, List<String> itemIds) {
        if (itemIds.isEmpty()) {
            return List.of();
        }
        return jdbcTemplate.queryForList("""
                select item_id from favourites where user_id = :userId and item_id in (:itemIds)
        """, new MapSqlParameterSource()
                .addValue("userId", userId)
                .addValue("itemIds", itemIds), String.class);
    }
}
