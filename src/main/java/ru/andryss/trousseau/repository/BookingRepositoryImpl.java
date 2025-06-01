package ru.andryss.trousseau.repository;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.andryss.trousseau.model.BookingEntity;

@Repository
@RequiredArgsConstructor
public class BookingRepositoryImpl implements BookingRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    private final RowMapper<BookingEntity> rowMapper = (rs, rowNum) -> {
        BookingEntity booking = new BookingEntity();
        booking.setId(rs.getString("id"));
        booking.setItemId(rs.getString("item_id"));
        booking.setUserId(rs.getString("user_id"));
        booking.setBookedAt(rs.getTimestamp("booked_at").toInstant());
        return booking;
    };

    @Override
    public List<BookingEntity> findAllByUserId(String userId) {
        return jdbcTemplate.query("""
                select * from bookings where user_id = :userId order by booked_at desc
        """, new MapSqlParameterSource()
                .addValue("userId", userId), rowMapper);
    }

    @Override
    public Optional<BookingEntity> findByItemIdAndOwner(String itemId, String owner) {
        List<BookingEntity> result = jdbcTemplate.query("""
                        select b.id, b.user_id, b.item_id, b.booked_at
                        from bookings b join items i on b.item_id = i.id
                        where b.item_id = :itemId and i.owner = :owner
                """, new MapSqlParameterSource()
                        .addValue("itemId", itemId)
                        .addValue("owner", owner), rowMapper);

        if (result.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(result.get(0));
    }

    @Override
    public void save(BookingEntity booking) {
        jdbcTemplate.update("""
                        insert into bookings(id, item_id, user_id, booked_at)
                            values(:id, :itemId, :userId, :bookedAt)
                """, new MapSqlParameterSource()
                        .addValue("id", booking.getId())
                        .addValue("itemId", booking.getItemId())
                        .addValue("userId", booking.getUserId())
                        .addValue("bookedAt", Timestamp.from(booking.getBookedAt())));
    }

    @Override
    public int deleteByItemId(String itemId) {
        return jdbcTemplate.update("""
                        delete from bookings where item_id = :itemId
                """, new MapSqlParameterSource()
                        .addValue("itemId", itemId));
    }

    @Override
    public int deleteByItemIdAndUserId(String itemId, String userId) {
        return jdbcTemplate.update("""
                        delete from bookings where item_id = :itemId and user_id = :userId
                """, new MapSqlParameterSource()
                        .addValue("itemId", itemId)
                        .addValue("userId", userId));
    }
}
