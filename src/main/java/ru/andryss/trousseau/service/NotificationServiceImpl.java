package ru.andryss.trousseau.service;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.andryss.trousseau.model.NotificationEntity;
import ru.andryss.trousseau.repository.NotificationRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final TimeService timeService;

    private final DateTimeFormatter idFormatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmssSSS");

    @Override
    public List<NotificationEntity> getAll() {
        log.info("Getting all notifications");

        return notificationRepository.findAllOrderByCreatedAtDesc();
    }

    @Override
    public int getUnreadCount() {
        log.info("Getting unread notifications count");

        return notificationRepository.countWithIsRead(false);
    }

    @Override
    public void markRead(String id) {
        log.info("Marking notification with id={} as read", id);

        notificationRepository.updateByIdSetIsRead(id, true);
    }

    @Override
    public void sendNotification(NotificationInfo info) {
        ZonedDateTime now = timeService.now();

        NotificationEntity entity = new NotificationEntity();
        entity.setId(idFormatter.format(now));
        entity.setTitle(info.title());
        entity.setContent(info.content());
        entity.setLinks(info.links());
        entity.setRead(false);
        entity.setCreatedAt(now.toInstant());

        notificationRepository.save(entity);
    }
}
