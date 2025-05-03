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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class NotificationsApiControllerTest extends BaseApiTest {

    @Autowired
    NotificationService notificationService;

    @Test
    @SneakyThrows
    void getNotificationsTest() {
        createNotification(new NotificationInfo("test-id", "test-title0", "test-content0",
                List.of("test-link00")));
        createNotification(new NotificationInfo("test-id", "test-title1", "test-content1",
                List.of("test-link10", "test-link11")));

        mockMvc.perform(
                        get("/public/notifications")
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpectAll(
                        status().isOk(),
                        content().json("""
                        {
                            "notifications": [
                                {
                                    "id": "20240520_123002000",
                                    "title": "test-title1",
                                    "content": "test-content1",
                                    "links": [
                                        "test-link10",
                                        "test-link11"
                                    ],
                                    "isRead": false,
                                    "timestamp": "2024-05-20T12:30:02Z"
                                },
                                {
                                    "id": "20240520_123001000",
                                    "title": "test-title0",
                                    "content": "test-content0",
                                    "links": [
                                        "test-link00"
                                    ],
                                    "isRead": false,
                                    "timestamp": "2024-05-20T12:30:01Z"
                                }
                            ]
                        }
                        """)
                );
    }

    @Test
    @SneakyThrows
    void getUnreadCountTest() {
        createNotification(new NotificationInfo("test-id", "test-title0", "test-content0",
                List.of("test-link00")));
        createNotification(new NotificationInfo("test-id", "test-title1", "test-content1",
                List.of("test-link10", "test-link11")));
        createNotification(new NotificationInfo("test-id", "test-title2", "test-content2",
                List.of()));

        mockMvc.perform(
                        get("/public/notifications/unread/count")
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpectAll(
                        status().isOk(),
                        content().json("""
                        {
                            "count": 3
                        }
                        """)
                );
    }

    @Test
    @SneakyThrows
    void markNotificationReadTest() {
        createNotification(new NotificationInfo("test-id", "test-title", "test-content",
                List.of("test-link")));

        MvcResult result = mockMvc.perform(
                        get("/public/notifications")
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpectAll(
                        status().isOk(),
                        content().json("""
                        {
                            "notifications": [
                                {
                                    "id": "20240520_123001000",
                                    "title": "test-title",
                                    "content": "test-content",
                                    "links": [
                                        "test-link"
                                    ],
                                    "isRead": false,
                                    "timestamp": "2024-05-20T12:30:01Z"
                                }
                            ]
                        }
                        """)
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
                        content().json("""
                        {
                            "notifications": [
                                {
                                    "id": "20240520_123001000",
                                    "title": "test-title",
                                    "content": "test-content",
                                    "links": [
                                        "test-link"
                                    ],
                                    "isRead": true,
                                    "timestamp": "2024-05-20T12:30:01Z"
                                }
                            ]
                        }
                        """)
                );
    }

    private void createNotification(NotificationInfo info) {
        notificationService.sendNotification(info);
    }
}