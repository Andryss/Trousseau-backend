package ru.andryss.trousseau.controller;

import java.util.List;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import ru.andryss.trousseau.generated.model.NotificationListResponse;
import ru.andryss.trousseau.service.NotificationService;
import ru.andryss.trousseau.service.NotificationService.NotificationInfo;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class NotificationsApiControllerTest extends BaseApiTest {

    @Autowired
    NotificationService notificationService;

    @Test
    @SneakyThrows
    void getNotificationsTest() {
        createNotification(new NotificationInfo("test-title0", "test-content0", List.of("test-link00")));
        createNotification(new NotificationInfo("test-title1", "test-content1", List.of("test-link10", "test-link11")));

        mockMvc.perform(
                        get("/public/notifications")
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.notifications").isArray(),
                        jsonPath("$.notifications.size()").value(2),
                        jsonPath("$.notifications[0].id").isNotEmpty(),
                        jsonPath("$.notifications[0].title").value("test-title1"),
                        jsonPath("$.notifications[0].content").value("test-content1"),
                        jsonPath("$.notifications[0].links").isArray(),
                        jsonPath("$.notifications[0].links.size()").value(2),
                        jsonPath("$.notifications[0].links[0]").value("test-link10"),
                        jsonPath("$.notifications[0].links[1]").value("test-link11"),
                        jsonPath("$.notifications[0].isRead").value(false),
                        jsonPath("$.notifications[0].timestamp").isNotEmpty(),
                        jsonPath("$.notifications[1].id").isNotEmpty(),
                        jsonPath("$.notifications[1].title").value("test-title0"),
                        jsonPath("$.notifications[1].content").value("test-content0"),
                        jsonPath("$.notifications[1].links").isArray(),
                        jsonPath("$.notifications[1].links.size()").value(1),
                        jsonPath("$.notifications[1].links[0]").value("test-link00"),
                        jsonPath("$.notifications[1].isRead").value(false),
                        jsonPath("$.notifications[1].timestamp").isNotEmpty()
                );
    }

    @Test
    @SneakyThrows
    void getUnreadCountTest() {
        createNotification(new NotificationInfo("test-title0", "test-content0", List.of("test-link00")));
        createNotification(new NotificationInfo("test-title1", "test-content1", List.of("test-link10", "test-link11")));
        createNotification(new NotificationInfo("test-title2", "test-content2", List.of()));

        mockMvc.perform(
                        get("/public/notifications/unread/count")
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.count").value(3)
                );
    }

    @Test
    @SneakyThrows
    void markNotificationReadTest() {
        createNotification(new NotificationInfo("test-title", "test-content", List.of("test-link")));

        MvcResult result = mockMvc.perform(
                        get("/public/notifications")
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.notifications").isArray(),
                        jsonPath("$.notifications.size()").value(1),
                        jsonPath("$.notifications[0].id").isNotEmpty(),
                        jsonPath("$.notifications[0].isRead").value(false)
                )
                .andReturn();

        NotificationListResponse response =
                objectMapper.readValue(result.getResponse().getContentAsByteArray(), NotificationListResponse.class);

        String id = response.getNotifications().get(0).getId();

        mockMvc.perform(
                        post("/public/notifications/{notificationId}/read", id)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk());

        mockMvc.perform(
                        get("/public/notifications")
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.notifications").isArray(),
                        jsonPath("$.notifications.size()").value(1),
                        jsonPath("$.notifications[0].id").value(id),
                        jsonPath("$.notifications[0].isRead").value(true)
                );
    }

    private void createNotification(NotificationInfo info) {
        notificationService.sendNotification(info);
    }
}