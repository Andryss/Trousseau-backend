package ru.andryss.trousseau.repository;

import java.util.List;

import ru.andryss.trousseau.model.BookingEntity;

public interface BookingRepository {
    List<BookingEntity> findAll();
    void save(BookingEntity booking);
    int deleteByItemId(String itemId);
}
