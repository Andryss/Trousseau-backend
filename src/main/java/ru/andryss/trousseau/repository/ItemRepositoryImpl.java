package ru.andryss.trousseau.repository;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.andryss.trousseau.model.ItemEntity;
import ru.andryss.trousseau.model.ItemStatus;
import ru.andryss.trousseau.service.ObjectMapperWrapper;

@Slf4j
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
            item.setTitle(rs.getString("title"));
            item.setMediaIds(objectMapper.readValue(rs.getString("media_ids")));
            item.setDescription(rs.getString("description"));
            item.setCategoryId(rs.getString("category_id"));
            item.setStatus(ItemStatus.fromValue(rs.getString("status")));
            item.setCreatedAt(rs.getTimestamp("created_at").toInstant());
            return item;
        };
    }

    @Override
    @Transactional
    public ItemEntity save(ItemEntity item) {
        jdbcTemplate.update("""
            insert into items(id, title, media_ids, description, category_id, status, created_at)
                values(:id, :title, :mediaIds::jsonb, :description, :categoryId, :status, :createdAt)
        """, getParameterSource(item));

        return item;
    }

    @Override
    public ItemEntity update(ItemEntity item) {
        jdbcTemplate.update("""
            update items
                set title = :title, media_ids = :mediaIds::jsonb, description = :description,
                    category_id = :categoryId, status = :status
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
    public List<ItemEntity> findAllOrderByCreatedAtDesc() {
        return jdbcTemplate.query("""
                select * from items order by created_at desc
        """, rowMapper);
    }

    @Override
    public List<ItemEntity> findAllByStatusOrderByCreatedAtDesc(ItemStatus status) {
        return jdbcTemplate.query("""
                select * from items where status = :status order by created_at desc
        """, new MapSqlParameterSource()
                .addValue("status", status.getValue()), rowMapper);
    }

    @Override
    public List<ItemEntity> findAllByStatusOrderByCreatedAtDesc(ItemStatus status, int limit) {
        return jdbcTemplate.query("""
                select * from items where status = :status order by created_at desc limit :limit
        """, new MapSqlParameterSource()
                .addValue("status", status.getValue())
                .addValue("limit", limit), rowMapper);
    }

    @Override
    public List<ItemEntity> findAllFavourites() {
        return jdbcTemplate.query("""
                select i.id, i.title, i.media_ids, i.description, i.category_id, i.status, i.created_at
                    from items i join favourites f on i.id = f.item_id
                order by f.created_at desc
        """, rowMapper);
    }

    @Override
    public List<ItemEntity> executeQuery(String query) {
        return jdbcTemplate.query(query, rowMapper);
    }

    private MapSqlParameterSource getParameterSource(ItemEntity item) {
        return new MapSqlParameterSource()
                .addValue("id", item.getId())
                .addValue("title", item.getTitle())
                .addValue("mediaIds", objectMapper.writeValueAsString(item.getMediaIds()))
                .addValue("description", item.getDescription())
                .addValue("categoryId", item.getCategoryId())
                .addValue("status", item.getStatus().getValue())
                .addValue("createdAt", Timestamp.from(item.getCreatedAt()));
    }
}
