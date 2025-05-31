package ru.andryss.trousseau.controller;

import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import ru.andryss.trousseau.generated.api.NotificationsApi;
import ru.andryss.trousseau.generated.model.NotificationCountResponse;
import ru.andryss.trousseau.generated.model.NotificationDto;
import ru.andryss.trousseau.generated.model.NotificationListResponse;
import ru.andryss.trousseau.generated.model.UpdateNotificationsTokenRequest;
import ru.andryss.trousseau.model.NotificationEntity;
import ru.andryss.trousseau.service.NotificationService;

@RestController
@RequiredArgsConstructor
public class NotificationsApiController extends BaseApiController implements NotificationsApi {

    private final NotificationService notificationService;

    @Override
    public NotificationListResponse getNotifications() {
        List<NotificationEntity> list = notificationService.getAll(getUser());

        List<NotificationDto> dtoList = list.stream()
                .map(entity -> {
                    NotificationDto dto = new NotificationDto();
                    dto.setId(entity.getId());
                    dto.setTitle(entity.getTitle());
                    dto.setContent(entity.getContent());
                    dto.setLinks(entity.getLinks());
                    dto.setIsRead(entity.isRead());
                    dto.setTimestamp(toOffsetDateTime(entity.getCreatedAt()));
                    return dto;
                })
                .toList();

        return new NotificationListResponse()
                .notifications(dtoList);
    }

    @Override
    public NotificationCountResponse getUnreadNotificationsCount() {
        int count = notificationService.getUnreadCount(getUser());

        return new NotificationCountResponse()
                .count(count);
    }

    @Override
    public void markNotificationRead(String notificationId) {
        notificationService.markRead(notificationId, getUser());
    }

    @Override
    public void updateNotificationsToken(UpdateNotificationsTokenRequest request) {
        notificationService.updateToken(request.getToken(), getUser());
    }
}
