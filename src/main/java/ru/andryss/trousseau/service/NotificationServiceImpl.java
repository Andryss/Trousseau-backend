package ru.andryss.trousseau.service;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.andryss.trousseau.model.NotificationEntity;
import ru.andryss.trousseau.model.NotificationSettingsEntity;
import ru.andryss.trousseau.repository.NotificationRepository;
import ru.andryss.trousseau.repository.NotificationSettingsRepository;
import ru.andryss.trousseau.security.UserData;
import ru.andryss.trousseau.service.RuntimeMessagingService.MessageInfo;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final RuntimeMessagingService runtimeMessagingService;
    private final NotificationRepository notificationRepository;
    private final NotificationSettingsRepository notificationSettingsRepository;
    private final TimeService timeService;

    private final DateTimeFormatter idFormatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmssSSS");

    @Override
    public List<NotificationEntity> getAll(UserData user) {
        log.info("Getting all notifications as user {}", user.getId());

        return notificationRepository.findAllByReceiverOrderByCreatedAtDesc(user.getId());
    }

    @Override
    public int getUnreadCount(UserData user) {
        log.info("Getting unread notifications count as user {}", user.getId());

        return notificationRepository.countByReceiverAndIsRead(user.getId(), false);
    }

    @Override
    public void markRead(String id, UserData user) {
        log.info("Marking notification {} as user {} as read", id, user.getId());

        notificationRepository.updateByIdAndReceiverSetIsRead(id, user.getId(), true);
    }

    @Override
    public void updateToken(String token, UserData user) {
        Instant now = timeService.now().toInstant();

        NotificationSettingsEntity entity = new NotificationSettingsEntity();
        entity.setUserId(user.getId());
        entity.setToken(token);
        entity.setUpdatedAt(now);
        entity.setCreatedAt(now);

        notificationSettingsRepository.upsert(entity);
    }

    @Override
    public void sendNotification(NotificationInfo info) {
        ZonedDateTime now = timeService.now();

        NotificationEntity entity = new NotificationEntity();
        entity.setId(idFormatter.format(now));
        entity.setReceiver(info.receiver());
        entity.setTitle(info.title());
        entity.setContent(info.content());
        entity.setLinks(info.links());
        entity.setRead(false);
        entity.setCreatedAt(now.toInstant());

        notificationRepository.save(entity);

        runtimeMessagingService.sendMessage(
                new MessageInfo(info.receiver(), info.title(), info.content())
        );
    }
}
