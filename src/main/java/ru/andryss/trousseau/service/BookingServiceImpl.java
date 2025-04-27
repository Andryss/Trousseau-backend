package ru.andryss.trousseau.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.andryss.trousseau.exception.Errors;
import ru.andryss.trousseau.model.BookingEntity;
import ru.andryss.trousseau.repository.BookingRepository;
import ru.andryss.trousseau.security.UserData;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;

    @Override
    public BookingEntity findByItem(String itemId, UserData user) {
        log.info("Getting booking info for item {} as user {}", itemId, user.getId());

        return bookingRepository.findByItemIdAndOwner(itemId, user.getId())
                .orElseThrow(() -> Errors.bookingNotFound(itemId));
    }
}
