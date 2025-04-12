package ru.andryss.trousseau.repository;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.andryss.trousseau.model.SessionEntity;
import ru.andryss.trousseau.service.ObjectMapperWrapper;

@Repository
@RequiredArgsConstructor
public class SessionRepositoryImpl implements SessionRepository, InitializingBean {

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final ObjectMapperWrapper objectMapper;

    private RowMapper<SessionEntity> rowMapper;

    @Override
    public void afterPropertiesSet() throws Exception {
        rowMapper = (rs, rowNum) -> {
            SessionEntity session = new SessionEntity();
            session.setId(rs.getString("id"));
            session.setUserId(rs.getString("user_id"));
            session.setMeta(objectMapper.readValue(rs.getString("meta")));
            session.setCreatedAt(rs.getTimestamp("created_at").toInstant());
            return session;
        };
    }

    @Override
    public void save(SessionEntity session) {
        jdbcTemplate.update("""
                insert into sessions(id, user_id, meta, create_at)
                    values (:id, :userId, :meta::jsonb, :createdAt)
        """, new MapSqlParameterSource()
                .addValue("id", session.getId())
                .addValue("userId", session.getUserId())
                .addValue("meta", objectMapper.writeValueAsString(session.getMeta()))
                .addValue("createdAt", Timestamp.from(session.getCreatedAt())));
    }

    @Override
    public Optional<SessionEntity> findById(String id) {
        List<SessionEntity> result = jdbcTemplate.query("""
                select * from sessions where id = :id
        """, new MapSqlParameterSource()
                .addValue("id", id), rowMapper);

        if (result.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(result.get(0));
    }
}
