package ru.andryss.trousseau.repository;

import java.sql.Timestamp;
import java.util.List;

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
        booking.setBookedAt(rs.getTimestamp("booked_at").toInstant());
        return booking;
    };

    @Override
    public List<BookingEntity> findAll() {
        return jdbcTemplate.query("""
                select * from bookings order by booked_at desc
        """, rowMapper);
    }

    @Override
    public void save(BookingEntity booking) {
        jdbcTemplate.update("""
                insert into bookings(id, item_id, booked_at)
                    values(:id, :itemId, :bookedAt)
        """, new MapSqlParameterSource()
                .addValue("id", booking.getId())
                .addValue("itemId", booking.getItemId())
                .addValue("bookedAt", Timestamp.from(booking.getBookedAt())));
    }

    @Override
    public int deleteByItemId(String itemId) {
        return jdbcTemplate.update("""
                delete from bookings where item_id = :itemId
        """, new MapSqlParameterSource()
                .addValue("itemId", itemId));
    }
}
