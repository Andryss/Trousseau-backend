package ru.andryss.trousseau.repository;

import java.sql.Timestamp;
import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
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
            item.setTitle(rs.getString("title"));
            item.setMediaIds(objectMapper.readValue(rs.getString("media_ids")));
            item.setDescription(rs.getString("description"));
            item.setStatus(ItemStatus.fromValue(rs.getString("status")));
            item.setCreatedAt(rs.getTimestamp("created_at").toInstant());
            return item;
        };
    }

    @Override
    @Transactional
    public ItemEntity save(ItemEntity item) {
        MapSqlParameterSource param = getParameterSource(item);

        jdbcTemplate.update("""
            insert into items(id, title, media_ids, description, status, created_at)
                values(:id, :title, :mediaIds::jsonb, :description, :status, :createdAt)
        """, param);

        return item;
    }

    @Override
    public ItemEntity update(ItemEntity item) {
        MapSqlParameterSource param = getParameterSource(item);

        jdbcTemplate.update("""
            update items set title = :title, media_ids = :mediaIds::jsonb, description = :description, status = :status
                where id = :id
        """, param);

        return item;
    }

    @Override
    @Transactional
    public ItemEntity findById(String id) {
        MapSqlParameterSource param = new MapSqlParameterSource("id", id);
        
        return jdbcTemplate.queryForObject("""
                select * from items where id = :id
        """, param, rowMapper);
    }

    @Override
    public List<ItemEntity> findAll() {
        return jdbcTemplate.query("""
                select * from items order by created_at desc
        """, rowMapper);
    }

    private MapSqlParameterSource getParameterSource(ItemEntity item) {
        MapSqlParameterSource param = new MapSqlParameterSource();
        param.addValue("id", item.getId());
        param.addValue("title", item.getTitle());
        param.addValue("mediaIds", objectMapper.writeValueAsString(item.getMediaIds()));
        param.addValue("description", item.getDescription());
        param.addValue("status", item.getStatus().getValue());
        param.addValue("createdAt", Timestamp.from(item.getCreatedAt()));
        return param;
    }
}
