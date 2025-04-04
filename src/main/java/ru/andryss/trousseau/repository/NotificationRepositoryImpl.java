package ru.andryss.trousseau.repository;

import java.sql.Timestamp;
import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.andryss.trousseau.model.NotificationEntity;
import ru.andryss.trousseau.service.ObjectMapperWrapper;

@Repository
@RequiredArgsConstructor
public class NotificationRepositoryImpl implements NotificationRepository, InitializingBean {

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final ObjectMapperWrapper objectMapper;

    private RowMapper<NotificationEntity> rowMapper;

    @Override
    public void afterPropertiesSet() {
        rowMapper = (rs, rowNum) -> {
            NotificationEntity notification = new NotificationEntity();
            notification.setId(rs.getString("id"));
            notification.setTitle(rs.getString("title"));
            notification.setContent(rs.getString("content"));
            notification.setLinks(objectMapper.readValue(rs.getString("links")));
            notification.setRead(rs.getBoolean("is_read"));
            notification.setCreatedAt(rs.getTimestamp("created_at").toInstant());
            return notification;
        };
    }

    @Override
    public void save(NotificationEntity entity) {
        jdbcTemplate.update("""
                insert into notifications(id, title, content, links, is_read, created_at)
                    values (:id, :title, :content, :links::jsonb, :isRead, :createdAt)
        """, new MapSqlParameterSource()
                .addValue("id", entity.getId())
                .addValue("title", entity.getTitle())
                .addValue("content", entity.getContent())
                .addValue("links", objectMapper.writeValueAsString(entity.getLinks()))
                .addValue("isRead", entity.isRead())
                .addValue("createdAt", Timestamp.from(entity.getCreatedAt())));
    }

    @Override
    public List<NotificationEntity> findAllOrderByCreatedAtDesc() {
        return jdbcTemplate.query("""
                select * from notifications order by created_at desc
        """, rowMapper);
    }

    @Override
    public int countWithIsRead(boolean isRead) {
        return jdbcTemplate.queryForObject("""
                select count(*) from notifications where is_read = :isRead
        """, new MapSqlParameterSource()
                .addValue("isRead", isRead), Integer.class);
    }

    @Override
    public void updateByIdSetIsRead(String id, boolean isRead) {
        jdbcTemplate.update("""
                update notifications set is_read = :isRead where id = :id
        """, new MapSqlParameterSource()
                .addValue("id", id)
                .addValue("isRead", isRead));
    }
}
