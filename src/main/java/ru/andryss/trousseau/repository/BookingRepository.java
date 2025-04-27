package ru.andryss.trousseau.repository;

import java.util.List;
import java.util.Optional;

import ru.andryss.trousseau.model.BookingEntity;

public interface BookingRepository {
    List<BookingEntity> findAllByUserId(String userId);
    Optional<BookingEntity> findByItemIdAndOwner(String itemId, String owner);
    void save(BookingEntity booking);
    int deleteByItemId(String itemId);
    int deleteByItemIdAndUserId(String itemId, String userId);
}
