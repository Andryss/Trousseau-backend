package ru.andryss.trousseau.service;

import ru.andryss.trousseau.model.BookingEntity;
import ru.andryss.trousseau.security.UserData;

public interface BookingService {
    BookingEntity findByItem(String itemId, UserData user);
}
