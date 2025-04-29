package ru.andryss.trousseau.repository;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.andryss.trousseau.model.ItemEntity;
import ru.andryss.trousseau.model.ItemStatus;
import ru.andryss.trousseau.service.ObjectMapperWrapper;

@Repository
@RequiredArgsConstructor
public class ItemRepositoryImpl implements ItemRepository, InitializingBean {

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final ObjectMapperWrapper objectMapper;

    private RowMapper<ItemEntity> rowMapper;

    @Override
    public void afterPropertiesSet() {
        rowMapper = (rs, rowNum) -> {
            ItemEntity item = new ItemEntity();
            item.setId(rs.getString("id"));
            item.setOwner(rs.getString("owner"));
            item.setTitle(rs.getString("title"));
            item.setMediaIds(objectMapper.readValue(rs.getString("media_ids")));
            item.setDescription(rs.getString("description"));
            item.setCategoryId(rs.getString("category_id"));
            item.setCost(rs.getLong("cost"));
            item.setStatus(ItemStatus.fromValue(rs.getString("status")));
            item.setPublishedAt(toInstant(rs.getTimestamp("published_at")));
            item.setCreatedAt(toInstant(rs.getTimestamp("created_at")));
            return item;
        };
    }

    @Override
    @Transactional
    public ItemEntity save(ItemEntity item) {
        jdbcTemplate.update("""
            insert into items(id, owner, title, media_ids, description, category_id,
                              cost, status, published_at, created_at)
                values(:id, :owner, :title, :mediaIds::jsonb, :description, :categoryId,
                       :cost, :status, :publishedAt, :createdAt)
        """, getParameterSource(item));

        return item;
    }

    @Override
    public ItemEntity update(ItemEntity item) {
        jdbcTemplate.update("""
            update items
                set title = :title, media_ids = :mediaIds::jsonb, description = :description,
                    category_id = :categoryId, cost = :cost, status = :status, published_at = :publishedAt
                where id = :id
        """, getParameterSource(item));

        return item;
    }

    @Override
    @Transactional
    public Optional<ItemEntity> findById(String id) {
        List<ItemEntity> result = jdbcTemplate.query("""
                select * from items where id = :id
        """, new MapSqlParameterSource()
                .addValue("id", id), rowMapper);

        if (result.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(result.get(0));
    }

    @Override
    public Optional<ItemEntity> findByIdAndOwner(String id, String owner) {
        List<ItemEntity> result = jdbcTemplate.query("""
                select * from items where id = :id and owner = :owner
        """, new MapSqlParameterSource()
                .addValue("id", id)
                .addValue("owner", owner), rowMapper);

        if (result.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(result.get(0));
    }

    @Override
    public List<ItemEntity> findAllByOwnerOrderByCreatedAtDesc(String owner) {
        return jdbcTemplate.query("""
                select * from items where owner = :owner order by created_at desc
        """, new MapSqlParameterSource()
                .addValue("owner", owner), rowMapper);
    }

    @Override
    public List<ItemEntity> findAllBookedBy(String userId) {
        return jdbcTemplate.query("""
                select i.id, i.owner, i.title, i.media_ids, i.description, i.category_id, i.cost,
                        i.status, i.published_at, i.created_at
                    from items i join bookings b on b.item_id = i.id
                where b.user_id = :userId
                order by b.booked_at desc
        """, new MapSqlParameterSource()
                .addValue("userId", userId), rowMapper);
    }

    @Override
    public List<ItemEntity> findFavouritesOf(String userId) {
        return jdbcTemplate.query("""
                select i.id, i.owner, i.title, i.media_ids, i.description, i.category_id, i.cost,
                        i.status, i.published_at, i.created_at
                    from items i join favourites f on i.id = f.item_id
                where f.user_id = :userId
                order by f.created_at desc
        """, new MapSqlParameterSource()
                .addValue("userId", userId), rowMapper);
    }

    @Override
    public List<ItemEntity> executeQuery(String query) {
        return jdbcTemplate.query(query, rowMapper);
    }

    private MapSqlParameterSource getParameterSource(ItemEntity item) {
        return new MapSqlParameterSource()
                .addValue("id", item.getId())
                .addValue("owner", item.getOwner())
                .addValue("title", item.getTitle())
                .addValue("mediaIds", objectMapper.writeValueAsString(item.getMediaIds()))
                .addValue("description", item.getDescription())
                .addValue("categoryId", item.getCategoryId())
                .addValue("cost", item.getCost())
                .addValue("status", item.getStatus().getValue())
                .addValue("publishedAt", toTimestamp(item.getPublishedAt()))
                .addValue("createdAt", toTimestamp(item.getCreatedAt()));
    }


    @Nullable
    private Instant toInstant(@Nullable Timestamp timestamp) {
        if (timestamp == null) {
            return null;
        }
        return timestamp.toInstant();
    }

    @Nullable
    private Timestamp toTimestamp(@Nullable Instant instant) {
        if (instant == null) {
            return null;
        }
        return Timestamp.from(instant);
    }
}
