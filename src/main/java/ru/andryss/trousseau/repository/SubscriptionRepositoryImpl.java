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
import ru.andryss.trousseau.model.SubscriptionEntity;
import ru.andryss.trousseau.model.SubscriptionInfo;
import ru.andryss.trousseau.service.ObjectMapperWrapper;

@Repository
@RequiredArgsConstructor
public class SubscriptionRepositoryImpl implements SubscriptionRepository, InitializingBean {

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final ObjectMapperWrapper objectMapper;

    private RowMapper<SubscriptionEntity> rowMapper;


    @Override
    public void afterPropertiesSet() {
        rowMapper = (rs, rowNum) -> {
            SubscriptionEntity subscription = new SubscriptionEntity();
            subscription.setId(rs.getString("id"));
            subscription.setOwner(rs.getString("owner"));
            subscription.setName(rs.getString("name"));
            subscription.setData(objectMapper.readValue(rs.getString("data"), SubscriptionInfo.class));
            subscription.setCreatedAt(rs.getTimestamp("created_at").toInstant());
            return subscription;
        };
    }

    @Override
    public SubscriptionEntity save(SubscriptionEntity subscription) {
        jdbcTemplate.update("""
                        insert into subscriptions(id, owner, name, data, created_at)
                            values (:id, :owner, :name, :data::jsonb, :createdAt)
                """, getParameterSource(subscription));

        return subscription;
    }

    @Override
    public Optional<SubscriptionEntity> findById(String id) {
        List<SubscriptionEntity> result = jdbcTemplate.query("""
                        select * from subscriptions where id = :id
                """, new MapSqlParameterSource()
                        .addValue("id", id), rowMapper);

        if (result.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(result.get(0));
    }

    @Override
    public Optional<SubscriptionEntity> findByIdAndOwner(String id, String owner) {
        List<SubscriptionEntity> result = jdbcTemplate.query("""
                        select * from subscriptions where id = :id and owner = :owner
                """, new MapSqlParameterSource()
                        .addValue("id", id)
                        .addValue("owner", owner), rowMapper);

        if (result.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(result.get(0));
    }

    @Override
    public SubscriptionEntity update(SubscriptionEntity subscription) {
        jdbcTemplate.update("""
                        update subscriptions
                            set name = :name, data = :data::jsonb, created_at = :createdAt
                            where id = :id
                """, getParameterSource(subscription));

        return subscription;
    }

    @Override
    public List<SubscriptionEntity> findAllByOwnerOrderByCreatedAt(String owner) {
        return jdbcTemplate.query("""
                        select * from subscriptions where owner = :owner order by created_at
                """, new MapSqlParameterSource()
                        .addValue("owner", owner), rowMapper);
    }

    @Override
    public void deleteByIdAndOwner(String id, String owner) {
        jdbcTemplate.update("""
                            delete from subscriptions where id = :id and owner = :owner
                    """, new MapSqlParameterSource()
                            .addValue("id", id)
                            .addValue("owner", owner));
    }

    @Override
    public List<SubscriptionEntity> findAllByCategoryIdsHas(List<String> categoryIds) {
        return jdbcTemplate.query("""
                select *
                from subscriptions s
                where exists(
                    select 1
                    from jsonb_array_elements_text(s.data::jsonb -> 'categoryIds') AS elem1,
                         jsonb_array_elements_text(:categoryIds::jsonb) AS elem2
                    where elem1.value = elem2.value
                )
        """, new MapSqlParameterSource()
                .addValue("categoryIds", objectMapper.writeValueAsString(categoryIds)), rowMapper);
    }

    private MapSqlParameterSource getParameterSource(SubscriptionEntity subscription) {
        return new MapSqlParameterSource()
                .addValue("id", subscription.getId())
                .addValue("owner", subscription.getOwner())
                .addValue("name", subscription.getName())
                .addValue("data", objectMapper.writeValueAsString(subscription.getData()))
                .addValue("createdAt", Timestamp.from(subscription.getCreatedAt()));
    }
}
