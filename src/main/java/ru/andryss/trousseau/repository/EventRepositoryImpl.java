package ru.andryss.trousseau.repository;

import java.sql.Timestamp;
import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.andryss.trousseau.model.EventEntity;
import ru.andryss.trousseau.model.EventEntity.EventType;
import ru.andryss.trousseau.service.ObjectMapperWrapper;

@Repository
@RequiredArgsConstructor
public class EventRepositoryImpl implements EventRepository, InitializingBean {

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final ObjectMapperWrapper objectMapper;

    private RowMapper<EventEntity> rowMapper;

    @Override
    public void afterPropertiesSet() {
        rowMapper = (rs, rowNum) -> {
            EventEntity event = new EventEntity();
            event.setId(rs.getString("id"));
            event.setType(EventType.valueOf(rs.getString("type")));
            event.setPayload(objectMapper.readValue(rs.getString("payload")));
            event.setCreatedAt(rs.getTimestamp("created_at").toInstant());
            return event;
        };
    }

    @Override
    public void save(EventEntity event) {
        jdbcTemplate.update("""
                insert into events(id, type, payload, created_at)
                    values (:id, :type, :payload::jsonb, :createdAt)
        """, new MapSqlParameterSource()
                .addValue("id", event.getId())
                .addValue("type", event.getType().getValue())
                .addValue("payload", objectMapper.writeValueAsString(event.getPayload()))
                .addValue("createdAt", Timestamp.from(event.getCreatedAt())));
    }

    @Override
    public List<EventEntity> findAllByTypeOrderByCreatedAt(EventType type, int limit) {
        return jdbcTemplate.query("""
                select * from events
                where type = :type
                order by created_at
                limit :limit
        """, new MapSqlParameterSource()
                .addValue("type", type.getValue())
                .addValue("limit", limit), rowMapper);
    }

    @Override
    public void deleteByIds(List<String> ids) {
        if (ids.isEmpty()) {
            return;
        }
        jdbcTemplate.update("""
                delete from events
                where id in (:ids)
        """, new MapSqlParameterSource()
                .addValue("ids", ids));
    }
}
